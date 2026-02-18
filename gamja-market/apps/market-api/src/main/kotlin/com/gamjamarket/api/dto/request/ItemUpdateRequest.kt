package com.gamjamarket.api.dto.request

import com.gamjamarket.domain.ItemCondition

data class ItemUpdateRequest(
    val title: String?,
    val content: String?,
    val condition: ItemCondition?,
    val categoryId: Long?,
    val imageUrls: List<String>?
)