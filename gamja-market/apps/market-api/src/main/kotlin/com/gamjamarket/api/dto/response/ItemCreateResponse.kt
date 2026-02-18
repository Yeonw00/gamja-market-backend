package com.gamjamarket.api.dto.response

import com.gamjamarket.domain.AuctionStatus
import java.time.LocalDateTime
import java.util.UUID

data class ItemCreateResponse (
    val id: Long,
    val title: String,
    val sellerId: UUID,
    val auctionStatus: AuctionStatus,
    val endAt: LocalDateTime,
    val createdAt: LocalDateTime
)