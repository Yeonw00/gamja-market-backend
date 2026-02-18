package com.gamjamarket.batch

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication
@EntityScan(basePackages = ["com.gamjamarket"])
@EnableJpaRepositories(basePackages = ["com.gamjamarket"])
@ComponentScan(basePackages = ["com.gamjamarket"])
class MarketBatchApplication {}

fun main(args: Array<String>) {
    runApplication<MarketBatchApplication>(*args)
}
