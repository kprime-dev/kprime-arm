package unibz.cs.semint.kprime.scenario.sakila

import org.junit.Test
import unibz.cs.semint.kprime.adapter.repository.MetaSchemaJdbcAdapter
import unibz.cs.semint.kprime.adapter.service.XMLSerializerJacksonAdapter
import unibz.cs.semint.kprime.domain.datasource.DataSource
import unibz.cs.semint.kprime.usecase.common.MetaSchemaReadUseCase
import kotlin.test.assertEquals

class SakilaMetaTI {

    @Test
    fun test_read_sakila_meta() {
       //given
        val type = "psql"
        val name = "sakila-source"
        val driver = "org.postgresql.Driver"
        val path = "jdbc:postgresql://localhost:5432/sakila"
        val user = System.getenv()["sakila_user"] ?: ""
        val pass = System.getenv()["sakila_pass"] ?: ""
        val sakilaSource = DataSource(type,name,driver,path,user,pass)
        // when
        val result = MetaSchemaReadUseCase().doit(sakilaSource,"read-meta-schema sakila-source", MetaSchemaJdbcAdapter(), XMLSerializerJacksonAdapter())
        // then
        assertEquals("read-meta-schema done.",result.message)
    }
}