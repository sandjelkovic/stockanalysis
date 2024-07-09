package com.sandjelkovic.stockanalysis.web

import com.fasterxml.jackson.databind.ObjectMapper
import com.sandjelkovic.stockanalysis.StockAnalysisApplication
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import org.testcontainers.containers.GenericContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName
import kotlin.random.Random

@SpringBootTest(classes = [StockAnalysisApplication::class])
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class StockAnalysisControllerIntegrationTest {

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Autowired
    lateinit var webApplicationContext: WebApplicationContext

    private lateinit var mockMvc: MockMvc

    companion object {
        @Container
        val redisContainer = GenericContainer(DockerImageName.parse("redis:6.2.6")).apply {
            withExposedPorts(6379)
        }

        @JvmStatic
        @DynamicPropertySource
        fun redisProperties(registry: DynamicPropertyRegistry) {
            redisContainer.start()
            registry.add("jedis.host") { redisContainer.host }
            registry.add("jedis.port") { redisContainer.getMappedPort(6379) }
        }
    }

    @BeforeAll
    fun setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build()
    }

    @AfterAll
    fun tearDown() {
        redisContainer.stop()
    }


    @Test
    fun `test add and retrieve single data point`() {
        val symbol = "AAPL"
        val value = 150.0
        val dataPointDto = DataPointDto(symbol, value)

        mockMvc.perform(
            post("/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(dataPointDto))
        )
            .andExpect(status().isOk)

        mockMvc.perform(
            get("/stats")
                .param("symbol", symbol)
                .param("k", "1")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.last").value(value))
    }

    @Test
    fun `test add and retrieve batch data points`() {
        val symbol = "GOOGL"
        val values = listOf(2500.0, 2520.0, 2550.0)
        val batchDataDto = BatchDataDto(symbol, values)

        mockMvc.perform(
            post("/add_batch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(batchDataDto))
        )
            .andExpect(status().isOk)

        mockMvc.perform(
            get("/stats")
                .param("symbol", symbol)
                .param("k", "3")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.last").value(values.last()))
    }

    @Test
    fun `test get stats with count greater than data size`() {
        val symbol = "MSFT"
        val values = listOf(300.0, 305.0)
        val batchDataDto = BatchDataDto(symbol, values)

        mockMvc.perform(
            post("/add_batch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(batchDataDto))
        )
            .andExpect(status().isOk)

        mockMvc.perform(
            get("/stats")
                .param("symbol", symbol)
                .param("k", "5")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.last").value(values.last()))
    }

    @Test
    fun `test retrieve stats for non-existent symbol`() {
        val symbol = "TSLA"

        mockMvc.perform(
            get("/stats")
                .param("symbol", symbol)
                .param("k", "3")
        )
            .andExpect(status().isNotFound)
    }

    @Test
    fun `test add batch data with varying sizes`() {
        val symbol = "FB"
        val values1 = listOf(300.0, 305.0)
        val values2 = listOf(310.0, 315.0, 320.0)

        val batchDataDto1 = BatchDataDto(symbol, values1)
        mockMvc.perform(
            post("/add_batch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(batchDataDto1))
        )
            .andExpect(status().isOk)

        val batchDataDto2 = BatchDataDto(symbol, values2)
        mockMvc.perform(
            post("/add_batch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(batchDataDto2))
        )
            .andExpect(status().isOk)

        mockMvc.perform(
            get("/stats")
                .param("symbol", symbol)
                .param("k", "5")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.last").value(values2.last()))
            .andExpect(jsonPath("$.avg").value((values1 + values2).average()))
    }

    @Test
    fun `test get stats with more than 1000 entries`() {
        val symbol = "NVDA"
        val values = generateSequence { Random.nextDouble() }
            .take(1005)
            .toList()

        val batchDataDto = BatchDataDto(symbol, values)
        mockMvc.perform(
            post("/add_batch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(batchDataDto))
        )
            .andExpect(status().isOk)

        mockMvc.perform(
            get("/stats")
                .param("symbol", symbol)
                .param("k", "4")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.last").value(values.last()))
            .andExpect(jsonPath("$.min").value(values.minOrNull()))
            .andExpect(jsonPath("$.max").value(values.maxOrNull()))
            .andExpect(jsonPath("$.avg").value(values.average()))
    }

    @Test
    fun `test get stats with invalid k greater than 7`() {
        val symbol = "AAPL"

        mockMvc.perform(
            get("/stats")
                .param("symbol", symbol)
                .param("k", "8")
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `test get stats with non-numeric k`() {
        val symbol = "AAPL"

        mockMvc.perform(
            get("/stats")
                .param("symbol", symbol)
                .param("k", "abc")
        )
            .andExpect(status().isBadRequest)
    }

    fun <T> toJson(value: T): String = objectMapper.writeValueAsString(value)

    private inline fun <reified T> fromJson(json: String): T = objectMapper.readValue(json, T::class.java)
}
