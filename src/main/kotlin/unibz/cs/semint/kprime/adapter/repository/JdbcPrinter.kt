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
                result += "${metaData.getColumnLabel(i)}: ${resultSet.getString(i)}" + System.lineSeparator()
            }
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


}