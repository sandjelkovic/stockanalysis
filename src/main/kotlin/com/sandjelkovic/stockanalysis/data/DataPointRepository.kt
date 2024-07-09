package com.sandjelkovic.stockanalysis.data

class DataPointRepository(
    private val jedisPool: JedisPoolDelegate,
) {
    fun addDataPoint(symbol: String, value: Double) {
        jedisPool.resource.use {
            it.rpush(symbol, value.toString())
        }
    }

    fun addBatchData(symbol: String, values: List<Double>) {
        jedisPool.resource.use {
            it.rpush(symbol, *values.map(Double::toString).toTypedArray())
        }
    }

    fun getStats(symbol: String, count: Int): List<Double> =
        jedisPool.resource.use {
            it.lrange(symbol, -count.toLong(), -1).map(String::toDouble)
        }
}