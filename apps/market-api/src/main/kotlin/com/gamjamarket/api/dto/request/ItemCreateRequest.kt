package com.gamjamarket.api.dto.request

import com.gamjamarket.domain.enums.ItemCondition
import jakarta.validation.constraints.FutureOrPresent
import jakarta.validation.constraints.Future
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive
import java.time.LocalDateTime

data class ItemCreateRequest(
    @field:NotBlank
    val title: String,

    @field:NotBlank
    val content: String,

    @field:Positive
    val categoryId: Long,

    val condition: ItemCondition,

    @field:Positive
    val startPrice: Long,

    @field:Positive
    val buyNowPrice: Long?,

    @field:FutureOrPresent
    val startAt: LocalDateTime,

    @field:Future
    val endAt: LocalDateTime,

    val imageUrls: List<String> // 이미지 URL 리스트
)
