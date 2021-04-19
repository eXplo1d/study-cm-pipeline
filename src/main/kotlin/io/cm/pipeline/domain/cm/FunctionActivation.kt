package io.cm.pipeline.domain.cm

interface FunctionActivation<T> {
    fun calculate(input: T): T
    fun increment(delta: T)
}