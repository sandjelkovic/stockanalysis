package com.sandjelkovic.stockanalysis.analytics

interface StatisticsCalculator {
    fun summarise(values: List<Double>): SummaryStatistics
}
