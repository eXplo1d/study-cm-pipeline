package io.cm.pipeline.domain.cm

import java.util.function.Function

class MultiplyEdge(
    private val from: Vertex,
    private val to: Vertex,
    private val func: Function<Number, Number>
) : Edge<Number> {
    override fun from(): Vertex = from
    override fun to(): Vertex = to
    override fun move(input: Number): Number = func.apply(input)
}