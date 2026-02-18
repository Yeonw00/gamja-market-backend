package com.gamjamarket.api.dto.response

import com.gamjamarket.domain.AuctionStatus
import java.time.LocalDateTime

data class ItemSummaryResponse(
    val id: Long,
    val title: String,
    val startPrice: Long,
    val thumbnailImageUrl: String?,
    val auctionStatus: AuctionStatus,
    val viewCount: Int,
    val createdAt: LocalDateTime
)