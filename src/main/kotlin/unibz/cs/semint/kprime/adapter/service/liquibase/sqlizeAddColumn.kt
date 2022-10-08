package unibz.cs.semint.kprime.adapter.service.liquibase

import liquibase.sqlgenerator.core.AddColumnGenerator
import liquibase.statement.NotNullConstraint
import liquibase.statement.core.AddColumnStatement
import unibz.cs.semint.kprime.adapter.service.MockSqlGeneratorChain
import unibz.cs.semint.kprime.domain.db.DataType
import unibz.cs.semint.kprime.domain.db.DatabaseTrademark
import unibz.cs.semint.kprime.domain.ddl.CreateColumn

fun sqlizeAddColumn(dbTrademark: DatabaseTrademark, createColumns: CreateColumn): List<String> {
    val statements = createColumns.columns.map { AddColumnStatement(
                createColumns.catalog,
                createColumns.schema,
                it.dbtable,
                it.dbname,
                liquibaseDataType(DataType.fromString(it.dbtype)).toString(),
                null,
                NotNullConstraint()
        )
    }
    val addStatements = AddColumnStatement(statements)
    val generateSql = AddColumnGenerator().generateSql(
            addStatements,
            liquibaseDbByTrademark(dbTrademark),
            MockSqlGeneratorChain())
    return generateSql.map { it.toSql() }
}
