package com.sandjelkovic.stockanalysis.analytics

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

// Tests for the calculators, also making sure both calculators return the same results and behave the same
class CommonStatisticsCalculatorTests {

    companion object {
        @JvmStatic
        fun calculators(): Stream<StatisticsCalculator> {
            return Stream.of(
                SequentialStatisticsCalculator(),
                ParallelStatisticsCalculator(),
            )
        }
    }

    @ParameterizedTest
    @MethodSource("calculators")
    fun `Should calculate summary statistics for non-empty list`(calculator: StatisticsCalculator) {
        val values = listOf(1.0, 2.0, 3.0, 4.0, 5.0)

        val stats = calculator.summarise(values)

        assertEquals(1.0, stats.min)
        assertEquals(5.0, stats.max)
        assertEquals(5.0, stats.last)
        assertEquals(3.0, stats.average)
        assertEquals(2.0, stats.variance)
    }

    @ParameterizedTest
    @MethodSource("calculators")
    fun `Should calculate summary statistics for single element list`(calculator: StatisticsCalculator) {
        val values = listOf(1.0)

        val stats = calculator.summarise(values)

        assertEquals(1.0, stats.min)
        assertEquals(1.0, stats.max)
        assertEquals(1.0, stats.last)
        assertEquals(1.0, stats.average)
        assertEquals(0.0, stats.variance)
    }

    @ParameterizedTest
    @MethodSource("calculators")
    fun `Should calculate summary statistics for list with same elements`(calculator: StatisticsCalculator) {
        val values = listOf(1.0, 1.0, 1.0, 1.0, 1.0)

        val stats = calculator.summarise(values)

        assertEquals(1.0, stats.min)
        assertEquals(1.0, stats.max)
        assertEquals(1.0, stats.last)
        assertEquals(1.0, stats.average)
        assertEquals(0.0, stats.variance)
    }

    @ParameterizedTest
    @MethodSource("calculators")
    fun `Should calculate summary statistics for zeroes`(calculator: StatisticsCalculator) {
        val values = listOf(0.0, 0.0)

        val stats = calculator.summarise(values)

        assertEquals(0.0, stats.min)
        assertEquals(0.0, stats.max)
        assertEquals(0.0, stats.last)
        assertEquals(0.0, stats.average)
        assertEquals(0.0, stats.variance)
    }

    @ParameterizedTest
    @MethodSource("calculators")
    fun `Should handle empty list`(calculator: StatisticsCalculator) {
        val values = emptyList<Double>()

        val stats = calculator.summarise(values)

        assertEquals(0.0, stats.min)
        assertEquals(0.0, stats.max)
        assertEquals(0.0, stats.last)
        assertEquals(0.0, stats.average)
        assertEquals(0.0, stats.variance)
    }
}