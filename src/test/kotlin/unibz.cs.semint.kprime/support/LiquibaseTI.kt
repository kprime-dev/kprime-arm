package unibz.cs.semint.kprime.support

import liquibase.change.AddColumnConfig
import liquibase.change.core.AddColumnChange
import liquibase.database.core.H2Database
import liquibase.database.core.MockDatabase
import liquibase.sqlgenerator.SqlGenerator
import liquibase.sqlgenerator.SqlGeneratorChain
import liquibase.sqlgenerator.core.AddColumnGenerator
import liquibase.statement.AutoIncrementConstraint
import liquibase.statement.NotNullConstraint
import liquibase.statement.SqlStatement
import liquibase.statement.core.AddColumnStatement
import org.junit.Test
import java.util.*

class LiquibaseTI {

    class MockSqlGeneratorChain: SqlGeneratorChain<SqlStatement>(TreeSet<SqlGenerator<SqlStatement>>()) {

    }
    @Test
    fun test_change_sql() {
        val addColChange = AddColumnChange()
        val addColumnConfig = AddColumnConfig()
        addColumnConfig.name = "column1"
        addColumnConfig.type = "int"
        addColChange.addColumn(addColumnConfig)
        val statements = addColChange.generateStatements(MockDatabase())
        for (sqlStat in statements) {
            println(sqlStat.toString())
        }


        val addCol1 = AddColumnStatement(null, null, "table1", "column1", "INT", null, NotNullConstraint(),AutoIncrementConstraint())
        val addCol2 = AddColumnStatement(null, null, "table1", "column2", "INT", null, NotNullConstraint())
        val addStatements = AddColumnStatement(addCol1,addCol2)

        val generateSql = AddColumnGenerator().generateSql(
                                                                addStatements,
                                                                H2Database(),
                                                                MockSqlGeneratorChain())
        for (sqlStat in generateSql) {
            println(sqlStat.toSql())
        }

    }
}