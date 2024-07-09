package com.sandjelkovic.stockanalysis

import com.sandjelkovic.stockanalysis.analytics.ParallelStatisticsCalculator
import com.sandjelkovic.stockanalysis.analytics.SequentialStatisticsCalculator
import com.sandjelkovic.stockanalysis.analytics.SummaryStatistics
import org.openjdk.jmh.annotations.*
import java.util.concurrent.TimeUnit
import kotlin.random.Random

@State(Scope.Benchmark)
@Fork(value = 2, warmups = 1)
@Warmup(iterations = 1, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 2, time = 1, timeUnit = TimeUnit.SECONDS)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
open class SequentialOrParallelBenchmarkTest {

    @Param("10", "100", "1000", "10000", "100000", "1000000", "10000000")
    var size: Int = 0

    private lateinit var values: List<Double>

    private val sequentialStatisticsCalculator = SequentialStatisticsCalculator()
    private val parallelStatisticsCalculator = ParallelStatisticsCalculator()

    @Setup
    fun setUp() {
        values = generateSequence { Random.nextDouble() }
            .take(size)
            .toList()
    }

    @Benchmark
    fun calculateSummarySequential(): SummaryStatistics {
        return sequentialStatisticsCalculator.summarise(values)
    }

    @Benchmark
    fun calculateSummaryParallel(): SummaryStatistics {
        return parallelStatisticsCalculator.summarise(values)
    }
}
