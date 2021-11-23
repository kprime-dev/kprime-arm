package unibz.cs.semint.kprime.adapter

import org.junit.Test
import unibz.cs.semint.kprime.adapter.service.LiquibaseSQLizeAdapter
import unibz.cs.semint.kprime.domain.ddl.Column
import unibz.cs.semint.kprime.domain.dml.ChangeSet
import unibz.cs.semint.kprime.domain.dml.CreateColumn
import kotlin.test.assertEquals

class LiquibaseSQLizeAdapterTest {

    @Test
    fun test_add_column_change() {
        // given
        val change = ChangeSet()
        val createColumn = CreateColumn()
        createColumn.catalog = "cat1"
        createColumn.schema = "schema1"
        val col1 = Column("colName","colId")
        col1.dbname = "colDbName"
        col1.dbtype = "colDbType"
        col1.dbtable = "tableDbName"
        createColumn.columns.add(col1)
        change.createColumn.add(createColumn)

        // when
        val sqlize = LiquibaseSQLizeAdapter().sqlize(change)

        // then
        val sqlizeresult = sqlize.joinToString(System.lineSeparator())
        assertEquals("""
            ALTER TABLE schema1.tableDbName ADD colDbName COLDBTYPE NOT NULL
        """.trimIndent(),sqlizeresult)
    }
}