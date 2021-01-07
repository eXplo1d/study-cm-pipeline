package io.cm.pipeline

import io.cm.pipeline.domain.SmartDataGenerator
import io.cm.pipeline.domain.StubFilter
import io.cm.pipeline.domain.StubTransformer

class CmPipelineApplication

fun main() {

    print(
        SmartDataGenerator(
            columns = setOf("clmn1", "clmn2", "clmn3", "clmn4", "clmn5"),
            rows = 100,
            goalColumnName = "label"
        )
            .andThen(StubFilter())
            .andThen(StubTransformer())
            .andThen(StubFilter())
            .andThen(StubFilter())
            .andThen(StubFilter())
    )
}
