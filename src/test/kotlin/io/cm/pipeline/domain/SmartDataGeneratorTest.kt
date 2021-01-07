package io.cm.pipeline.domain

import org.junit.Test
import kotlin.test.assertEquals

class SmartDataGeneratorTest {

    @Test
    fun `test with 10 rows`() {
        val smartDataGenerator = SmartDataGenerator(
            columns = setOf("clmn1", "clmn2", "clmn3", "clmn4", "clmn5"),
            rows = 10,
            goalColumnName = "clmn3"
        )
        val dataFrame = smartDataGenerator.apply(Unit)
        assertEquals(10, dataFrame.count())
    }
}