package io.cm.pipeline.domain.cm

import io.cm.pipeline.domain.Column
import io.cm.pipeline.domain.DataFrame
import io.cm.pipeline.domain.MapDataFrame
import io.cm.pipeline.domain.Model
import io.cm.pipeline.domain.Schema

class CognitiveMap<T>(
    vertices: Set<Vertex>,
    edges: Set<Edge<T>>,
    private val accumulator: (T, T) -> T
) : Model {

    val roots: Map<String, Vertex> = (
        vertices - edges
            .map { edge ->
                edge.to()
            }
        ).map { edge ->
            edge.name to edge
        }.toMap()

    val fromToEdge: Map<Vertex, List<Edge<T>>> = edges.groupBy { edge -> edge.from() }

    override fun apply(dataFrame: DataFrame): DataFrame {
        if (!roots.keys.containsAll(dataFrame.getSchema().columnsNames)) {
            throw IllegalArgumentException("Not enough columns for predict")
        }
        val rows = dataFrame
            .getRows()
            .map { map ->
                map.map { kv ->
                    val value = kv.value as T
                    roots[kv.key]!! to value
                }.toMap()
            }

        val forecast = rows.map {
            predict(it)
        }

        val data = forecast
            .flatMap { vertex2value ->
                vertex2value.map { (vertex, value) ->
                    vertex.name to value
                }
            }.groupBy({ kv -> kv.first }, { kv -> kv.second })

        val schema = Schema(
            data
                .keys
                .map { column ->
                    Column(column)
                }.toSet()
        )

        return MapDataFrame(
            data = data,
            schema = schema
        )
    }

    private fun predict(data: Map<Vertex, T>): Map<Vertex, T> {
        var valuesBuffer = data
        for (root in roots.values) {
            valuesBuffer = goDeep(root, valuesBuffer, mapOf())
        }
        return valuesBuffer
    }

    private fun goDeep(from: Vertex, values: Map<Vertex, T>, incomeCounter: Map<Vertex, Int>): Map<Vertex, T> {
        val counter = incomeCounter
            .toMutableMap().let {
                it.merge(from, 1) { sum, new -> sum + new }
                it
            }.toMap()

        if (counter[from] ?: 0 > 1) {
            return values
        }
        val buffer = values.toMutableMap()
        val edges = fromToEdge[from] ?: listOf()
        for (edge in edges) {
            buffer.merge(
                edge.to(),
                edge.invoke(buffer[from]!!),
                accumulator
            )
        }
        var valuesBuffer = buffer.toMap()
        for (edge in edges) {
            valuesBuffer = goDeep(edge.to(), valuesBuffer, counter)
        }
        return valuesBuffer
    }
}