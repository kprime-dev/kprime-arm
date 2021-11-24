package unibz.cs.semint.kprime.adapter.service.liquibase

import liquibase.database.Database
import liquibase.database.core.H2Database
import liquibase.database.core.PostgresDatabase
import liquibase.database.core.UnsupportedDatabase
import liquibase.datatype.LiquibaseDataType
import liquibase.datatype.core.*
import unibz.cs.semint.kprime.domain.ddl.DataType
import unibz.cs.semint.kprime.domain.ddl.DatabaseTrademark

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