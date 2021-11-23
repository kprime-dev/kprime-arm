package unibz.cs.semint.kprime.adapter.service

import liquibase.database.core.H2Database
import liquibase.sqlgenerator.SqlGenerator
import liquibase.sqlgenerator.SqlGeneratorChain
import liquibase.sqlgenerator.core.AddColumnGenerator
import liquibase.statement.NotNullConstraint
import liquibase.statement.SqlStatement
import liquibase.statement.core.AddColumnStatement
import unibz.cs.semint.kprime.domain.dml.ChangeSet
import unibz.cs.semint.kprime.domain.dml.CreateColumn
import unibz.cs.semint.kprime.usecase.service.SQLizeServiceI
import java.util.*

class MockSqlGeneratorChain: SqlGeneratorChain<SqlStatement>(TreeSet<SqlGenerator<SqlStatement>>()) { }

class LiquibaseSQLizeAdapter: SQLizeServiceI {

    override fun sqlize(change: ChangeSet): List<String> {
        return change.createColumn.flatMap { sqlizeAddColumn(it) }
    }

    fun sqlizeAddColumn(createColumns: CreateColumn): List<String> {
        val statements = createColumns.columns.map {
            AddColumnStatement(
                    createColumns.catalog,
                    createColumns.schema,
                    it.dbtable,
                    it.dbname,
                    it.dbtype,
                    null,
                    NotNullConstraint()
            )
        }
        val addStatements = AddColumnStatement(statements)
        val generateSql = AddColumnGenerator().generateSql(
                addStatements,
                H2Database(),
                MockSqlGeneratorChain())
        return generateSql.map { it.toSql() }
    }
}