package unibz.cs.semint.kprime.usecase.meta

import org.junit.Test
import unibz.cs.semint.kprime.adapter.repository.QueryJdbcAdapter
import unibz.cs.semint.kprime.domain.DataSource
import unibz.cs.semint.kprime.domain.dql.Query

class SakilaQueryTI {

    val type = "psql"
    val name = "sakila-source"
    val driver = "org.postgresql.Driver"
    val path = "jdbc:postgresql://localhost:5432/sakila"
    val user = System.getenv()["sakila_user"]?:""//"npedot"
    val pass = System.getenv()["sakila_pass"]?:""//"password"
    //val user = "sammy"
    //val pass = "pass"

    @Test
    fun test_read_sakila_query_string() {
        //given
        val sakilaSource = DataSource(type,name,driver,path,user,pass)
        // when
        val result = QueryJdbcAdapter().query(sakilaSource,"select * from film")
        // then
        //assertEquals("read-meta-schema done.","")
    }


    @Test
    fun test_read_sakila_query_films() {
        //given
        val sakilaSource = DataSource(type,name,driver,path,user,pass)
        // when
        val sqlquery = Query.build("film")
        val result = QueryJdbcAdapter().query(sakilaSource, sqlquery)
        // then
        //assertEquals("read-meta-schema done.","")
    }


    @Test
    fun test_read_sakila_query_italian_films() {
        //given
        val sakilaSource = DataSource(type,name,driver,path,user,pass)
        // when
        val condition = "language_id=2"
        val sqlquery = Query.build("film",condition)
        val result = QueryJdbcAdapter().query(sakilaSource, sqlquery)
        // then
        //assertEquals("read-meta-schema done.","")
    }


}