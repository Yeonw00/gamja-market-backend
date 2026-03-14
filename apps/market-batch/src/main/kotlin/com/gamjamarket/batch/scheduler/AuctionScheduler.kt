package com.gamjamarket.batch.scheduler

import com.gamjamarket.domain.enums.AuctionStatus
import com.gamjamarket.repository.AuctionRepository
import com.gamjamarket.repository.BidRepository
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Component
class AuctionScheduler(
    private val auctionRepository: AuctionRepository,
    private val bidRepository: BidRepository,
) {
    private val log = LoggerFactory.getLogger(this::class.java)

    // 경매 시작 처리 스케줄러
    @Transactional
    @Scheduled(cron = "*/10 * * * * *")
    fun processStartAuctions() {
        val now = LocalDateTime.now()

        try {
            val updatedCount = auctionRepository.updateAuctionStatusToOngoing(now = now)

            if (updatedCount > 0) {
                log.info("[AuctionScheduler] 경매 시작 처리 완료: {}건 업데이트 (기준 시간: {})", updatedCount, now)
            } else {
                // 업데이트된 내역이 없을 때 (조용히 넘어감)
                log.debug("새로 시작할 경매가 없습니다.")
            }
        } catch (e: Exception) {
            log.error("[AuctionScheduler] 경매 시작 처리 중 오류 발생", e)
        }
    }

    // 경매 종료 처리 스케줄러
    @Transactional
    @Scheduled(cron = "*/10 * * * * *")
    fun processEndAuctions() {
        val now = LocalDateTime.now()

        // 1. 종료 시간이 지났지만 아직 '진행 중'인 경매 엔티티들을 조회
        val targetAuctions = auctionRepository.findAllByAuctionStatusAndEndAtBefore(
            status = AuctionStatus.ON_GOING,
            dateTime = now
        )

        if (targetAuctions.isEmpty()) {
            return
        }

        // 2. 조회된 경매들의 ID만 리스트로 추출
        val auctionIds = targetAuctions.mapNotNull { it.id }

        // 3. 쿼리 단 한번으로 입찰이 존재하는 경매 ID 목록을 가져와 빠른 검색을 위해 Set으로 변환
        val auctionedIdsWithBids = bidRepository.findAuctionIdsWithBidsIn(auctionIds).toSet()

        // 4. 조회된 경매들을 순회하며 종료 비즈니스 로직 처리
        for (auction in targetAuctions) {
            val bidExist = auctionedIdsWithBids.contains(auction.id)

            if (bidExist) {
                auction.auctionStatus = AuctionStatus.BID_COMPLETED
                log.info("경매 [ID: {}] 낙찰 완료 (BID_COMPLETED) 처리", auction.id)
                // TODO: 알림 발송 이벤트 호출
            } else {
                auction.auctionStatus = AuctionStatus.END_WITHOUT_BID
                log.info("경매 [ID: {}] 유찰 종료 (END_WITHOUT_BID) 처리", auction.id)
            }
        }
        log.info("[AuctionScheduler] 경매 종료 처리 완료: 총 {}건", targetAuctions.size)
    }
}