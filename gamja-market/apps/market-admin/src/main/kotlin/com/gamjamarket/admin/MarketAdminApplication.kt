package com.gamjamarket.admin

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class MarketAdminApplication {}

fun main(args: Array<String>) {
    runApplication<MarketAdminApplication>(*args)
}
