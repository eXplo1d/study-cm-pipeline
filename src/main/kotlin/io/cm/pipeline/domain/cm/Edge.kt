package io.cm.pipeline.domain.cm

interface Edge<T> : (T) -> T {
    fun from(): Vertex
    fun to(): Vertex
    fun move(input: T): T
    override fun invoke(input: T): T = move(input)
}