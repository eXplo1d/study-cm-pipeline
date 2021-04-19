package io.cm.pipeline.domain.cm

import io.cm.pipeline.domain.Column
import io.cm.pipeline.domain.DataFrame
import io.cm.pipeline.domain.MapDataFrame
import io.cm.pipeline.domain.Model
import io.cm.pipeline.domain.Schema
import java.util.concurrent.atomic.AtomicLong
import kotlin.math.pow
import kotlin.math.sqrt

class CognitiveMap(private val vertices: Set<StatefulVertex>) : Model {

    private val currentIteration = AtomicLong()
    private val name2Vertex = vertices.associateBy { it.name }

    override fun invoke(dataFrame: DataFrame): DataFrame {
        val iterationBuffer = mutableSetOf<Long>()
        dataFrame
            .getRows()
            .forEach { row ->
                val iter1 = currentIteration.incrementAndGet()
                row.forEach { entry ->
                    name2Vertex[entry.key]!!.setValue(iter1, entry.value as Double)
                }
                val iter2 = currentIteration.incrementAndGet()
                iterationBuffer.add(iter2)
                vertices.forEach { vertex ->
                    vertex.calculate(iter2)
                }
            }
        val data = iterationBuffer.flatMap { iteration ->
            vertices.map { vertex ->
                vertex.name to vertex.getState(iteration).value
            }
        }.groupBy({ it.first }, { it.second })

        val schema = Schema(
            data
                .keys
                .map { name ->
                    Column(name)
                }.toSet()
        )

        return MapDataFrame(data, schema)
    }

    override fun predict(): DataFrame {
        val iteration = currentIteration.incrementAndGet()
        vertices.forEach { vertex ->
            vertex.calculate(iteration)
        }
        val data = vertices.map { vertex ->
            vertex.name to vertex.getState(iteration)
        }.groupBy({ it.first }, { it.second })

        val schema = Schema(
            data
                .keys
                .map { name ->
                    Column(name)
                }.toSet()
        )

        return MapDataFrame(data, schema)
    }

    override fun train(data: DataFrame) {
        var activationDelta = meanValue(data) * 1 / 10000
        var edgeDelta = 0.02
        var error = Double.MAX_VALUE
        var iter = 0
        while (iter < 10) {
            println("Train iter $iter")
            for (vertex in vertices) {
                vertex.activationFunction.increment(activationDelta)
                val firstNewError = calculateError(data)
                if (firstNewError > error) {
                    vertex.activationFunction.increment((-1) * 2 * activationDelta)
                    val secondNewError = calculateError(data)
                    if (secondNewError > firstNewError) {
                        // изначально было лучше => вернули обратно
                        vertex.activationFunction.increment(activationDelta)
                    }
                }

                ////
                for (edge in vertex.outputEdges) {
                    edge.increment(edgeDelta)
                    val firstNewError = calculateError(data)
                    if (firstNewError > error) {
                        vertex.activationFunction.increment((-1) * 2 * activationDelta)
                        val secondNewError = calculateError(data)
                        if (secondNewError > firstNewError) {
                            // изначально было лучше => вернули обратно
                            vertex.activationFunction.increment(activationDelta)
                        }
                    }
                }
            }
            iter ++
        }
    }

    private fun calculateError(data: DataFrame): Double {
        val result = invoke(data)
        val rowsResult = result.getRows()
        val rowsExpected = data.getRows()
        var sqError: Double = 0.0
        for (i in 0 until result.count()) {
            sqError += rowsResult.get(i).map { rowResult ->
                val expected = rowsExpected.get(0)[rowResult.key] as Double
                val actual = rowResult.value as Double
                (expected - actual).pow(2.0)
            }.sum()
        }
        return sqrt(sqError)
    }

    private fun meanValue(data: DataFrame): Double {
        var sum: Double = 0.0
        data
            .getRows()
            .forEach { row ->
                sum += row
                    .values
                    .map { value ->
                        value as Double
                    }.sum()
            }
        return sum / (data.count() * data.getSchema().columns.count())
    }
}