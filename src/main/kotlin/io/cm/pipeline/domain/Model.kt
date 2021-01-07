package io.cm.pipeline.domain

interface Model: (DataFrame) -> DataFrame {
    override fun invoke(dataFrame: DataFrame): DataFrame
    fun predict(): DataFrame
}