package com.gamjamarket.domain

import com.gamjamarket.domain.common.BaseTimeEntity
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "auctions")
class Auction(
    @Id
    @Column(name = "item_id")
    val id: Long? = null,

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    val item: Item,

    @Column(nullable = false)
    var startPrice: Long,

    @Column
    var buyNowPrice: Long? = null, // 즉시 구매가 (null이면 즉시 구매 불가)

    @Column(nullable = false)
    var startAt: LocalDateTime,

    @Column(nullable = false)
    var endAt: LocalDateTime,

    @Enumerated(EnumType.STRING)
    @Column(name = "auction_status", nullable = false)
    var auctionStatus: AuctionStatus = AuctionStatus.BEFORE_START
) : BaseTimeEntity() {
    fun getEffectiveStatus(): AuctionStatus {
        val now = LocalDateTime.now()

        if (auctionStatus == AuctionStatus.BID_COMPLETED ||
            auctionStatus == AuctionStatus.CANCELLED ||
            auctionStatus == AuctionStatus.END_WITHOUT_BID) {
            return auctionStatus
        }

        return when {
            now.isBefore(startAt) -> AuctionStatus.BEFORE_START
            now.isAfter(endAt) -> AuctionStatus.END_WITHOUT_BID
            else -> AuctionStatus.ON_GOING
        }
    }
}

enum class AuctionStatus(val description: String) {
    BEFORE_START("경매 시작 전"),
    ON_GOING("경매 진행 중"),
    BID_COMPLETED("낙찰 완료"),
    END_WITHOUT_BID("유찰 종료"),
    CANCELLED("경매 취소")
}
