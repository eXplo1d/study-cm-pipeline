package io.cm.pipeline.domain

class StubTransformer : Transformer {
    override fun apply(dataFrame: DataFrame): DataFrame = dataFrame
}