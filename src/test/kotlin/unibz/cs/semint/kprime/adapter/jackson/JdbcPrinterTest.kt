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
        val result = JdbcPrinter.printJsonResultList(header, resultList,referenced)
        // then
        println(result)
    }
}