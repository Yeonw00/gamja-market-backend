package com.gamjamarket.api.service

import com.gamjamarket.api.dto.response.BidHistoryResponse
import com.gamjamarket.api.dto.response.BidResponse
import com.gamjamarket.domain.Bid
import com.gamjamarket.repository.AuctionRepository
import com.gamjamarket.repository.BidRepository
import com.gamjamarket.repository.UserRepository
import com.gamjamarket.utils.exception.BusinessException
import com.gamjamarket.utils.response.ResultCode
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.script.DefaultRedisScript
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.support.TransactionSynchronization
import org.springframework.transaction.support.TransactionSynchronizationManager
import java.time.LocalDateTime
import java.util.UUID

@Service
class BidService(
    private val redisTemplate: RedisTemplate<String, String>,
    private val auctionRepository: AuctionRepository,
    private val bidRepository: BidRepository,
    private val userRepository: UserRepository
) {
    // 입찰가가 현재 최고가보다 높으면 Redis를 갱신하고 이전 값을 반환, 아니면 -1 반환
    private val bidFilterScript = DefaultRedisScript<Long>().apply {
        setScriptText("""
            local current = tonumber(redis.call('GET', KEYS[1]) or '0')
            local bid = tonumber(ARGV[1])
            if bid > current then
                redis.call('SET', KEYS[1], ARGV[1])
                return current
            end
            return -1
        """.trimIndent())
        resultType = Long::class.java
    }

    // Redis 값이 지정된 값과 같을 때만 이전 값으로 복원 (CAS 방식 롤백)
    private val bidRollbackScript = DefaultRedisScript<Long>().apply {
        setScriptText("""
            if redis.call('GET', KEYS[1]) == ARGV[1] then
                if tonumber(ARGV[2]) > 0 then
                    redis.call('SET', KEYS[1], ARGV[2])
                else
                    redis.call('DEL', KEYS[1])
                end
            end
            return 0
        """.trimIndent())
        resultType = Long::class.java
    }

    @Transactional
    fun placeBid(auctionId: Long, bidderId: UUID, bidPrice: Long): BidResponse {
        val highestBidKey = "auction:$auctionId:highest_bid"

        // 1. Redis Lua 스크립트로 원자적 비교-설정 (빠른 1차 필터)
        val previousPrice = redisTemplate.execute(
            bidFilterScript, listOf(highestBidKey), bidPrice.toString()
        ) ?: -1L

        if (previousPrice == -1L) {
            throw BusinessException(ResultCode.BID_LOWER_THAN_HIGHEST, "현재 최고 입찰가보다 높은 금액을 제시해야 합니다.")
        }

        // 트랜잭션 커밋 실패 시 Redis 자동 롤백 등록
        TransactionSynchronizationManager.registerSynchronization(object : TransactionSynchronization {
            override fun afterCompletion(status: Int) {
                if (status != TransactionSynchronization.STATUS_COMMITTED) {
                    rollbackRedisPrice(highestBidKey, bidPrice, previousPrice)
                }
            }
        })

        // 2. DB 비관적 락으로 경매 조회
        val auction = auctionRepository.findByIdWithItemAndSellerForUpdate(auctionId)
            ?: throw BusinessException(ResultCode.AUCTION_NOT_FOUND)

        if (auction.item.seller.id == bidderId) {
            throw BusinessException(ResultCode.BID_OWN_ITEM)
        }

        val now = LocalDateTime.now()
        if (auction.endAt.isBefore(now)) {
            throw BusinessException(ResultCode.AUCTION_ALREADY_ENDED)
        }

        if (bidPrice < auction.startPrice) {
            throw BusinessException(ResultCode.BID_LOWER_THAN_START_PRICE, "입찰 금액은 시작가(${auction.startPrice}원) 이상이어야 합니다.")
        }

        // 3. DB에서 실제 최고가 재확인 (최종 정합성 보장)
        val actualHighestPrice = bidRepository.findTopByAuctionIdOrderByBidPriceDesc(auctionId)?.bidPrice
            ?: auction.startPrice

        if (bidPrice <= actualHighestPrice) {
            // Redis를 DB의 실제 최고가로 보정
            redisTemplate.opsForValue().set(highestBidKey, actualHighestPrice.toString())
            throw BusinessException(ResultCode.BID_LOWER_THAN_HIGHEST, "현재 최고 입찰가(${actualHighestPrice}원)보다 높은 금액을 제시해야 합니다.")
        }

        // 4. 입찰 저장 (Redis는 Lua 스크립트에서 이미 갱신됨)
        val bidderProxy = userRepository.getReferenceById(bidderId)
        val newBid = bidRepository.save(
            Bid(auction = auction, bidder = bidderProxy, bidPrice = bidPrice)
        )

        return BidResponse(
            currentHighestPrice = newBid.bidPrice,
            bidTime = newBid.createdAt ?: LocalDateTime.now()
        )
    }

    /**
     * Redis 값이 bidPrice와 같을 때만 previousPrice로 복원 (CAS 방식)
     * 다른 유효한 입찰이 이미 Redis를 갱신한 경우에는 복원하지 않음
     */
    private fun rollbackRedisPrice(key: String, bidPrice: Long, previousPrice: Long) {
        redisTemplate.execute(
            bidRollbackScript, listOf(key), bidPrice.toString(), previousPrice.toString()
        )
    }

    @Transactional(readOnly = true)
    fun getBidHistory(auctionId: Long, pageable: Pageable): Page<BidHistoryResponse> {

        if (!auctionRepository.existsById(auctionId)) {
            throw BusinessException(ResultCode.AUCTION_NOT_FOUND)
        }

        val bidPage = bidRepository.findByAuctionIdWithBidder(auctionId, pageable)

        return bidPage.map { bid ->
            BidHistoryResponse(
                bidId = bid.id!!,
                bidderId = bid.bidder.id,
                bidderName = bid.bidder.nickname,
                bidPrice = bid.bidPrice,
                bidTime = bid.createdAt ?: LocalDateTime.now()
            )
        }
    }
}
