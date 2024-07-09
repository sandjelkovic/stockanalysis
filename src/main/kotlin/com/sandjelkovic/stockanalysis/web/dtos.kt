package com.sandjelkovic.stockanalysis.web

data class DataPointDto(
    val symbol: String,
    val value: Double,
)

data class BatchDataDto(
    val symbol: String,
    val values: List<Double>,
)

data class StatsResponseDto(
    val min: Double,
    val max: Double,
    val last: Double,
    val avg: Double,
    val variance: Double,
)