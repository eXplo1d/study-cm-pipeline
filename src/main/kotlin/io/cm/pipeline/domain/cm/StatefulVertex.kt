package io.cm.pipeline.domain.cm

import java.util.UUID

class StatefulVertex(
    val name: String,
    val activationFunction: (Number) -> Number,
    val outputEdges: MutableSet<Edge<Number>> = mutableSetOf(),
    val inputEdges: MutableSet<Edge<Number>> = mutableSetOf(),
) : Vertex<Number> {
    private val MAX_DEPTH = 3

    private val iterationState: MutableMap<Long, VertexState<Number>> = mutableMapOf()
    private val iterationInputValueState: MutableMap<Long, MutableMap<Edge<Number>, Number>> = mutableMapOf()
    private val iterationRecursionFlag: MutableMap<String, Long> = mutableMapOf()

    override fun getState(iteration: Long): VertexState<Number> =
        iterationState[iteration] ?: VertexState(0.0, VertexState.State.NOT_COMPLETED)

    override fun iterate(
        iteration: Long,
        fromEdge: Edge<Number>,
        value: Number,
        iterationUid: String
    ) {
        println("Iteration: $iteration from {${fromEdge.from().name}; ${fromEdge.to().name}} with value: $value")

        if (iterateCount(iterationUid, fromEdge) > MAX_DEPTH) {
            return
        }

        addInputValue(iteration, fromEdge, value)

        if (getState(iteration).state == VertexState.State.NOT_COMPLETED) {
            if (checkAllInputsReceived(iteration)) {
                val result = calculateState(iteration)
                setState(iteration, VertexState(result, VertexState.State.COMPLETED))
                goDeep(iteration, result, iterationUid)
            }
        } else {
            val result = getState(iteration).value
            goDeep(iteration, result, iterationUid)
        }
    }

    override fun setValue(
        iteration: Long,
        value: Number
    ) {
        setState(
            iteration,
            VertexState(
                value,
                VertexState.State.SET
            )
        )
        goDeep(iteration, value, UUID.randomUUID().toString())
    }

    private fun setState(
        iteration: Long,
        state: VertexState<Number>
    ) {
        iterationState[iteration] = state
    }

    override fun calculate(iteration: Long) {
        val state = getState(iteration - 1).state
        if (state == VertexState.State.COMPLETED || state == VertexState.State.SET) {
            val res = calculateState(iteration)
            val vertexState = if (checkAllInputsReceived(iteration)) {
                VertexState(res, VertexState.State.COMPLETED)
            } else {
                VertexState(res, VertexState.State.NOT_COMPLETED)
            }
            setState(iteration, vertexState)
            goDeep(
                iteration,
                res,
                UUID.randomUUID().toString()
            )
        } else {
            throw IllegalStateException("could not recalculate iteration $iteration - 1 graph cause state ${this.name} is not completed")
        }
    }

    override fun recalculate(iteration: Long) {
        val vertexState = getState(iteration)
        if (vertexState.state == VertexState.State.COMPLETED || vertexState.state == VertexState.State.SET) {
            val res = vertexState.value
            goDeep(
                iteration,
                res,
                UUID.randomUUID().toString()
            )
        }
    }

    private fun addInputValue(iteration: Long, edge: Edge<Number>, value: Number) {
        iterationInputValueState.merge(
            iteration,
            mutableMapOf(edge to value)
        ) { old: MutableMap<Edge<Number>, Number>, new: MutableMap<Edge<Number>, Number> ->
            old.putAll(new)
            old
        }
    }

    private fun sumConcepts(iteration: Long): Number {
        return iterationInputValueState[iteration]?.let { state ->
            state.values.reduce { old, new ->
                old.toDouble() + new.toDouble()
            }
        } ?: 0
    }

    private fun checkAllInputsReceived(iteration: Long): Boolean {
        return iterationInputValueState[iteration]?.keys?.containsAll(inputEdges) ?: false
    }

    private fun calculateState(iteration: Long): Number = activationFunction.invoke(
        getState(iteration - 1).value.toDouble() + sumConcepts(iteration - 1).toDouble()
    )

    private fun goDeep(iteration: Long, value: Number, iterationUid: String) {
        outputEdges.forEach { edge ->
            edge.move(iteration, value, iterationUid)
        }
    }

    private fun iterateCount(uuid: String, fromEdge: Edge<Number>): Long {
        return iterationRecursionFlag.merge(uuid + fromEdge.from().name, 1) { old, new -> old + new }!!
    }
}