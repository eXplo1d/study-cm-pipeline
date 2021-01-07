package io.cm.pipeline.domain.cm

interface Vertex<T> {
    fun getState(iteration: Long): VertexState<T>
    fun setValue(iteration: Long, value: T)
    fun iterate(iteration: Long, fromEdge: Edge<T>, value: T, iterationUid: String)
    fun calculate(iteration: Long)
    fun recalculate(iteration: Long)
}

class VertexState<T>(
    val value: T,
    val state: State
) {
    enum class State {
        NOT_COMPLETED,
        COMPLETED,
        SET
        ;
    }
}

