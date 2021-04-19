package io.cm.pipeline.domain.cm

import io.cm.pipeline.domain.Column
import io.cm.pipeline.domain.MapDataFrame
import io.cm.pipeline.domain.Schema
import org.junit.Test
import kotlin.test.assertEquals

class CognitiveMapTest {

    @Test
    fun `test full connected graph with three concepts`() {
        val vertexX1 = StatefulVertex("x1", LinearFunctionActivation(2.0))
        val vertexX2 = StatefulVertex("x2", LinearFunctionActivation(3.0))
        val vertexY = StatefulVertex("y", LinearFunctionActivation(1.0))
        val cognitiveMap = CognitiveMapBuilder()
            .withVertex(vertexX1)
            .withVertex(vertexX2)
            .withVertex(vertexY)
            .fullConnected(true)
            .build()
        val df = MapDataFrame(
            data = mapOf(
                "x1" to listOf(1.0),
                "x2" to listOf(2.0)
            ),
            schema = Schema(setOf(Column("x1"), Column("x2")))
        )
        val result = cognitiveMap.invoke(df)
        assertEquals(1, result.count())
        assertEquals(4.0, result.collectAs<Double>("x1").first())
        assertEquals(7.5, result.collectAs<Double>("x2").first())
        assertEquals(1.5, result.collectAs<Double>("y").first())
    }

    @Test
    fun `test full connected graph with four concepts`() {
        val vertexX1 = StatefulVertex("x1", LinearFunctionActivation(2.0))
        val vertexX2 = StatefulVertex("x2", LinearFunctionActivation(3.0))
        val vertexX3 = StatefulVertex("x3", LinearFunctionActivation(4.0))
        val vertexY = StatefulVertex("y", LinearFunctionActivation(1.0))
        val cognitiveMap = CognitiveMapBuilder()
            .withVertex(vertexX1)
            .withVertex(vertexX2)
            .withVertex(vertexX3)
            .withVertex(vertexY)
            .fullConnected(true)
            .build()
        val df = MapDataFrame(
            data = mapOf(
                "x1" to listOf(1.0),
                "x2" to listOf(2.0),
                "x3" to listOf(3.0)
            ),
            schema = Schema(setOf(Column("x1"), Column("x2"), Column("x3")))
        )

        val result = cognitiveMap.invoke(df)
        assertEquals(1, result.count())
        assertEquals(7.0, result.collectAs<Double>("x1").first())
        assertEquals(12.0, result.collectAs<Double>("x2").first())
        assertEquals(18.0, result.collectAs<Double>("x3").first())
        assertEquals(3.0, result.collectAs<Double>("y").first())
    }

    @Test
    fun `test fit full connected graph with four concepts`() {
        val vertexX1 = StatefulVertex("x1", LinearFunctionActivation(2.0))
        val vertexX2 = StatefulVertex("x2", LinearFunctionActivation(3.0))
        val vertexX3 = StatefulVertex("x3", LinearFunctionActivation(4.0))
        val vertexY = StatefulVertex("y", LinearFunctionActivation(1.0))
        val cognitiveMap = CognitiveMapBuilder()
            .withVertex(vertexX1)
            .withVertex(vertexX2)
            .withVertex(vertexX3)
            .withVertex(vertexY)
            .fullConnected(true)
            .build()
        val df = MapDataFrame(
            data = mapOf(
                "x1" to listOf(1.0),
                "x2" to listOf(2.0),
                "x3" to listOf(3.0),
                "y" to listOf(3.0)
            ),
            schema = Schema(setOf(Column("x1"), Column("x2"), Column("x3")))
        )
        cognitiveMap.train(df)

        print(cognitiveMap)
    }
}