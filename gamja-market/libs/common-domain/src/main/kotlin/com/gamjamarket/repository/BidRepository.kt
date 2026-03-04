package com.gamjamarket.repository

import com.gamjamarket.domain.Bid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface BidRepository : JpaRepository<Bid, Long> {
    // 특정 경매의 입찰 기록을 조회
    fun findByAuctionId(auctionId: Long, pageable: Pageable): Page<Bid>

    // 특정 사용자의 입찰 내역 조회
    fun findAllByBidderId(bidderId: UUID): List<Bid>

    // 해당 경매 ID를 가진 입찰이 존재하는지 여부만 확인 (count보다 빠름)
    fun existsByAuctionId(auctionId: Long): Boolean

    // 스케줄러 용
    @Query("SELECT DISTINCT b.auction.id FROM Bid b WHERE b.auction.id IN :auctionIds")
    fun findAuctionIdsWithBidsIn(@Param("auctionIds")auctionIds: List<Long>): List<Long>


    // 알림 발송용
    @Query("SELECT DISTINCT b.bidder.id FROM Bid b WHERE b.auction.id = :auctionId")
    fun findDistinctBidderIdsByAuctionId(@Param("auctionId")auctionId: Long): List<UUID>

    // 특정 경매의 입찰 내역 중 입찰가가 가장 높은 1건을 조회
    fun findTopByAuctionIdOrderByBidPriceDesc(auctionId: Long): Bid?
}