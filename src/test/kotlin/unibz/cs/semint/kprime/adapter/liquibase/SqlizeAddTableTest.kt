package unibz.cs.semint.kprime.adapter.liquibase

import org.junit.Test
import unibz.cs.semint.kprime.adapter.service.liquibase.sqlizeAddTable
import unibz.cs.semint.kprime.domain.db.Column
import unibz.cs.semint.kprime.domain.db.DatabaseTrademark
import unibz.cs.semint.kprime.domain.dml.CreateTable
import kotlin.test.assertEquals

class SqlizeAddTableTest {

    @Test
    fun test_add_table_liquibase_sqlize() {

        // given
        val createTable = CreateTable()
        createTable.name("tab1")
        val col1 = Column("colName1", "colId1", "col1DbName")
        col1.dbtype = "int"
        col1.dbtable = createTable.name
        createTable.columns.add(col1)

        // when
        val sqlizeAddTableLines = sqlizeAddTable(DatabaseTrademark.H2, createTable)

        // then
        assertEquals("""
            CREATE TABLE tab1 (col1DbName INT)
        """.trimIndent(),sqlizeAddTableLines.joinToString(System.lineSeparator()))
    }
}