package unibz.cs.semint.kprime.adapter.service.liquibase

import liquibase.sqlgenerator.core.DropTableGenerator
import liquibase.statement.core.DropTableStatement
import unibz.cs.semint.kprime.adapter.service.MockSqlGeneratorChain
import unibz.cs.semint.kprime.domain.db.DatabaseTrademark
import unibz.cs.semint.kprime.domain.dml.DropTable

fun sqlizeDropTable(dbTrademark: DatabaseTrademark, dropTables: List<DropTable>): List<String> {
    val dropStatements =  dropTables.map { DropTableStatement(
                it.catalog,
                it.schemaName,
                it.tableName,
                it.cascadeConstraints?:false
        )
    }
    val sqls =
            dropStatements.flatMap {
            DropTableGenerator().generateSql(
                    it,
                    liquibaseDbByTrademark(dbTrademark),
                    MockSqlGeneratorChain()).toList()
    }
    return sqls.map { it.toSql() }

}
