package io.cm.pipeline

import io.cm.pipeline.domain.RandomDataGenerator
import io.cm.pipeline.domain.StubFilter
import io.cm.pipeline.domain.StubTransformer

class CmPipelineApplication

fun main() {

    RandomDataGenerator(columns = 10, rows = 1000)
        .andThen(StubFilter())
        .andThen(StubTransformer())
        .andThen(StubFilter())
        .andThen(StubFilter())
        .andThen(StubFilter())
}

