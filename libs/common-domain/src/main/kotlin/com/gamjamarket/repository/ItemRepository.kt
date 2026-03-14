package com.gamjamarket.repository

import com.gamjamarket.domain.Item
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface ItemRepository : JpaRepository<Item, Long> {
    fun findAllBySellerId(sellerId: UUID): List<Item>

    @Modifying
    @Query("UPDATE Item i SET i.viewCount = i.viewCount + :count WHERE i.id = :itemId")
    fun updateViewCount(itemId: Long, count: Int): Int

    // 목록 조회용: Item + Auction + Seller를 한 번에 로딩 (N+1 방지)
    @Query(
        "SELECT i FROM Item i JOIN FETCH i.auction JOIN FETCH i.seller",
        countQuery = "SELECT count(i) FROM Item i"
    )
    fun findAllWithAuction(pageable: Pageable): Page<Item>

    // 상세 조회용: 모든 연관 엔티티를 한 번에 로딩 (N+1 방지)
    @Query("""
        SELECT i FROM Item i
        JOIN FETCH i.auction
        JOIN FETCH i.seller
        LEFT JOIN FETCH i.category
        LEFT JOIN FETCH i.images
        WHERE i.id = :id
    """)
    fun findByIdWithDetails(@Param("id") id: Long): Item?
}