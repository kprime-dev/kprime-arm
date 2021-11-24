package unibz.cs.semint.kprime.adapter.service.liquibase

import liquibase.pro.packaged.it
import liquibase.sqlgenerator.core.DropColumnGenerator
import liquibase.statement.NotNullConstraint
import liquibase.statement.core.AddColumnStatement
import liquibase.statement.core.DropColumnStatement
import unibz.cs.semint.kprime.adapter.service.MockSqlGeneratorChain
import unibz.cs.semint.kprime.domain.ddl.DataType
import unibz.cs.semint.kprime.domain.ddl.DatabaseTrademark
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
