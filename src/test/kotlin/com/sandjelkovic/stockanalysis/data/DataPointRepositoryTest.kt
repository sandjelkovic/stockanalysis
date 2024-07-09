package com.sandjelkovic.stockanalysis.data

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.GenericContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName

@SpringBootTest
@Testcontainers
class DataPointRepositoryIntegrationTest {

    @Autowired
    lateinit var dataPointRepository: DataPointRepository

    companion object {
        @Container
        val redisContainer = GenericContainer(DockerImageName.parse("redis:6.2.6")).apply {
            withExposedPorts(6379)
        }

        @JvmStatic
        @DynamicPropertySource
        fun redisProperties(registry: DynamicPropertyRegistry) {
            registry.add("jedis.host") { redisContainer.host }
            registry.add("jedis.port") { redisContainer.getMappedPort(6379) }
        }
    }

    @Test
    fun `Add and retrieve single data point should work`() {
        val symbol = "AAPL"
        val value = 150.0

        dataPointRepository.addDataPoint(symbol, value)
        val stats = dataPointRepository.getStats(symbol, 1)

        assertEquals(listOf(value), stats)
    }

    @Test
    fun `Add and retrieve batch data points should save properly`() {
        val symbol = "GOOGL"
        val values = listOf(2500.0, 2520.0, 2550.0)

        dataPointRepository.addBatchData(symbol, values)
        val stats = dataPointRepository.getStats(symbol, values.size)

        assertEquals(values, stats)
    }

    @Test
    fun `Get stats with count greater than data size should not fail`() {
        val symbol = "MSFT"
        val values = listOf(300.0, 305.0)

        dataPointRepository.addBatchData(symbol, values)
        val stats = dataPointRepository.getStats(symbol, 5)

        assertEquals(values, stats)
    }

    @Test
    fun `Get stats with empty data should not fail`() {
        val symbol = "AMZN"
        val stats = dataPointRepository.getStats(symbol, 5)

        assertTrue(stats.isEmpty())
    }
}