package com.sandjelkovic.stockanalysis.web

import com.sandjelkovic.stockanalysis.analytics.ParallelStatisticsCalculator
import com.sandjelkovic.stockanalysis.analytics.SequentialStatisticsCalculator
import com.sandjelkovic.stockanalysis.analytics.SummaryStatistics
import com.sandjelkovic.stockanalysis.data.DataPointRepository
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus

class StockAnalysisControllerTest {

    private lateinit var dataPointRepository: DataPointRepository
    private lateinit var sequentialStatisticsCalculator: SequentialStatisticsCalculator
    private lateinit var parallelStatisticsCalculator: ParallelStatisticsCalculator
    private lateinit var controller: StockAnalysisController

    @BeforeEach
    fun setup() {
        dataPointRepository = mockk(relaxed = true)
        sequentialStatisticsCalculator = mockk(relaxed = true)
        parallelStatisticsCalculator = mockk(relaxed = true)
        controller =
            StockAnalysisController(dataPointRepository, sequentialStatisticsCalculator, parallelStatisticsCalculator)
    }

    @Test
    fun `Should return 200 when adding a data point`() {
        val response = controller.addDataPoint(DataPointDto("AAPL", 150.0))
        assert(response.statusCode == HttpStatus.OK)
    }

    @Test
    fun `Should return 200 when adding batch data`() {
        val response = controller.addBatchData(BatchDataDto("AAPL", listOf(150.0, 151.0, 152.0)))
        assert(response.statusCode == HttpStatus.OK)
    }

    @Test
    fun `Should return 400 when getting stats with blank symbol`() {
        val response = controller.getStats("", 1)
        assert(response.statusCode == HttpStatus.BAD_REQUEST)
    }

    @Test
    fun `Should return 400 when getting stats with invalid k`() {
        val response = controller.getStats("AAPL", 0)
        assert(response.statusCode == HttpStatus.BAD_REQUEST)
    }

    @Test
    fun `Should return 404 when getting stats with no data`() {
        every { dataPointRepository.getStats("AAPL", 10) } returns emptyList()
        val response = controller.getStats("AAPL", 1)
        assert(response.statusCode == HttpStatus.NOT_FOUND)
    }

    @Test
    fun `Should return 200 and correct stats when getting stats with data`() {
        every { dataPointRepository.getStats("AAPL", 10) } returns listOf(150.0, 151.0, 152.0)
        every { sequentialStatisticsCalculator.summarise(listOf(150.0, 151.0, 152.0)) } returns SummaryStatistics(
            150.0,
            152.0,
            152.0,
            151.0,
            1.0
        )
        val response = controller.getStats("AAPL", 1)
        assert(response.statusCode == HttpStatus.OK)
        assert(response.body == StatsResponseDto(150.0, 152.0, 152.0, 151.0, 1.0))
    }

    @Test
    fun `Should return 400 when getting stats with k less than 1`() {
        val response = controller.getStats("AAPL", 0)
        assert(response.statusCode == HttpStatus.BAD_REQUEST)
    }

    @Test
    fun `Should return 400 when getting stats with k greater than 7`() {
        val response = controller.getStats("AAPL", 8)
        assert(response.statusCode == HttpStatus.BAD_REQUEST)
    }

    @Test
    fun `Should return 200 when getting stats with k equal to 1`() {
        every { dataPointRepository.getStats("AAPL", 10) } returns listOf(150.0)
        every { sequentialStatisticsCalculator.summarise(listOf(150.0)) } returns SummaryStatistics(
            150.0,
            150.0,
            150.0,
            150.0,
            0.0
        )
        val response = controller.getStats("AAPL", 1)
        assert(response.statusCode == HttpStatus.OK)
    }

    @Test
    fun `Should return 200 when getting stats with k equal to 7`() {
        every { dataPointRepository.getStats("AAPL", 10_000_000) } returns listOf(150.0)
        every { sequentialStatisticsCalculator.summarise(listOf(150.0)) } returns SummaryStatistics(
            150.0,
            150.0,
            150.0,
            150.0,
            0.0
        )
        val response = controller.getStats("AAPL", 7)
        assert(response.statusCode == HttpStatus.OK)
    }
}