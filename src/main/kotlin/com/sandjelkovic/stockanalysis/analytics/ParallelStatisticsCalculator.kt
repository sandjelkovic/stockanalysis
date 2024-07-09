package com.sandjelkovic.stockanalysis.analytics

import java.util.stream.Collectors

class ParallelStatisticsCalculator : StatisticsCalculator {
    override fun summarise(values: List<Double>): SummaryStatistics {
        if (values.isEmpty()) return SummaryStatistics()

        val stats = values.parallelStream()
            .collect(Collectors.summarizingDouble(Double::toDouble))
        return SummaryStatistics(
            min = stats.min,
            max = stats.max,
            last = values.lastOrNull() ?: 0.0,
            average = stats.average,
            variance = variance(values, stats.average)
        )
    }

    // should be further optimised with a different processor that can be configured for given number of threads
    // since parallelStream will use ForkJoinPull and all available threads/cores by default
    private fun variance(values: List<Double>, avgVal: Double): Double =
        values.parallelStream()
            .mapToDouble { it - avgVal }
            .map { it * it }
            .average().asDouble
}