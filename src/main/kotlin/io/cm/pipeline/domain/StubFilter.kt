package io.cm.pipeline.domain

class StubFilter : Filter {
    override fun apply(dataFrame: DataFrame): DataFrame = dataFrame
}