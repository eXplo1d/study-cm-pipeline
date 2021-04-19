package io.cm.pipeline.domain.cm

class MultiplyEdge(
    private val from: StatefulVertex,
    private val to: StatefulVertex,
    private var edgeEffect: Double
) : Edge<Double> {
    override fun from(): StatefulVertex = from
    override fun to(): StatefulVertex = to
    override fun move(iteration: Long, input: Double, iterationUid: String) {
        val result: Double = input * edgeEffect
        to.iterate(iteration, this, result, iterationUid)
    }
    override fun increment(delta: Double) {
        edgeEffect += delta
    }
}