package com.gamjamarket.repository

import com.gamjamarket.domain.Item
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface ItemRepository : JpaRepository<Item, Long> {
    fun findAllBySellerId(sellerId: UUID): List<Item>

    @Modifying
    @Query("UPDATE Item i SET i.viewCount = i.viewCount + :count WHERE i.id = :itemId")
    fun updateViewCount(itemId: Long, count: Int): Int
}