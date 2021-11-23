package unibz.cs.semint.kprime.adapter.service.liquibase

import liquibase.database.core.H2Database
import liquibase.sqlgenerator.core.AddColumnGenerator
import liquibase.statement.NotNullConstraint
import liquibase.statement.core.AddColumnStatement
import unibz.cs.semint.kprime.adapter.service.MockSqlGeneratorChain
import unibz.cs.semint.kprime.domain.dml.CreateColumn

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
