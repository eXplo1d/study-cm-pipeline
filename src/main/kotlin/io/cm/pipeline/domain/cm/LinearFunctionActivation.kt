package io.cm.pipeline.domain.cm

class LinearFunctionActivation(
    var multiplier: Double,
    var constant: Double = 0.0
) : FunctionActivation<Double> {
    override fun calculate(input: Double): Double = input * multiplier + constant

    override fun increment(delta: Double) {
        multiplier += delta
        constant += (1 / 10) * delta
    }
}
