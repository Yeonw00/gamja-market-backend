package com.gamjamarket.domain

import com.gamjamarket.domain.common.BaseTimeEntity
import jakarta.persistence.*
import java.util.UUID

@Entity
@Table(name = "bids")
class Bid(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auction_id", nullable = false)
    val auction: Auction,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bidder_id", nullable = false) // USERS 참조
    val bidder: User,

    @Column(nullable = false)
    val bidPrice: Long,

    // 입찰 시간은 BaseTimeEntity의 createdAt을 활용
) : BaseTimeEntity()