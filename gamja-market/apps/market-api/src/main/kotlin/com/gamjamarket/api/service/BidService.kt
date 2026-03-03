package com.gamjamarket.api.service

import com.gamjamarket.api.dto.response.BidHistoryResponse
import com.gamjamarket.api.dto.response.BidResponse
import com.gamjamarket.domain.Bid
import com.gamjamarket.repository.AuctionRepository
import com.gamjamarket.repository.BidRepository
import com.gamjamarket.repository.UserRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.script.DefaultRedisScript
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.UUID

@Service
class BidService(
    private val redisTemplate: RedisTemplate<String, String>,
    private val auctionRepository: AuctionRepository,
    private val bidRepository: BidRepository,
    private val userRepository: UserRepository
) {
    @Transactional
    fun placeBid(auctionId: Long, bidderId: UUID, bidPrice: Long): BidResponse {
        val highestBidKey = "auction:$auctionId:highest_bid"

        val cachedHighestPriceStr = redisTemplate.opsForValue().get(highestBidKey)
        if (cachedHighestPriceStr != null) {
            val cachedHighestPrice = cachedHighestPriceStr.toLong()
            if (bidPrice <= cachedHighestPrice) {
                throw IllegalArgumentException("현재 최고 입찰가(${cachedHighestPrice}원) 보다 높은 금액을 제시해야 합니다.")
            }
        }

        val auction = auctionRepository.findByIdWIthPessimisticLock(auctionId)
            ?: throw IllegalArgumentException("경매를 찾을 수 없습니다.")

        if (auction.item.seller.id == bidderId) {
            throw IllegalArgumentException("자신의 상품에는 입찰할 수 없습니다.")
        }

        val now = LocalDateTime.now()
        if (auction.endAt.isBefore(now)) {
            throw IllegalStateException("이미 종료된 경매입니다.")
        }

        if (bidPrice < auction.startPrice) {
            throw IllegalArgumentException("입찰 금액은 시작가(${auction.startPrice}원) 이상이어야 합니다.")
        }

        val highestBid = bidRepository.findTopByAuctionIdOrderByBidPriceDesc(auctionId)
        val actualHighestPrice = highestBid?.bidPrice ?: auction.startPrice

        if (bidPrice <= actualHighestPrice) {
            throw IllegalArgumentException("현재 최고 입찰가(${actualHighestPrice}원)보다 높은 금액을 제시해야 합니다.")
        }

        val bidderProxy = userRepository.getReferenceById(bidderId)

        val  newBid = Bid(
            auction = auction,
            bidder = bidderProxy,
            bidPrice = bidPrice
        )

        bidRepository.save(newBid)

        redisTemplate.opsForValue().set(highestBidKey, bidPrice.toString())

        return BidResponse(
            currentHighestPrice = newBid.bidPrice,
            bidTime = newBid.createdAt ?: LocalDateTime.now() // BaseTimeEntity 활용
        )
    }

    @Transactional(readOnly = true)
    fun getBidHistory(auctionId: Long, pageable: Pageable): Page<BidHistoryResponse> {

        if (!auctionRepository.existsById(auctionId)) {
            throw IllegalArgumentException("존재하지 않는 경매입니다.")
        }

        val bidPage = bidRepository.findByAuctionId(auctionId, pageable)

        return bidPage.map { bid ->
            BidHistoryResponse(
                bidId = bid.id!!,
                bidderId = bid.bidder.id,
                bidderName = bid.bidder.nickname,
                bidPrice = bid.bidPrice,
                bidTime = bid.createdAt ?: LocalDateTime.now()
            )
        }
    }
}