package unibz.cs.semint.kprime.adapter.service.liquibase

import liquibase.sqlgenerator.core.DropColumnGenerator
import liquibase.statement.core.DropColumnStatement
import unibz.cs.semint.kprime.adapter.service.MockSqlGeneratorChain
import unibz.cs.semint.kprime.domain.db.DatabaseTrademark
import unibz.cs.semint.kprime.domain.dml.DropColumn

fun sqlizeDropColumn(dbTrademark: DatabaseTrademark, dropColumns: List<DropColumn>): List<String> {
    val statements = dropColumns.map { DropColumnStatement(
                it.catalog,
                it.schema,
                it.tableName,
                it.name
        )}
    val dropStatements = DropColumnStatement(statements)
    val generateSql = DropColumnGenerator().generateSql(
            dropStatements,
            liquibaseDbByTrademark(dbTrademark),
            MockSqlGeneratorChain())
    return generateSql.map { it.toSql() }
}
