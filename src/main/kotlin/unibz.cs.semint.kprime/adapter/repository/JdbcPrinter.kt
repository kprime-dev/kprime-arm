package unibz.cs.semint.kprime.adapter.repository

import com.fasterxml.jackson.databind.ObjectMapper
import java.sql.ResultSet
import java.util.LinkedHashMap

object JdbcPrinter {

    fun printResultSet(resultSet: ResultSet):String {
        var result = ""
        val metaData = resultSet.metaData
        val columnCount = metaData.columnCount
        while( resultSet.next()) {
            result += "-----------------------------------------------------"
            for (i in 1..columnCount) {
                //if (i >1 ) print(",")
                //print("${metaData.getColumnName(i)} ${resultSet.getString(i)} ")
                result += "${metaData.getColumnName(i)}: ${resultSet.getString(i)}" + System.lineSeparator()
            }
            //println()
        }
        return result
    }

    fun printJsonResultSet(resultSet: ResultSet):String {
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

    fun printJsonLDResultSet(resultSet: ResultSet):String {
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


}