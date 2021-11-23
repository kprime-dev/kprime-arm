package unibz.cs.semint.kprime.adapter.service.liquibase

import liquibase.database.Database
import liquibase.database.core.H2Database
import liquibase.database.core.PostgresDatabase
import liquibase.database.core.UnsupportedDatabase
import liquibase.datatype.LiquibaseDataType
import liquibase.datatype.core.*
import liquibase.sqlgenerator.core.CreateTableGenerator
import liquibase.statement.core.CreateTableStatement
import unibz.cs.semint.kprime.adapter.service.MockSqlGeneratorChain
import unibz.cs.semint.kprime.domain.ddl.DataType
import unibz.cs.semint.kprime.domain.ddl.DatabaseTrademark
import unibz.cs.semint.kprime.domain.dml.CreateTable

fun sqlizeAddTable(dbTrademark: DatabaseTrademark, createTable: CreateTable): List<String> {
    val createTableStatement = CreateTableStatement(createTable.catalog,createTable.schema,createTable.name)
    createTable.columns.map { createTableStatement.addColumn(it.dbname,liquibaseDataType(DataType.fromString(it.dbtype))) }
    return CreateTableGenerator().generateSql(
                createTableStatement,
                liquibaseDbByTrademark(dbTrademark),
                MockSqlGeneratorChain())
            .map { it.toSql() }
}

fun liquibaseDataType(dbDataType: DataType): LiquibaseDataType? {
    return when(dbDataType) {
        DataType.int -> IntType()
        DataType.varchar -> NVarcharType()
        DataType.timestamp -> TimestampType()
        DataType.date -> DateType()
        DataType.time -> TimeType()
        DataType.currency -> CurrencyType()
        DataType.boolean -> BooleanType()
        DataType.bigint -> BigIntType()
        DataType.unknown -> UnknownType()
    }
}

fun liquibaseDbByTrademark(dbTrademark: DatabaseTrademark): Database? {
    return when(dbTrademark) {
        DatabaseTrademark.PSQL -> PostgresDatabase()
        DatabaseTrademark.H2 -> H2Database()
        DatabaseTrademark.unknown -> UnsupportedDatabase()
    }
}