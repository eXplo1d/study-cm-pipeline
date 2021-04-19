package io.cm.pipeline.domain.cm

interface Edge<T> {
    fun from(): StatefulVertex
    fun to(): StatefulVertex
    fun move(iteration: Long, input: T, iterationUid: String)
    fun increment(delta: T)
}