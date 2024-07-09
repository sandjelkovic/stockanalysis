package com.sandjelkovic.stockanalysis

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class StockAnalysisApplication

fun main(args: Array<String>) {
    runApplication<StockAnalysisApplication>(*args)
}
