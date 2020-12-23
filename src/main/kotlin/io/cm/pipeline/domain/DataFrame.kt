package io.cm.pipeline.domain

interface DataFrame {
    fun getColumn(name: String): DataFrame
    fun <T> collectAs(name: String): List<T>
    fun union(another: DataFrame): DataFrame
    fun join(right: DataFrame): DataFrame
    fun getSchema(): Schema
    fun count(): Long
    fun getRows(): List<Map<String, Any?>>
}