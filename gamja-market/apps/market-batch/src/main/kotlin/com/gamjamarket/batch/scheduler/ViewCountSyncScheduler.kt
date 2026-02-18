package com.gamjamarket.batch.scheduler

import com.gamjamarket.repository.ItemRepository
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class ViewCountSyncScheduler(
    private val itemRepository: ItemRepository,
    private val redisTemplate: StringRedisTemplate
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    fun syncViewCountToDB() {
        val keys = redisTemplate.keys("item:view_count:*") ?: return
        if (keys.isEmpty()) return

        logger.info("조회수 동기화 시작: ${keys.size} 개의 상품")

        keys.forEach { key ->
            val itemId = key.split(":").last().toLong()
            val count = redisTemplate.opsForValue().get(key)?.toLong() ?: 0L

            if (count > 0) {
                val updatedRows = itemRepository.updateViewCount(itemId, count.toInt())

                if(updatedRows > 0) {
                    redisTemplate.delete(key)
                }
            }
        }
        logger.info("조회수 동기화 완료")
    }
}