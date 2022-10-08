package unibz.cs.semint.kprime.domain.db

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
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

    @Test
    fun test_json() {
        // given
        val col = Column.of("colA")
        // when
        val colJson = jacksonObjectMapper().writeValueAsString(col)
        // then
        val result = """
            {"name":"colA","id":"","dbname":null,"nullable":false,"dbtype":"","type":null,"unit":null,"cardinality":null,"role":null,"labels":null,"default":null,"dbtable":"","var":null}
        """.trimIndent()
        assertEquals(result,colJson)
    }

    @Test
    fun test_json_with_labels() {
        // given
        val col = Column.of("colA")
        col.labels = "aaa"
        // when
        val colJson = jacksonObjectMapper().writeValueAsString(col)
        // then
        val result = """
            {"name":"colA","id":"","dbname":null,"nullable":false,"dbtype":"","type":null,"unit":null,"cardinality":null,"role":null,"labels":"aaa","default":null,"dbtable":"","var":null}
        """.trimIndent()
        assertEquals(result,colJson)
    }

}