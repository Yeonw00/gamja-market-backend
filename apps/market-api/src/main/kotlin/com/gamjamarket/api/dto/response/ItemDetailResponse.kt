package com.gamjamarket.api.dto.response

import com.gamjamarket.domain.Item
import com.gamjamarket.domain.enums.AuctionStatus
import com.gamjamarket.domain.enums.ItemCondition
import com.gamjamarket.utils.exception.BusinessException
import com.gamjamarket.utils.response.ResultCode
import java.time.LocalDateTime

data class ItemDetailResponse(
    val id: Long,
    val title: String,
    val content: String,
    val condition: ItemCondition,
    val categoryName: String,
    val sellerNickname: String,
    val startPrice: Long,
    val buyNowPrice: Long?,
    val auctionStatus: AuctionStatus,
    val startAt: LocalDateTime,
    val endAt: LocalDateTime,
    val viewCount: Int,
    val imageUrls: List<String>,
    val createdAt: LocalDateTime
) {
    companion object {
        fun from(item: Item): ItemDetailResponse {
            val auction = item.auction ?: throw BusinessException(ResultCode.AUCTION_NO_INFO)

            return ItemDetailResponse(
                id = item.id!!,
                title = item.title,
                content = item.content,
                condition = item.condition,
                categoryName = item.category.name,
                sellerNickname = item.seller.nickname,
                viewCount = item.viewCount,
                imageUrls = item.images.map {it.imageUrl},
                createdAt = item.createdAt!!,
                startPrice = auction.startPrice,
                buyNowPrice = auction.buyNowPrice,
                auctionStatus = auction.auctionStatus,
                startAt = auction.startAt,
                endAt = auction.endAt,
            )
        }
    }
}