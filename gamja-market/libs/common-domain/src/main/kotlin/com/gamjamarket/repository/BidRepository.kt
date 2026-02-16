package com.gamjamarket.repository

import com.gamjamarket.domain.Bid
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface BidRepository : JpaRepository<Bid, Long> {
    // 특정 경매의 입찰 기록을 금액 높은 순으로 조회
    fun findAllByAuctionIdOrderByBidPriceDesc(auctionId: Long): List<Bid>

    // 특정 경매의 최신 입찰(현재 최고가) 한 건 조회
    fun findFirstByAuctionIdOrderByBidPriceDesc(auctionId: Long): Bid

    // 특정 사용자의 입찰 내역 조회
    fun findAllByBidderId(bidderId: UUID): List<Bid>
}