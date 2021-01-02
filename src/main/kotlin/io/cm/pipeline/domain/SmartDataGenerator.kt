package io.cm.pipeline.domain

import java.util.*
import kotlin.math.ln
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.cos
import kotlin.random.Random

class SmartDataGenerator(
    private val columns: Set<String>,
    private val goalColumnName: String,
    private val rows: Int,
    private val deviationPercent: Short = 20,
    private val transitivePercent: Short = 20
) : DataGenerator {

  private val transitiveStartIndex: Int = (columns.size * (1-transitivePercent/100))
  private fun deviation(double: Double): Double {
    return (double * Random
            .nextInt(100 - deviationPercent.toInt(), 100 + transitiveStartIndex)/100)
            .toInt()
            .toDouble()
  }
  private val setOfFun = setOf(
          { x: Double -> x },
          { x: Double -> ln(x) },
          { x: Double -> ln(x).pow(ln(x)) },
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
    for (col in 1..transitiveStartIndex) {
//      random seed = x+ln(x)
      val randomSequence = Random(seed = (col + ln(col.toDouble())).toInt())
      val rowBuffer = mutableListOf<Any?>()
      for (row in 1..rows) {
        rowBuffer.add(randomSequence.nextLong())
      }
      columnBuffer[columns.elementAt(col)] = rowBuffer.toList()
    }
    for (col in (transitiveStartIndex+1)..(columns.size)) {
      val rowBuffer = mutableListOf<Any?>()
//      get random column index
      val randomColumn: Int = Random.nextInt(1, transitiveStartIndex)
//      get random function index
      val randomFunction: Int = Random.nextInt(1, setOfFun.size)
      for (row in 1..rows) {
        rowBuffer.add(
          deviation(
            setOfFun.elementAt(randomFunction)
                    .invoke(columnBuffer[randomColumn]!![row] as Double)
          )
        )
      }
      columnBuffer[UUID.randomUUID().toString()] = rowBuffer.toList()
    }

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
