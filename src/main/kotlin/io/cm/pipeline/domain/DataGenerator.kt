package io.cm.pipeline.domain

import java.util.function.Function

interface DataGenerator: Function<Unit, DataFrame> {
    override fun apply(unit: Unit): DataFrame
}