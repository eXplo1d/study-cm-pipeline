package io.cm.pipeline.domain.cm

import io.cm.pipeline.domain.Model

class CognitiveMapBuilder {

    private val vertices: MutableSet<Vertex> = mutableSetOf()
    private val edges: MutableSet<Edge<Number>> = mutableSetOf()
    private var isFullConnected = false
    private var edgeFuncBuilder: (Vertex, Vertex) -> (Number) -> Number = { _, _ -> { _ -> 1 } }

    fun withVertex(vertex: Vertex): CognitiveMapBuilder {
        if (!isFullConnected) {
            vertices.add(vertex)
        }
        return this
    }

    fun withEdge(edge: Edge<Number>): CognitiveMapBuilder {
        if (!isFullConnected) {
            edges.add(edge)
        }
        return this
    }

    fun withEdgeFunctionBuilder(func: (Vertex, Vertex) -> (Number) -> Number): CognitiveMapBuilder {
        this.edgeFuncBuilder = func
        return this
    }

    fun fullConnected(isFullConnected: Boolean = true): CognitiveMapBuilder {
        this.isFullConnected = isFullConnected
        return this
    }

    fun build(): Model =
        CognitiveMap(
            vertices = vertices,
            edges = getEdges()
        ) { l, r -> l.toDouble() + r.toDouble() }

    private fun getEdges(): Set<Edge<Number>> =
        if (!isFullConnected) {
            edges
        } else {
            vertices
                .flatMap { from ->
                    vertices
                        .filter { it != from }
                        .map { to ->
                            MultiplyEdge(from = from, to = to, edgeFuncBuilder.invoke(from, to))
                        }
                }.toSet()
        }
}