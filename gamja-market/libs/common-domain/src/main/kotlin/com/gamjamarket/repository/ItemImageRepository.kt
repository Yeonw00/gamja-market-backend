package com.gamjamarket.repository

import com.gamjamarket.domain.ItemImage
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ItemImageRepository : JpaRepository<ItemImage, Long> {
    fun findAllByItemIdOrderBySortOrderAsc(itemId: Long): List<ItemImage>
}