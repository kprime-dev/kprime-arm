package unibz.cs.semint.kprime.usecase

import org.junit.Test
import unibz.cs.semint.kprime.domain.ddl.Column
import unibz.cs.semint.kprime.domain.ddl.Table
import unibz.cs.semint.kprime.domain.dml.ChangeSet
import unibz.cs.semint.kprime.domain.dml.CreateColumn
import unibz.cs.semint.kprime.usecase.common.SQLizeCreateUseCase
import java.util.*
import kotlin.test.assertEquals

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
}