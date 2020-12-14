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


class QueryJdbcAdapter {

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
        if (datasource.connection == null || datasource.connection?.closed!!) {
            conn.close()
            datasource.remResource(datasource.connection?.id!!)
            println("closed connection ${datasource.connection?.id}")
        }
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

    fun printResultSet(resultSet: ResultSet):String {
        var result = ""
        val metaData = resultSet.metaData
        val columnCount = metaData.columnCount
        while( resultSet.next()) {
            result += "-----------------------------------------------------"
            for (i in 1..columnCount) {
                //if (i >1 ) print(",")
                print("${metaData.getColumnName(i)} ${resultSet.getString(i)} ")
                result += "${metaData.getColumnName(i)}: ${resultSet.getString(i)}" + System.lineSeparator()
            }
            //println()
        }
        return result
    }

    fun printJsonResultSet(resultSet:ResultSet):String {
        val list = mutableListOf<Map<String, String>>()
        val metaData = resultSet.metaData
        val columnCount = metaData.columnCount
        while( resultSet.next()) {
            val obj = LinkedHashMap<String, String>()
            for (i in 1..columnCount) {
                obj.put(metaData.getColumnName(i), resultSet.getString(i))
            }
            list.add(obj)
        }
        val mapper = ObjectMapper()
        val result = mapper.writeValueAsString(list)
        println(result)
        return result
    }

    fun printJsonLDResultSet(resultSet:ResultSet):String {
        val list = mutableListOf<Map<String, String>>()
        val metaData = resultSet.metaData
        val columnCount = metaData.columnCount

        val contextObj = LinkedHashMap<String, String>()
        contextObj.put("ex","http://example.org/vocab#")

        while( resultSet.next()) {
            val obj = LinkedHashMap<String, String>()
            obj.put("@id", "tableurl")
            obj.put("@type", "tablename")
            for (i in 1..columnCount) {
                obj.put("ex:"+metaData.getColumnName(i), resultSet.getString(i))
            }
            list.add(obj)
        }

        val graphObj = LinkedHashMap<String, Any>()
        graphObj.put("@context", contextObj)
        graphObj.put("@graph", list)

        val mapper = ObjectMapper()
        val result = mapper.writeValueAsString(graphObj)
        println(result)
        return result
    }

    /*
    fun available(datasource: DataSource):Boolean {
        val source = datasource
        val user = source.user
        val pass = source.pass
        val path = source.path
        try {
            val connectionProps = Properties()
            connectionProps.put("user", user)
            connectionProps.put("password", pass)
            println("Looking for driver [${source.driver}] for connection [$path] with user [$user].")
            Class.forName(source.driver).newInstance()
            val conn = DriverManager.getConnection(
                    path, connectionProps)
            val schema = conn.schema
            conn.close()
        } catch (ex: Exception) {
            return false
        }
        return true
    }

     */

}