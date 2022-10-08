package unibz.cs.semint.kprime.adapter.liquibase

import org.junit.Test
import unibz.cs.semint.kprime.adapter.service.liquibase.sqlizeAddColumn
import unibz.cs.semint.kprime.domain.db.Column
import unibz.cs.semint.kprime.domain.db.DataType
import unibz.cs.semint.kprime.domain.db.DatabaseTrademark
import unibz.cs.semint.kprime.domain.ddl.CreateColumn
import kotlin.test.assertEquals

class SqlizeAddColumnTest {

    @Test
    fun test_add_column_liquibase_sqlize() {
        // given
        val createColumn = CreateColumn()
        createColumn.catalog = "cat1"
        createColumn.schema = "schema1"
        val col1 = Column("colName","colId")
        col1.dbname = "colDbName"
        col1.dbtype = DataType.varchar.name
        col1.dbtable = "tableDbName"
        createColumn.columns.add(col1)


        // when
        val sqlize = sqlizeAddColumn(DatabaseTrademark.H2, createColumn)

        // then
        val sqlizeresult = sqlize.joinToString(System.lineSeparator())
        assertEquals("""
            ALTER TABLE schema1.tableDbName ADD colDbName NVARCHAR NOT NULL
        """.trimIndent(),sqlizeresult)
    }

}