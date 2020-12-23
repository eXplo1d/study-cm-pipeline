package io.cm.pipeline.domain

import java.util.UUID
import kotlin.random.Random

class RandomDataGenerator(private val columns: Long, private val rows: Long) : DataGenerator {
    private val random: Random = Random(42L)
    override fun apply(unit: Unit): DataFrame {
        val columnBuffer = mutableMapOf<String, List<Any?>>()
        for (col in 1..columns) {
            val rowBuffer = mutableListOf<Any?>()
            for (row in 1..rows) {
                rowBuffer.add(random.nextLong())
            }
            columnBuffer.put(
                UUID.randomUUID().toString(),
                rowBuffer.toList()
            )
        }
        val schema = Schema(
            columnBuffer
                .keys
                .map { colName ->
                    Column(colName)
                }.toSet()
        )

        return MapDataFrame(
            columnBuffer.toMap(),
            schema
        )
    }
}