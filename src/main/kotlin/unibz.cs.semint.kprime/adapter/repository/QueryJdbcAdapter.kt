package unibz.cs.semint.kprime.adapter.repository

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import unibz.cs.semint.kprime.domain.DataSource
import unibz.cs.semint.kprime.domain.dql.Query
import unibz.cs.semint.kprime.usecase.common.SQLizeSelectUseCase
import java.io.StringWriter
import java.sql.DriverManager
import java.sql.ResultSet
import java.util.*


class QueryJdbcAdapter {

    fun query(datasource: DataSource, query: Query):String {
        var sqlquery = SQLizeSelectUseCase().sqlize(query)
        return query(datasource,sqlquery)
    }

    fun query(datasource: DataSource, sqlquery: String):String {
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
        val result =printJsonResultSet(resultSet)
        resultSet.close()
        conn.close()
        return result
    }

    fun printResultSet(resultSet: ResultSet):String {
        var result = ""
        val metaData = resultSet.metaData
        val columnCount = metaData.columnCount
        while( resultSet.next()) {
            result += "-----------------------------------------------------"
            for (i in 1..columnCount) {
                //if (i >1 ) print(",")
                //print("${resultSet.getString(i)} ${metaData.getColumnName(i)}")
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
        mapper.enable(SerializationFeature.INDENT_OUTPUT)
        val stringWriter = StringWriter()
        mapper.writeValue(stringWriter, list)
        return stringWriter.toString()
    }

    fun create(datasource: DataSource, sqlcreate: String) {
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
        val createStatement = conn.createStatement()
        val resultSet = createStatement.executeUpdate(sqlcreate)
        println(" Create result : $resultSet")
        createStatement.close()
        conn.close()
    }



}