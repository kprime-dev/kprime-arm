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

    fun printJsonResultList(resultSet: ResultSet):String {
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

    fun printJsonResultList(header: Map<String,Any?>, result: ResultList, referencedSets: Map<String,Any?>,
                            externalKeys: Map<String,String?>, externalSources: Map<String,String>):String {
        val a =  try {
            val list = mutableListOf<Map<String, Any?>>()
            if (result.isEmpty()) return ""
            for (row in result) {
                val obj = LinkedHashMap<String, Any?>()
                for (col in row) {
                    obj[col.key] = if (externalKeys[col.key]!=null) externalKeys[col.key]+col.value else col.value
                    obj.putAll(referencedSets)
                }
                for (externalSource in externalSources) {
                    val keyName = externalSource.value.substringAfterLast("=")
                    obj[externalSource.key] = externalSource.value.substringBeforeLast("=") + "=" + obj[keyName]
                }
                list.add(obj)
            }
            val mapHeader = mapOf(
                "header" to header,
                "data" to list
            )
            val mapper = ObjectMapper()
            mapper.writeValueAsString(mapHeader)
        } catch(e : Exception) {
            e.printStackTrace()
        }
        return a.toString()
    }

}