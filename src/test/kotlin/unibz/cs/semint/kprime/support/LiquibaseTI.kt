package unibz.cs.semint.kprime.support

import liquibase.change.AddColumnConfig
import liquibase.change.ColumnConfig
import liquibase.change.core.AddColumnChange
import liquibase.change.core.DropColumnChange
import liquibase.database.core.H2Database
import liquibase.database.core.MockDatabase
import liquibase.sqlgenerator.SqlGenerator
import liquibase.sqlgenerator.SqlGeneratorChain
import liquibase.sqlgenerator.core.AddColumnGenerator
import liquibase.sqlgenerator.core.DropColumnGenerator
import liquibase.statement.AutoIncrementConstraint
import liquibase.statement.NotNullConstraint
import liquibase.statement.SqlStatement
import liquibase.statement.core.AddColumnStatement
import liquibase.statement.core.DropColumnStatement
import org.junit.Test
import java.util.*


class LiquibaseTI {

    class MockSqlGeneratorChain: SqlGeneratorChain<SqlStatement>(TreeSet<SqlGenerator<SqlStatement>>()) { }

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


        val addCol1 = AddColumnStatement(null, null, "table1", "column1", "INT", null, NotNullConstraint(), AutoIncrementConstraint())
        val addCol2 = AddColumnStatement(null, null, "table1", "column2", "INT", null, NotNullConstraint())
        val addStatements = AddColumnStatement(addCol1, addCol2)

        val generateSql = AddColumnGenerator().generateSql(
                addStatements,
                H2Database(),
                MockSqlGeneratorChain())
        for (sqlStat in generateSql) {
            println(sqlStat.toSql())
        }

    }


    @Test
    fun test_drop_column() {
        val change = DropColumnChange()
        val column1 = ColumnConfig()
        column1.name = "column1"
        change.addColumn(column1)
        val column2 = ColumnConfig()
        column2.name = "column2"
        change.addColumn(column2)

        val statements = change.generateStatements(MockDatabase())
        for (statement in statements) {
            println(statement.toString())
        }

        val dropColStatements = DropColumnStatement(null,null,"table1","colname")

        val generateSql = DropColumnGenerator().generateSql(
                dropColStatements,
                H2Database(),
                MockSqlGeneratorChain()
        )
        for (sqlStat in generateSql) {
            println(sqlStat.toSql())
        }
    }

}