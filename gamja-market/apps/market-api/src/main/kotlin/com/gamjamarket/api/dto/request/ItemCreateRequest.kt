package com.gamjamarket.api.dto.request

import com.gamjamarket.domain.ItemCondition
import java.time.LocalDateTime

data class ItemCreateRequest(
    val title: String,
    val content: String,
    val categoryId: Long,
    val condition: ItemCondition,
    val startPrice: Long,
    val buyNowPrice: Long?,
    val startAt: LocalDateTime,
    val endAt: LocalDateTime,
    val imageUrls: List<String> // 이미지 URL 리스트
)
