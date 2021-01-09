package io.cm.pipeline.domain

import kotlin.math.*
import kotlin.random.Random

class SmartDataGenerator(
        private val columns: Set<String>,
        private val goalColumnName: String,
        private val rows: Int,
        private val deviationPercent: Short = 20,
        private val transitivePercent: Short = 20
) : DataGenerator {

    private val transitiveStartIndex: Int = (columns.size * (1 - transitivePercent / 100))
    private fun deviation(double: Double): Double {
        return (double * Random.nextInt(100 - deviationPercent.toInt(), 100 + transitiveStartIndex) / 100)
    }

    private fun goalFunctionsAmount(quantity: Int): Int {
        return sqrt(quantity.toDouble()).toInt()
    }

    private val setOfFun = setOf(
            { x: Double -> x },
            { x: Double -> ln(x) },
            { x: Double -> ln(x).pow(ln(x)) },
            { x: Double -> sqrt(x) },
            { x: Double -> x.pow(2.0) },
            { x: Double -> x.pow(3.0) },
            { x: Double -> sin(x) },
            { x: Double -> cos(x) },
            { x: Double -> sin(x.pow(2.0)) },
            { x: Double -> cos(x.pow(2.0)) },
            { x: Double -> sin(x.pow(3.0)) },
            { x: Double -> cos(x.pow(3.0)) }
    )

    override fun apply(unit: Unit): DataFrame {
        val columnBuffer = mutableMapOf<String, List<Any?>>()
        for (col in 0 until transitiveStartIndex) {
//      random seed = x+ln(x)
            val randomSequence = Random(seed = (col))
            val rowBuffer = mutableListOf<Any?>()
            for (row in 1..rows) {
                rowBuffer.add(randomSequence.nextLong(1000))
            }
            columnBuffer[columns.elementAt(col)] = rowBuffer.toList()
        }

        for (col in (transitiveStartIndex) until (columns.size)) {
            val rowBuffer = mutableListOf<Any?>()
//      get random column index
            val randomColumn: Int = Random.nextInt(0, transitiveStartIndex)
//      get random function index
            val randomFunction: Int = Random.nextInt(0, setOfFun.size)
            for (row in 1..rows) {
                rowBuffer.add(
                        deviation(
                                setOfFun.elementAt(randomFunction)
                                        .invoke(columnBuffer[columns.elementAt(randomColumn)]!![row] as Double)
                        )
                )
            }
            columnBuffer[columns.elementAt(col)] = rowBuffer.toList()
        }

//    goal column set to zero
        val rowBufferZero = mutableListOf<Any?>()
        for (row in 1..rows) {
            rowBufferZero.add(0.0)
        }
        columnBuffer[goalColumnName] = rowBufferZero.toList()

//    map of column index and function for this column
        val functionBuffer = mutableMapOf<Int, Int>()
        for (i in 1 until (goalFunctionsAmount(columns.size))) {
            functionBuffer[Random.nextInt(columns.size)] = Random.nextInt(setOfFun.size)
        }

//    goal column calculation
        val rowBuffer = mutableListOf<Any?>()
        for (row in 0 until rows) {
            var currentValue: Double = 0.0
            for (k in functionBuffer) {
                currentValue += deviation(
                        setOfFun.elementAt(k.value)
                                .invoke((columnBuffer[columns.elementAt(k.key)]!![row] as Long).toDouble())
                )
            }
            rowBuffer.add(currentValue)
        }
        columnBuffer[goalColumnName] = rowBuffer.toList()

        val schema = Schema(
                columnBuffer
                        .keys
                        .map { colName ->
                            Column(colName)
                        }.toSet()
        )

        return MapDataFrame(
                columnBuffer.toMap(),
                schema
        )
    }
}
