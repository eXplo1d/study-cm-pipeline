package io.cm.pipeline.domain

object SmartDataGeneratorTest {
    //    print(SmartDataGenerator(columns = setOf("clmn1","clmn2","clmn3","clmn4","clmn5"))
    fun test() {
        val smartDataGenerator = SmartDataGenerator(
            columns = setOf("clmn1", "clmn2", "clmn3", "clmn4", "clmn5"),
            rows = 10,
            goalColumnName = "clmn3"
        )
        val dataFrame = smartDataGenerator.apply(Unit)
        assert(dataFrame.count().toInt() == 10)
    }
}

fun main() {
    SmartDataGeneratorTest.test()
}
