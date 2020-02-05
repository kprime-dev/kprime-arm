package unibz.cs.semint.kprime.usecase.meta

import org.junit.Test
import unibz.cs.semint.kprime.adapter.repository.MetaSchemaJdbcAdapter
import unibz.cs.semint.kprime.adapter.repository.QueryJdbcAdapter
import unibz.cs.semint.kprime.adapter.service.XMLSerializerJacksonAdapter
import unibz.cs.semint.kprime.domain.DataSource
import unibz.cs.semint.kprime.domain.ddl.Database
import unibz.cs.semint.kprime.domain.dql.Attribute
import unibz.cs.semint.kprime.domain.dql.From
import unibz.cs.semint.kprime.domain.dql.Query
import unibz.cs.semint.kprime.usecase.MetaSchemaReadUseCase
import kotlin.test.assertEquals

class SakilaQueryTI {

    @Test
    fun test_read_sakila_query_string() {
        //given
        val type = "psql"
        val name = "sakila-source"
        val driver = "org.postgresql.Driver"
        val path = "jdbc:postgresql://localhost:5432/sakila"
        val user = "npedot"
        val pass = "password"
        //val user = "sammy"
        //val pass = "pass"
        val sakilaSource = DataSource(type,name,driver,path,user,pass)
        // when
        val result = QueryJdbcAdapter().query(sakilaSource,"select * from film")
        // then
        //assertEquals("read-meta-schema done.","")
    }


    @Test
    fun test_read_sakila_query_films() {
        //given
        val type = "psql"
        val name = "sakila-source"
        val driver = "org.postgresql.Driver"
        val path = "jdbc:postgresql://localhost:5432/sakila"
        val user = "npedot"
        val pass = "password"
        //val user = "sammy"
        //val pass = "pass"
        val sakilaSource = DataSource(type,name,driver,path,user,pass)
        // when
        val sqlquery = Query.simpleFilmQueryFixture("film")
        val result = QueryJdbcAdapter().query(sakilaSource, sqlquery)
        // then
        //assertEquals("read-meta-schema done.","")
    }


    @Test
    fun test_read_sakila_query_italian_films() {
        //given
        val type = "psql"
        val name = "sakila-source"
        val driver = "org.postgresql.Driver"
        val path = "jdbc:postgresql://localhost:5432/sakila"
        val user = "npedot"
        val pass = "password"
        //val user = "sammy"
        //val pass = "pass"
        val sakilaSource = DataSource(type,name,driver,path,user,pass)
        // when
        val condition = "language_id=2"
        val sqlquery = Query.simpleFilmQueryFixture("film",condition)
        val result = QueryJdbcAdapter().query(sakilaSource, sqlquery)
        // then
        //assertEquals("read-meta-schema done.","")
    }


}