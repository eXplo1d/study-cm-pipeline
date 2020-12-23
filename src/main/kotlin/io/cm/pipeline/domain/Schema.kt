package io.cm.pipeline.domain

class Schema(val columns: Set<Column>) {
    val columnsNames = columns.map { it.name }
}

data class Column(val name: String)
