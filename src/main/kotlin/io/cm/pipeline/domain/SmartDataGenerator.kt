package io.cm.pipeline.domain

import kotlin.random.Random

class SmartDataGenerator(
    private val columns: Set<String>,
    private val goalColumnName: String,
    private val rows: Long,
    private val deviationPercent: Short = 90,
    private val transitivePercent: Short = 15
) : DataGenerator {

    private val random: Random = Random(42)

    override fun apply(unit: Unit): DataFrame {
        TODO()
    }

    private fun isTransitive(): Boolean = random.nextInt(100) <= transitivePercent
}