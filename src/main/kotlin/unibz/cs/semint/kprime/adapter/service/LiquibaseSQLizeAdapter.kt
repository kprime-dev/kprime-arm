package unibz.cs.semint.kprime.adapter.service

import liquibase.sqlgenerator.SqlGenerator
import liquibase.sqlgenerator.SqlGeneratorChain
import liquibase.statement.SqlStatement
import unibz.cs.semint.kprime.adapter.service.liquibase.sqlizeAddColumn
import unibz.cs.semint.kprime.adapter.service.liquibase.sqlizeAddTable
import unibz.cs.semint.kprime.domain.ddl.DatabaseTrademark
import unibz.cs.semint.kprime.domain.dml.ChangeSet
import unibz.cs.semint.kprime.usecase.service.SQLizeServiceI
import java.util.*

class MockSqlGeneratorChain: SqlGeneratorChain<SqlStatement>(TreeSet<SqlGenerator<SqlStatement>>()) { }

class LiquibaseSQLizeAdapter: SQLizeServiceI {

    override fun sqlize(dbTrademark: DatabaseTrademark, change: ChangeSet): List<String> {
        val result = mutableListOf<String>()
        result.addAll(change.createColumn.flatMap { sqlizeAddColumn(dbTrademark, it) })
        result.addAll(change.createTable.flatMap { sqlizeAddTable(dbTrademark, it) })
        return result
    }

}