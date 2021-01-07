package io.cm.pipeline.domain

import java.lang.IllegalArgumentException

class MapDataFrame(private val data: Map<String, List<Any?>>, private val schema: Schema) : DataFrame {

    private val framedData: Map<String, DataFrame> =
        if (schema.columns.size > 1) {
            schema
                .columns
                .map { column ->
                    val columnSchema = Schema(setOf(column))
                    val columnData = mapOf(column.name to (data[column.name] ?: listOf()))
                    column.name to MapDataFrame(columnData, columnSchema)
                }.toMap()
        } else {
            mapOf(schema.columnsNames.first() to this)
        }

    private val size = data.values.map { list ->
        list.size.toInt()
    }.firstOrNull() ?: 0

    override fun getColumn(name: String): DataFrame =
        framedData[name] ?: throw IllegalArgumentException("Column $name not found")

    override fun <T> collectAs(name: String): List<T> {
        val column = data[name] ?: throw IllegalArgumentException("Column $name not found")
        return column as List<T>
    }

    override fun union(another: DataFrame): DataFrame =
        if (schema == another.getSchema()) {
            val newDat = schema
                .columns
                .map { col ->
                    val first = data[col.name] ?: throw IllegalArgumentException("Column ${col.name} not found")
                    val second = another.collectAs<Any?>(col.name)
                    val list = first + second
                    col.name to list
                }.toMap()
            MapDataFrame(
                newDat, schema
            )
        } else throw IllegalArgumentException("Couldn't union two data frames with different schema")

    override fun join(right: DataFrame): DataFrame = if (size != right.count()) {
        throw IllegalArgumentException("Couldn't union two data frames with sizes")
    } else {
        val buffer = mutableMapOf<String, List<Any?>>()
        buffer.putAll(data)
        right
            .getSchema()
            .columns
            .forEach { col ->
                val list = right.collectAs<Any?>(col.name)
                buffer.putIfAbsent(col.name, list)
            }
        val newData = buffer.toMap()
        val newSchema = Schema(schema.columns + right.getSchema().columns)
        MapDataFrame(
            newData,
            newSchema
        )
    }

    override fun count(): Int = size

    override fun getSchema(): Schema = schema

    override fun getRows(): List<Map<String, Any?>> {
        val buffer = mutableListOf<Map<String, Any>>()
        val keys = data.keys
        for (i in 0 until size) {
            buffer.add(
                keys
                    .map { key ->
                        val value = data[key]?.let { it[i] } ?: 0
                        key to value
                    }.toMap()
            )
        }
        return buffer.toList()
    }
}