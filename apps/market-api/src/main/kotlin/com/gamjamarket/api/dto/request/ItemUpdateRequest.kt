package com.gamjamarket.api.dto.request

import com.gamjamarket.domain.enums.ItemCondition
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size

data class ItemUpdateRequest(
    @field:Size(min = 1)
    val title: String?,

    @field:Size(min = 1)
    val content: String?,

    val condition: ItemCondition?,

    @field:Positive
    val categoryId: Long?,

    val imageUrls: List<String>?
)