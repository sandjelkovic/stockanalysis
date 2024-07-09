package com.sandjelkovic.stockanalysis

import org.springframework.boot.fromApplication
import org.springframework.boot.with


fun main(args: Array<String>) {
    fromApplication<StockAnalysisApplication>().with(TestcontainersConfiguration::class).run(*args)
}
