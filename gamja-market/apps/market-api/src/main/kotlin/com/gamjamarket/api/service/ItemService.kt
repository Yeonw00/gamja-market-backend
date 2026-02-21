package com.gamjamarket.api.service

import com.gamjamarket.api.dto.request.ItemCreateRequest
import com.gamjamarket.api.dto.request.ItemUpdateRequest
import com.gamjamarket.api.dto.response.ItemCreateResponse
import com.gamjamarket.api.dto.response.ItemDetailResponse
import com.gamjamarket.api.dto.response.ItemSummaryResponse
import com.gamjamarket.domain.Auction
import com.gamjamarket.domain.AuctionStatus
import com.gamjamarket.domain.Item
import com.gamjamarket.domain.ItemImage
import com.gamjamarket.repository.AuctionRepository
import com.gamjamarket.repository.BidRepository
import com.gamjamarket.repository.CategoryRepository
import com.gamjamarket.repository.ItemImageRepository
import com.gamjamarket.repository.ItemRepository
import com.gamjamarket.repository.UserRepository
import jakarta.transaction.Transactional
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.UUID

@Service
@Transactional
class ItemService(
    private val itemRepository: ItemRepository,
    private val auctionRepository: AuctionRepository,
    private val userRepository: UserRepository,
    private val itemImageRepository: ItemImageRepository,
    private val categoryRepository: CategoryRepository,
    private val bidRepository: BidRepository,
    private val redisTemplate: StringRedisTemplate
) {
    fun createItem(sellerId: UUID, request: ItemCreateRequest) : ItemCreateResponse {
        // 1. 판매자, 카테고리 조회
        val seller = userRepository.findById(sellerId)
            .orElseThrow { IllegalArgumentException("사용자를 찾을 수 없습니다.") }

        val category = categoryRepository.findById(request.categoryId)
            .orElseThrow { IllegalArgumentException("존재하지 않는 카테고리입니다.") }

        val now = LocalDateTime.now()
        val actualStartAt = request.startAt ?: now

        val initialStatus = if (actualStartAt.isAfter(now)) {
            AuctionStatus.BEFORE_START
        } else {
            AuctionStatus.ON_GOING
        }

        // 2. Item 저장
        val item = itemRepository.save(
            Item(
                seller = seller,
                category = category,
                title = request.title,
                content = request.content,
                condition = request.condition
            )
        )

        // 3. auction 정보 저장
        val auction = auctionRepository.save(
            Auction(
                item = item,
                startPrice = request.startPrice,
                buyNowPrice = request.buyNowPrice,
                startAt = actualStartAt,
                endAt = request.endAt,
                auctionStatus = initialStatus,
            )
        )

        // 4. 이미지 정보 저장
        request.imageUrls.forEachIndexed { index, url ->
            itemImageRepository.save(
                ItemImage(
                    item = item,
                    imageUrl = url,
                    sortOrder = index,
                    isThumbnail = index == 0
                )
            )
        }
        return ItemCreateResponse(
            id = item.id ?: throw IllegalStateException("상품 ID 생성 실패"),
            title = item.title,
            sellerId = seller.id,
            auctionStatus = auction.auctionStatus,
            endAt = auction.endAt,
            createdAt = item.createdAt ?: LocalDateTime.now()
        )
    }

    @Transactional
    fun getItems(pageable: Pageable): Page<ItemSummaryResponse> {
        return itemRepository.findAll(pageable).map { item ->
            val auction = item.auction ?: throw IllegalStateException("경매 정보가 없는 상품입니다.")

            ItemSummaryResponse(
                id = item.id ?: throw IllegalStateException("상품 ID 생성 실패"),
                title = item.title,
                startPrice = auction.startPrice,
                thumbnailImageUrl = null, // TODO: 이미지 테이블에서 첫 번째 이미지 가져오기
                auctionStatus = auction.getEffectiveStatus(),
                viewCount = item.viewCount,
                createdAt = item.createdAt!!
            )
        }
    }

    @Transactional
    fun getItemDetail(itemId: Long): ItemDetailResponse {
        val viewCountKey = "item:view_count:$itemId"
        redisTemplate.opsForValue().increment(viewCountKey)

        val item = itemRepository.findById(itemId)
            .orElseThrow { IllegalArgumentException("해당 상품을 찾을 수 없습니다. (ID: $itemId)") }

        val auction = item.auction ?: throw IllegalStateException("경매 정보가 없는 상품입니다.")

        val currentRedisView = redisTemplate.opsForValue().get(viewCountKey)?.toInt() ?: 0

        val response = ItemDetailResponse.from(item)

        return response.copy(
            viewCount = item.viewCount + currentRedisView
        )
    }

    @Transactional
    fun updateItem(itemId: Long, sellerId: UUID, request: ItemUpdateRequest): ItemDetailResponse {
        val item = itemRepository.findById(itemId)
            .orElseThrow { IllegalArgumentException("상품을 찾을 수 없습니다.") }

        // 1. 권한 검증
        if (item.seller.id != sellerId) {
            throw IllegalArgumentException("수정 권한이 없습니다.")
        }

        // 2. 경매 및 입찰 상태 검증
        item.auction?.let { auction ->
            if (bidRepository.existsByAuctionId(auction.id!!)) {
                throw IllegalStateException("이미 입찰이 진행된 상품은 수정할 수 없습니다.")
            }

            // 경매 종료 시간이 지났다면 수정 금지
            if (auction.endAt.isBefore(LocalDateTime.now())) {
                throw IllegalStateException("이미 종료된 경매는 수정할 수 없습니다.")
            }
        }

        // 3. 카테고리 업데이트가 필요한 경우에만 조회
        val newCategory = request.categoryId?.let { categoryId ->
            categoryRepository.findById(categoryId)
                .orElseThrow { IllegalArgumentException("존재하지 않는 카테고리입니다.") }
        }

        item.update(
            newTitle = request.title,
            newContent = request.content,
            newCondition = request.condition,
            newCategory = newCategory,
            newImageUrls = request.imageUrls
        )

        return ItemDetailResponse.from(item)
    }

    @Transactional
    fun deleteItem(itemId: Long, sellerId: UUID) {
        val item = itemRepository.findById(itemId)
            .orElseThrow { IllegalArgumentException("상품을 찾을 수 없습니다.") }

        if (item.seller.id != sellerId) {
            throw IllegalArgumentException("삭제 권한이 없습니다.")
        }

        item.auction?.let { auction ->
            if (bidRepository.existsByAuctionId(auction.id!!)) {
                throw IllegalStateException("이미 입찰이 진행된 상품은 삭제할 수 없습니다.")
            }
        }

        itemRepository.delete(item)
    }
}