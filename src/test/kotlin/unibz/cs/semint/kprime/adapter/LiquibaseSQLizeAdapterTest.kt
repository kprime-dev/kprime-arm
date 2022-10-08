package unibz.cs.semint.kprime.adapter

import org.junit.Test
import unibz.cs.semint.kprime.adapter.service.LiquibaseSQLizeAdapter
import unibz.cs.semint.kprime.domain.db.Column
import unibz.cs.semint.kprime.domain.db.DataType
import unibz.cs.semint.kprime.domain.db.DatabaseTrademark
import unibz.cs.semint.kprime.domain.ddl.ChangeSet
import unibz.cs.semint.kprime.domain.ddl.CreateColumn
import unibz.cs.semint.kprime.domain.ddl.CreateTable
import kotlin.test.assertEquals

class LiquibaseSQLizeAdapterTest {

    @Test
    fun test_add_column_and_table_change_liquibase_sqlize() {
        // given
        val change = ChangeSet()

        val createColumn = CreateColumn()
        createColumn.catalog = "cat1"
        createColumn.schema = "schema1"
        val col1 = Column("colName","colId")
        col1.dbname = "colDbName"
        col1.dbtype = DataType.varchar.name
        col1.dbtable = "tableDbName"
        createColumn.columns.add(col1)
        change.createColumn.add(createColumn)

        val createTable = CreateTable()
        createTable.catalog = "cat1"
        createTable.schema = "schema1"
        createTable.name = "tab1"
        val col11 = Column("colName11","colId11")
        col11.dbname = "colDbName11"
        col11.dbtype = DataType.int.name
        col11.dbtable = "tableDbName11"
        createTable.columns.add(col11)
        change.createTable.add(createTable)

        // when
        val sqlize = LiquibaseSQLizeAdapter().sqlize(DatabaseTrademark.H2, change)

        // then
        val sqlizeresult = sqlize.joinToString(System.lineSeparator())
        assertEquals("""
            ALTER TABLE schema1.tableDbName ADD colDbName NVARCHAR NOT NULL
            CREATE TABLE schema1.tab1 (colDbName11 INT)
        """.trimIndent(),sqlizeresult)
    }

}