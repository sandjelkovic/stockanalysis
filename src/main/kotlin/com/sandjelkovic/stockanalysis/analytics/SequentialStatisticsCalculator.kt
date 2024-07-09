package com.sandjelkovic.stockanalysis.analytics

class SequentialStatisticsCalculator : StatisticsCalculator {
    override fun summarise(values: List<Double>): SummaryStatistics {
        if (values.isEmpty()) return SummaryStatistics()

        var min = Double.MAX_VALUE
        var max = Double.MIN_VALUE
        var sum = 0.0

        values.forEach { value ->
            if (value < min) min = value
            if (value > max) max = value
            sum += value // potential overflow warning
        }
        // Handles the case where all values are zero
        if (min == Double.MAX_VALUE) min = 0.0
        if (max == Double.MIN_VALUE) max = 0.0

        val average = if (values.isNotEmpty()) (sum / values.size) else 0.0
        val last = values.last()

        return SummaryStatistics(
            min = min,
            max = max,
            last = last,
            average = average,
            variance = variance(values, average)
        )
    }

    private fun variance(values: List<Double>, avgVal: Double): Double =
        // asSequence() is used to avoid creating intermediate collections
        values.asSequence()
            .map { it - avgVal }
            .map { it * it }
            .average()
}
