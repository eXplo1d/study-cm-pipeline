package io.cm.pipeline.domain.cm

import io.cm.pipeline.domain.Model

class CognitiveMapBuilder {

    private val vertices: MutableSet<StatefulVertex> = mutableSetOf()
    private val edges: MutableSet<Edge<Double>> = mutableSetOf()
    private var isFullConnected = false
    private var edgeFuncBuilder: (StatefulVertex, StatefulVertex) -> (Double) -> Double = { _, _ -> { _ -> 1.0 } }

    fun withVertex(vertex: StatefulVertex): CognitiveMapBuilder {
        vertices.add(vertex)
        return this
    }

    fun withEdge(edge: Edge<Double>): CognitiveMapBuilder {
        if (!isFullConnected) {
            edges.add(edge)
        }
        return this
    }

    fun withEdgeFunctionBuilder(func: (StatefulVertex, StatefulVertex) -> (Double) -> Double): CognitiveMapBuilder {
        this.edgeFuncBuilder = func
        return this
    }

    fun fullConnected(isFullConnected: Boolean = true): CognitiveMapBuilder {
        this.isFullConnected = isFullConnected
        return this
    }

    fun build(): Model =
        CognitiveMap(
            vertices = getVertices()
        )

    fun getVertices(): Set<StatefulVertex> {
        getEdges()
            .forEach { edge ->
                val fromEdge = edge.from()
                fromEdge.outputEdges.add(edge)
                val toEdge = edge.to()
                toEdge.inputEdges.add(edge)
            }
        return vertices
    }

    private fun getEdges(): Set<Edge<Double>> =
        if (!isFullConnected) {
            edges
        } else {
            vertices
                .flatMap { from ->
                    vertices
                        .filter { it != from }
                        .map { to ->
                            MultiplyEdge(from = from, to = to, 0.5)
                        }
                }.toSet()
        }
}