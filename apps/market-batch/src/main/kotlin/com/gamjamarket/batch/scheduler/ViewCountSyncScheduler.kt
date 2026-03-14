package com.gamjamarket.batch.scheduler

import com.gamjamarket.repository.ItemRepository
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.ScanOptions
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class ViewCountSyncScheduler(
    private val itemRepository: ItemRepository,
    private val redisTemplate: StringRedisTemplate
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    fun syncViewCountToDB() {
        val scanOptions = ScanOptions.scanOptions()
            .match("item:view_count:*")
            .count(100)
            .build()

        var syncCount = 0

        redisTemplate.scan(scanOptions).use { cursor ->
            cursor.forEach { key ->
                val itemId = key.substringAfterLast(":").toLongOrNull() ?: return@forEach
                val count = redisTemplate.opsForValue().getAndDelete(key)?.toLong() ?: return@forEach

                if (count > 0) {
                    itemRepository.updateViewCount(itemId, count.toInt())
                    syncCount++
                }
            }
        }

        if (syncCount > 0) {
            logger.info("조회수 동기화 완료: {}개 상품", syncCount)
        }
    }
}