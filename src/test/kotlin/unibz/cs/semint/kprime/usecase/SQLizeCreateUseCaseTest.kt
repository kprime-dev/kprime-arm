package unibz.cs.semint.kprime.usecase

import org.junit.Test
import unibz.cs.semint.kprime.domain.db.Column
import unibz.cs.semint.kprime.domain.ddl.ChangeSet
import unibz.cs.semint.kprime.domain.ddl.CreateColumn
import unibz.cs.semint.kprime.domain.dql.Mapping
import unibz.cs.semint.kprime.domain.dql.Query
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
        changeset.createMapping.add(Mapping.fromQuery(mapping))
        val createTableMappings = sqlize.createTableMappings(changeset)
        assertEquals("""
CREATE TABLE public.myquery AS
SELECT DISTINCT *
FROM   Person
        """.trimIndent(),createTableMappings[0])
    }

    @Test
    fun test_stripOptions() {
        // given
        val query = Query()
        val sql = "SELECT -target=confu DISTINCT * FROM Person; -source=confu  -sink=confu"
        // when
        val lineStrippedOptions = UnSQLizeSelectUseCase().stripOptions(query, sql)
        // then
        assertEquals("-target=confu", query.options?.get(0))
        assertEquals("-source=confu", query.options?.get(1))
        assertEquals("-sink=confu", query.options?.get(2))
        assertEquals("SELECT DISTINCT * FROM Person;", lineStrippedOptions)
    }
}