package unibz.cs.semint.kprime.domain.ddl

import org.junit.Test
import kotlin.test.assertEquals

class ColumnTest {

    @Test
    fun test_column_composed_name_split() {
        // given
        val col = Column.of("tab1.colA")
        // then
        assertEquals("tab1",col.dbtable)
        assertEquals("colA",col.name)
    }

    @Test
    fun test_column_simple_name_split() {
        // given
        val col = Column.of("colA")
        // then
        assertEquals("",col.dbtable)
        assertEquals("colA",col.name)
    }

}