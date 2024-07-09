package com.sandjelkovic.stockanalysis.web

import com.sandjelkovic.stockanalysis.analytics.ParallelStatisticsCalculator
import com.sandjelkovic.stockanalysis.analytics.SequentialStatisticsCalculator
import com.sandjelkovic.stockanalysis.analytics.StatisticsCalculator
import com.sandjelkovic.stockanalysis.analytics.SummaryStatistics
import com.sandjelkovic.stockanalysis.data.DataPointRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import kotlin.math.pow

@RestController
class StockAnalysisController(
    val dataPointRepository: DataPointRepository,
    val sequentialStatisticsCalculator: SequentialStatisticsCalculator,
    val parallelStatisticsCalculator: ParallelStatisticsCalculator,
) {

    @PostMapping("/add")
    fun addDataPoint(@RequestBody dataPoint: DataPointDto): ResponseEntity<Unit> {
        dataPointRepository.addDataPoint(dataPoint.symbol, dataPoint.value)
        return ResponseEntity.ok().build()
    }

    @PostMapping("/add_batch")
    fun addBatchData(@RequestBody batchDataDto: BatchDataDto): ResponseEntity<Unit> {
        dataPointRepository.addBatchData(batchDataDto.symbol, batchDataDto.values)
        return ResponseEntity.ok().build()
    }

    @GetMapping("/stats")
    fun getStats(@RequestParam symbol: String, @RequestParam k: Int): ResponseEntity<StatsResponseDto> {
        if (symbol.isBlank()) return ResponseEntity.badRequest().build()
        if (k !in 1..7) return ResponseEntity.badRequest().build()
        val count = tenToThePower(k)

        val values = dataPointRepository.getStats(symbol, count)

        if (values.isEmpty()) {
            return ResponseEntity.notFound().build()
        }

        return getStatisticsCalculator(count)
            .summarise(values)
            .let(::summaryToStatsResponseDto)
            .let { ResponseEntity.ok(it) }
    }

    private fun getStatisticsCalculator(count: Int): StatisticsCalculator =
        if (count > 10_000) parallelStatisticsCalculator
        else sequentialStatisticsCalculator

    private fun summaryToStatsResponseDto(summary: SummaryStatistics) =
        StatsResponseDto(
            min = summary.min,
            max = summary.max,
            last = summary.last,
            avg = summary.average,
            variance = summary.variance
        )

    // can be omitted, but it can be a slight potential performance improvement if under heavy load
    private fun tenToThePower(k: Int) =
        when (k) {
            1 -> 10
            2 -> 100
            3 -> 1000
            4 -> 10000
            5 -> 100000
            6 -> 1000000
            7 -> 10000000
            else -> 10.0.pow(k.toDouble()).toInt()
        }
}
