package io.cm.pipeline.domain.cm

import io.cm.pipeline.domain.Column
import io.cm.pipeline.domain.DataFrame
import io.cm.pipeline.domain.MapDataFrame
import io.cm.pipeline.domain.Model
import io.cm.pipeline.domain.Schema
import java.util.concurrent.atomic.AtomicLong

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
                    name2Vertex[entry.key]!!.setValue(iter1, entry.value as Number)
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
}