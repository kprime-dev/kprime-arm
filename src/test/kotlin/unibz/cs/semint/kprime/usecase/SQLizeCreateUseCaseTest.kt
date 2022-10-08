package unibz.cs.semint.kprime.usecase

import org.junit.Test
import unibz.cs.semint.kprime.domain.db.Column
import unibz.cs.semint.kprime.domain.dml.ChangeSet
import unibz.cs.semint.kprime.domain.dml.CreateColumn
import unibz.cs.semint.kprime.usecase.common.SQLizeCreateUseCase
import unibz.cs.semint.kprime.usecase.common.UnSQLizeSelectUseCase
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SQLizeCreateUseCaseTest {

    @Test
    fun test_add_columns() {
        // given
        val sqlize = SQLizeCreateUseCase()
        val changeSet = ChangeSet()
        val columnsToAdd = CreateColumn()
        columnsToAdd.name = "table1"
        val col1 = Column(name = "col1", id = "id1", dbname = "dbname1")
        col1.dbtype = "varchar"
        columnsToAdd.columns.add(col1)
        val col2 = Column(name = "col2", id = "id1", dbname = "dbname1")
        col2.dbtype = "varchar"
        columnsToAdd.columns.add(col2)
        val createColumns = ArrayList<CreateColumn>()
        createColumns.add(columnsToAdd)
        changeSet.createColumn = createColumns
        // when
        val createCommands = sqlize.createCommands(changeSet)
        // then
        assertEquals(2,createCommands.size)
        assertEquals("ALTER TABLE table1 ADD col1 VARCHAR NOT NULL",
                createCommands[0])
        assertEquals("ALTER TABLE table1 ADD col2 VARCHAR NOT NULL",
                createCommands[1])
    }

    @Test
    fun test_create_one_sql_mapping(){
        val unsqlize = UnSQLizeSelectUseCase()
        val sqlize = SQLizeCreateUseCase()
        val changeset = ChangeSet()
        var mapping = unsqlize.fromsql("myquery","SELECT DISTINCT * FROM Person")
        assertTrue(mapping.select.distinct)
        changeset.createMapping.add(mapping)
        val createTableMappings = sqlize.createTableMappings(changeset)
        assertEquals("""
CREATE TABLE public.myquery AS
SELECT DISTINCT *
FROM   Person
 LIMIT 10
        """.trimIndent(),createTableMappings[0])
    }
}