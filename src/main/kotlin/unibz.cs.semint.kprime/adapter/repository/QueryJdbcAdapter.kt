package unibz.cs.semint.kprime.adapter.repository

import unibz.cs.semint.kprime.domain.DataSource
import unibz.cs.semint.kprime.domain.dql.Query
import unibz.cs.semint.kprime.usecase.SQLizeUseCase
import java.sql.DriverManager
import java.sql.ResultSet
import java.util.*

class QueryJdbcAdapter {

    fun query(datasource: DataSource, query: Query) {

        var sqlquery = SQLizeUseCase().sqlize(query)
        return query(datasource,sqlquery)
    }

    fun query(datasource: DataSource, sqlquery: String) {
        val source = datasource
        val user = source.user
        val pass = source.pass
        val path = source.path

        val connectionProps = Properties()
        connectionProps.put("user", user)
        connectionProps.put("password", pass)
        println("Looking for driver [${source.driver}] for connection [$path] with user [$user].")
        Class.forName(source.driver).newInstance()
        val conn = DriverManager.getConnection(
                path, connectionProps)


        conn.autoCommit=true
        val sqlnative = conn.nativeSQL(sqlquery)
        val prepareStatement = conn.prepareStatement(sqlnative)
        val resultSet = prepareStatement.executeQuery()
        printResultSet(resultSet)
        resultSet.close()
    }

    private fun printResultSet(resultSet: ResultSet) {
        val metaData = resultSet.metaData
        val columnCount = metaData.columnCount
        while( resultSet.next()) {
            for (i in 1..columnCount) {
                if (i >1 ) print(",")
                print("${resultSet.getString(i)} ${metaData.getColumnName(i)}")
            }
            println()
        }
    }
}