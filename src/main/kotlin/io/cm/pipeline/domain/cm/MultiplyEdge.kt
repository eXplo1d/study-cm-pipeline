package io.cm.pipeline.domain.cm

class MultiplyEdge(
    private val from: StatefulVertex,
    private val to: StatefulVertex,
    private val edgeEffect: Float
) : Edge<Number> {
    override fun from(): StatefulVertex = from
    override fun to(): StatefulVertex = to
    override fun move(iteration: Long, input: Number, iterationUid: String) {
        val result = input.toDouble() * edgeEffect
        to.iterate(iteration, this, result, iterationUid)
    }
}