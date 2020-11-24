package unibz.cs.semint.kprime.domain

import junit.framework.Assert
import org.junit.Test
import unibz.cs.semint.kprime.usecase.common.UnSQLizeSelectUseCase

class MappingTest {

    @Test
    fun test_from_entity_mapping() {
        // given
        val sqlQuery = """
            SELECT *
            FROM tab1
        """.trimIndent()
        val query = UnSQLizeSelectUseCase().fromsql("query1", sqlQuery)
        Assert.assertEquals("*", query.select.attributes[0].name)
        Assert.assertEquals("tab1", query.select.from[0].tableName)
    }

    @Test
    fun test_from_relation_mapping() {
        // given
        val sqlQuery = """
            SELECT *
            FROM tab1
        """.trimIndent()
        val query = UnSQLizeSelectUseCase().fromsql("query1", sqlQuery)
    }

    @Test
    fun test_from_reference_mapping() {

    }
}