package unibz.cs.semint.kprime.adapter.jackson

import org.junit.Test
import unibz.cs.semint.kprime.adapter.repository.JdbcPrinter

class JdbcPrinterTest {

    @Test
    fun test_printJsonResultSet() {
        // given
        val resultList = listOf( mapOf(
            "name" to "nick", "surname" to null, "age" to 33.5
        ))
        val referenced = mapOf("alfa" to "beta")
        val header = mapOf( "version" to "1.0")
        // when
        val result = JdbcPrinter.printJsonResultList(header, resultList, referenced,
            externalKeys = emptyMap(), externalSources = emptyMap())
        // then
        println(result)
    }

    @Test
    fun test_printJsonResultSetWithExternalKeys() {
        // given
        val resultList = listOf( mapOf(
            "name" to "nick", "surname" to null, "age" to 33.5
        ))
        val referenced = mapOf("alfa" to "beta")
        val header = mapOf( "version" to "1.0")
        val externalKeys = mapOf("name" to "http://localhost:7777/project/kprime-case-confucius-mysql/ldata/root/base/abs_daily_monitoring?monitoring_date_id=")
        // when
        val result = JdbcPrinter.printJsonResultList(header, resultList, referenced, externalKeys, emptyMap())
        // then
        println(result)
    }

    @Test
    fun test_printJsonResultSetWithExternalSources() {
        // given
        val resultList = listOf( mapOf(
            "name" to "nick", "surname" to null, "age" to 33.5
        ))
        val referenced = mapOf("alfa" to "beta")
        val header = mapOf( "version" to "1.0")
        val externalKeys = mapOf("name" to "http://localhost:7777/project/kprime-case-confucius-mysql/ldata/root/base/abs_daily_monitoring?monitoring_date_id=")
        val externalSources = mapOf("source1" to "http://localhost:7777/project/kprime-case-confucius-mysql/ldata/root/base/source?ab=alfa")
        // when
        val result = JdbcPrinter.printJsonResultList(header, resultList, referenced, externalKeys, externalSources)
        // then
        println(result)
    }

}