package com.sandjelkovic.stockanalysis.data

import redis.clients.jedis.Jedis
import redis.clients.jedis.JedisPool
import java.io.Closeable


// to bypass Spring JMX issues with Jedis Pool
class JedisPoolDelegate(private val jedisPool: JedisPool) : Closeable {
    val resource: Jedis
        get() = jedisPool.resource

    override fun close() {
        jedisPool.close()
    }
}