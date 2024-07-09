package com.sandjelkovic.stockanalysis.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "jedis")
class JedisProperties {
    var host: String = "localhost"
    var port: Int = 6379
    var pool: Pool = Pool()

    class Pool {
        var maxTotal: Int = 16
        var maxIdle: Int = 16
    }
}