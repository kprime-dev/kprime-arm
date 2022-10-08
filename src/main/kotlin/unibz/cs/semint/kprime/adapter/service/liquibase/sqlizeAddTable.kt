package unibz.cs.semint.kprime.adapter.service.liquibase

import liquibase.sqlgenerator.core.CreateTableGenerator
import liquibase.statement.core.CreateTableStatement
import unibz.cs.semint.kprime.adapter.service.MockSqlGeneratorChain
import unibz.cs.semint.kprime.domain.db.DataType
import unibz.cs.semint.kprime.domain.db.DatabaseTrademark
import unibz.cs.semint.kprime.domain.dml.CreateTable

fun sqlizeAddTable(dbTrademark: DatabaseTrademark, createTable: CreateTable): List<String> {
    val createTableStatement = CreateTableStatement(createTable.catalog,createTable.schema,createTable.name)
    createTable.columns.map {
        createTableStatement.addColumn(
                it.dbname,
                liquibaseDataType(DataType.fromString(it.dbtype))
        )
    }
    return CreateTableGenerator().generateSql(
                createTableStatement,
                liquibaseDbByTrademark(dbTrademark),
                MockSqlGeneratorChain())
            .map { it.toSql() }
}

