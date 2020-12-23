package io.cm.pipeline.domain

import java.util.function.Function

interface Transformer: Function<DataFrame, DataFrame> {
    override fun apply(dataFrame: DataFrame): DataFrame
}