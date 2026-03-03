package com.gamjamarket.repository

import com.gamjamarket.domain.Auction
import com.gamjamarket.domain.enums.AuctionStatus
import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface AuctionRepository : JpaRepository<Auction, Long> {
    // 상태별 경매 조회 (예: 진행 중인 경매만 보기)
    fun findAllByAuctionStatus(status: AuctionStatus): List<Auction>

    // 경매 시작 처리용 벌크 업데이트 쿼리
    @Modifying(clearAutomatically = true)
    @Query("""
        UPDATE Auction a
        SET a.auctionStatus = :newStatus
        WHERE a.auctionStatus = :oldStatus AND a.startAt <= :now
    """)
    fun updateAuctionStatusToOngoing(
        @Param("oldStatus") oldStatus: AuctionStatus = AuctionStatus.BEFORE_START,
        @Param("newStatus") newStatus: AuctionStatus = AuctionStatus.ON_GOING,
        @Param("now") now: LocalDateTime
    ): Int

    // 특정 시간 이전에 종료되어야 하는데 아직 진행 중인 경매 찾기 (배치 작업용)
    fun findAllByAuctionStatusAndEndAtBefore(status: AuctionStatus, dateTime: LocalDateTime): List<Auction>

    // 비관적 락을 사용하여 경매 엔터티 조회 (동시성 제어용)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Auction a WHERE a.id = :id")
    fun findByIdWIthPessimisticLock(@Param("id") id: Long): Auction?
}