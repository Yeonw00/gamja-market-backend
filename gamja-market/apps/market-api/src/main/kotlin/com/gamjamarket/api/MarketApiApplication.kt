package com.gamjamarket.api

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication
@ComponentScan(basePackages = ["com.gamjamarket"])
@EntityScan(basePackages = ["com.gamjamarket.domain"])
@EnableJpaRepositories(basePackages = ["com.gamjamarket.repository"])
class MarketApiApplication {}

fun main(args: Array<String>) {
    runApplication<MarketApiApplication>(*args)
}
