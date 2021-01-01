package unibz.cs.semint.kprime.adapter.repository

import com.fasterxml.jackson.databind.ObjectMapper
import unibz.cs.semint.kprime.domain.DataSource
import unibz.cs.semint.kprime.domain.dql.Query
import unibz.cs.semint.kprime.usecase.common.SQLizeSelectUseCase
import java.lang.IllegalArgumentException
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.util.*


class JdbcAdapter {

    private var formatted=false
    constructor() {}
    constructor(formatted: Boolean) {
        this.formatted = formatted
    }

    fun query(datasource: DataSource, query: Query, printer: (rs :ResultSet)->String) :String {
        var sqlquery = SQLizeSelectUseCase().sqlize(query)
        return query(datasource,sqlquery,printer)
    }

    fun query(datasource: DataSource, sqlquery: String, printer:(rs:ResultSet)->String):String {
        var conn: Connection = openConnection(datasource) ?: throw IllegalArgumentException("No connection")
        val sqlnative = conn.nativeSQL(sqlquery)
        val prepareStatement = conn.prepareStatement(sqlnative)
        val resultSet = prepareStatement.executeQuery()
        val result =printer(resultSet)
        resultSet.close()
        closeConnection(datasource, conn)
        return result
    }

    fun create(datasource: DataSource, sqlcreate: String) {
        val conn = openConnection(datasource) ?: throw IllegalArgumentException("No connection")
        val createStatement = conn.createStatement()
        val resultSet = createStatement.executeUpdate(sqlcreate)
        println(" Create result : $resultSet")
        createStatement.close()
        closeConnection(datasource,conn)
    }


    private fun closeConnection(datasource: DataSource, conn: Connection) {
        println(" Check close connection ${datasource.connection?.id}: ${datasource.connection?.closed}")
        if (datasource.connection == null || datasource.connection?.closed!!) {
            conn.close()
            datasource.remResource(datasource.connection?.id!!)
            println("closed connection ${datasource.connection?.id}")
        }
    }

    fun commit(datasource: DataSource) {
        val conn = openConnection(datasource) ?: throw IllegalArgumentException("No connection")
        conn.commit()
    }

    private fun openConnection(datasource: DataSource): Connection? {
        val source = datasource
        val user = source.user
        val pass = source.pass
        val path = source.path

        println("Looking for... driver [${source.driver}] for connection [$path] with user [$user].")
        Class.forName(source.driver).newInstance()

        var conn: Connection?
        println("Connection preparing...")
        if (datasource.connection == null) {
            println("Connection NEW")
            val connectionProps = Properties()
            connectionProps.put("user", user)
            connectionProps.put("password", pass)
            conn = DriverManager.getConnection(
                    path, connectionProps)
            conn.autoCommit = true
        } else {
            println("Connection from POOL")
            var resource = datasource.getResource(datasource.connection?.id!!)
            if (resource == null) {
                val connectionProps = Properties()
                connectionProps.put("user", datasource.connection?.username)
                connectionProps.put("password", datasource.connection?.pass)
                conn = DriverManager.getConnection(
                        path, connectionProps)
                conn.autoCommit = datasource.connection?.commited!!
                datasource.setResource(datasource.connection?.id!!, conn)
            } else {
                conn = resource as Connection
            }

        }
        return conn
    }


}