package unibz.cs.semint.kprime.adapter.liquibase

import org.junit.Test
import unibz.cs.semint.kprime.adapter.service.liquibase.sqlizeDropColumn
import unibz.cs.semint.kprime.domain.db.DatabaseTrademark
import unibz.cs.semint.kprime.domain.ddl.DropColumn
import kotlin.test.assertEquals

class SqlizeDropColumnTest {

    @Test
    fun test_drop_column_liquibase_sqlize() {
        // given
        val dropColumn1 = DropColumn()
        dropColumn1.catalog = "cat1"
        dropColumn1.schema = "schema1"
        dropColumn1.name = "colDbName"
        dropColumn1.tableName = "tableDbName"

        val dropColumn2 = DropColumn()
        dropColumn2.catalog = "cat1"
        dropColumn2.schema = "schema1"
        dropColumn2.name = "colDbName2"
        dropColumn2.tableName = "tableDbName"

        // when
        val sqlize = sqlizeDropColumn(DatabaseTrademark.H2, listOf(dropColumn1,dropColumn2))

        // then
        val sqlizeresult = sqlize.joinToString(System.lineSeparator())
        assertEquals("""
            ALTER TABLE schema1.tableDbName DROP COLUMN colDbName
            ALTER TABLE schema1.tableDbName DROP COLUMN colDbName2
        """.trimIndent(),sqlizeresult)
    }

}