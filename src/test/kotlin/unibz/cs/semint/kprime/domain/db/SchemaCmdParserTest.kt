package unibz.cs.semint.kprime.domain.db

import org.junit.Test
import kotlin.test.assertEquals

class SchemaCmdParserTest {

    @Test
    fun test_parse_table_with_pk() {
        // given
        // when
        val table = SchemaCmdParser.parseTable("person(id/name):name,age")
        // then
        assertEquals("person",table.name)
        assertEquals("id",table.primaryKey)
        assertEquals("name",table.naturalKey)
    }

    @Test
    fun test_parse_functionals() {
        // Given
        // When
        val constraints = SchemaCmdParser.parseFunctionals(1,"table1","A,B-->C; C-->B")
        // Then
        assertEquals(2,constraints.size)
        val functionals = constraints.toList()
        assertEquals("table1.FUNCTIONAL1", functionals[0].name)
        assertEquals("table1", functionals[0].source.table)
        assertEquals("[C]", functionals[0].source.columns.toString())
        assertEquals("table1", functionals[0].target.table)
        assertEquals("[B]", functionals[0].target.columns.toString())

        assertEquals("table1.FUNCTIONAL2", functionals[1].name)
        assertEquals("table1", functionals[1].source.table)
        assertEquals("[A, B]", functionals[1].source.columns.toString())
        assertEquals("table1", functionals[1].target.table)
        assertEquals("[C]", functionals[1].target.columns.toString())
    }


    @Test
    fun test_parse_multivalueds() {
        // Given
        // When
        val constraints = SchemaCmdParser.parseMultivalued(1,"table1", "A,B-->C; C-->B")
        // Then
        assertEquals(2, constraints.size)
        val functionals = constraints.toList()
        assertEquals("table1.MULTIVALUED1", functionals[0].name)
        assertEquals("table1", functionals[0].source.table)
        assertEquals("[C]", functionals[0].source.columns.toString())
        assertEquals("table1", functionals[0].target.table)
        assertEquals("[B]", functionals[0].target.columns.toString())

        assertEquals("table1.MULTIVALUED2", functionals[1].name)
        assertEquals("table1", functionals[1].source.table)
        assertEquals("[A, B]", functionals[1].source.columns.toString())
        assertEquals("table1", functionals[1].target.table)
        assertEquals("[C]", functionals[1].target.columns.toString())
    }


    @Test
    fun test_parse_inclusion() {
        // Given
        // When
        val constraint = SchemaCmdParser.parseInclusion(1,"table0:A,B-->table1:C")
        // Then
        assertEquals("table0_table1.INCLUSION1", constraint.name)
        assertEquals("table0", constraint.source.table)
        assertEquals("[A, B]", constraint.source.columns.toString())
        assertEquals("table1", constraint.target.table)
        assertEquals("[C]", constraint.target.columns.toString())
    }

    @Test
    fun test_parse_double_inclusion() {
        // Given
        // When
        val constraint = SchemaCmdParser.parseDoubleInclusion(1,"table0:A,B<->table1:C")
        // Then
        assertEquals("table0_table1.DOUBLE_INCLUSION1", constraint.name)
        assertEquals("table0", constraint.source.table)
        assertEquals("[A, B]", constraint.source.columns.toString())
        assertEquals("table1", constraint.target.table)
        assertEquals("[C]", constraint.target.columns.toString())
    }
}