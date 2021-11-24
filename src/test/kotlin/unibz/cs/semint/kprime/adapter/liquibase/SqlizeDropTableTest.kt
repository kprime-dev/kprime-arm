package unibz.cs.semint.kprime.adapter.liquibase

import org.junit.Test
import unibz.cs.semint.kprime.adapter.service.liquibase.sqlizeDropColumn
import unibz.cs.semint.kprime.adapter.service.liquibase.sqlizeDropTable
import unibz.cs.semint.kprime.domain.ddl.DatabaseTrademark
import unibz.cs.semint.kprime.domain.dml.DropColumn
import unibz.cs.semint.kprime.domain.dml.DropTable
import kotlin.test.assertEquals

class SqlizeDropTableTest {

    @Test
    fun test_drop_table_liquibase_sqlize() {
        // given
        val dropTable1 = DropTable()
        dropTable1.catalog = "cat1"
        dropTable1.schemaName = "schema1"
        dropTable1.cascadeConstraints = true
        dropTable1.tableName = "tableDbName"

        val dropTable2 = DropTable()
        dropTable2.catalog = "cat1"
        dropTable2.schemaName = "schema1"
        dropTable2.tableName = "tableDbName"

        // when
        val sqlize = sqlizeDropTable(DatabaseTrademark.H2, listOf(dropTable1,dropTable2))

        // then
        val sqlizeresult = sqlize.joinToString(System.lineSeparator())
        assertEquals("""
            DROP TABLE schema1.tableDbName CASCADE
            DROP TABLE schema1.tableDbName
        """.trimIndent(),sqlizeresult)
    }

}