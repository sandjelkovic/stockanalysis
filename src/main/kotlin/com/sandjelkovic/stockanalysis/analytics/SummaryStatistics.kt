package com.sandjelkovic.stockanalysis.analytics

data class SummaryStatistics(
    val min: Double = 0.0,
    val max: Double = 0.0,
    val last: Double = 0.0,
    val average: Double = 0.0,
    val variance: Double = 0.0,
)