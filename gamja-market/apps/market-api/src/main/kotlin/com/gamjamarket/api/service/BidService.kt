package com.gamjamarket.api.service

import com.gamjamarket.domain.Bid
import com.gamjamarket.repository.AuctionRepository
import com.gamjamarket.repository.BidRepository
import com.gamjamarket.repository.UserRepository
import jakarta.transaction.Transactional
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.script.DefaultRedisScript
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class BidService(
    private val redisTemplate: RedisTemplate<String, String>,
    private val auctionRepository: AuctionRepository,
    private val bidRepository: BidRepository,
    private val userRepository: UserRepository
) {
    // Lua 스크립트: 원자적(Atomic)으로 최고가 비교 및 업데이트 수행
    private val bidScript = DefaultRedisScript(
        """
        local current_highest = tonumber(redis.call('get', KEYS[1] or '0')
        local new_bid = tonumber(ARGV[1])
        
        if new_bid > current_highest then
            redis.call('set', KEYS[1], new_bid)
            return 1 -- 성공
        else 
            return 0 -- 실패 (입찰가가 낮거나 같음)
        end
        """.trimIndent(),
        Long::class.java // 반환 타입
    )

    @Transactional
    fun placeBid(auctionId: Long, bidderId: UUID, bidPrice: Long) {
        val highesBidKey = "auction:$auctionId:highest_bid"

        // 1. Redis Lua 스크립트 실행 (단일 스레드로 동작하여 동시성 완벽 보장)
        val result = redisTemplate.execute(
            bidScript,
            listOf(highesBidKey), // KEYS[1]
            bidPrice.toString() // ARGV[1]
        )

        // 2. 스크립트 결과 확인
        if (result == 0L) {
            throw IllegalArgumentException("현재 최고 입찰가보다 높은 금액을 제시해야 합니다.")
        }

        val auction = auctionRepository.findById(auctionId)
            .orElseThrow { IllegalArgumentException("경매를 찾을 수 없습니다.") }

        val bidderProxy = userRepository.getReferenceById(bidderId)

        val  newBid = Bid(
            auction = auction,
            bidder = bidderProxy,
            bidPrice = bidPrice
        )

        bidRepository.save(newBid)
    }
}