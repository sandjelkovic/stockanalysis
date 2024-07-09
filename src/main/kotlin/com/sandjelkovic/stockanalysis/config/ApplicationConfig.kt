package com.sandjelkovic.stockanalysis.config

import com.sandjelkovic.stockanalysis.analytics.ParallelStatisticsCalculator
import com.sandjelkovic.stockanalysis.analytics.SequentialStatisticsCalculator
import com.sandjelkovic.stockanalysis.data.DataPointRepository
import com.sandjelkovic.stockanalysis.data.JedisPoolDelegate
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPoolConfig


@Configuration
@EnableConfigurationProperties(JedisProperties::class)
class ApplicationConfig(private val jedisProperties: JedisProperties) {

    @Bean
    @ConditionalOnMissingBean
    fun jedisPoolDelegate(): JedisPoolDelegate {
        val poolConfig = JedisPoolConfig().apply {
            maxTotal = jedisProperties.pool.maxTotal
            maxIdle = jedisProperties.pool.maxIdle
        }
        return JedisPoolDelegate(JedisPool(poolConfig, jedisProperties.host, jedisProperties.port))
    }

    @Bean
    fun dataPointRepository(jedisPool: JedisPoolDelegate) =
        DataPointRepository(jedisPool)

    @Bean
    fun sequentialStatisticsCalculator() =
        SequentialStatisticsCalculator()

    @Bean
    fun parallelStatisticsCalculator() =
        ParallelStatisticsCalculator()
}