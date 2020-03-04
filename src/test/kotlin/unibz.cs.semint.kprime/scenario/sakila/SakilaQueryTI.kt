package unibz.cs.semint.kprime.scenario.sakila

import junit.framework.Assert.assertEquals
import org.junit.Test
import unibz.cs.semint.kprime.adapter.repository.QueryJdbcAdapter
import unibz.cs.semint.kprime.adapter.service.XMLSerializerJacksonAdapter
import unibz.cs.semint.kprime.domain.DataSource
import unibz.cs.semint.kprime.domain.dql.Query
import unibz.cs.semint.kprime.domain.dql.Select
import unibz.cs.semint.kprime.usecase.common.SQLizeUseCase

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


    @Test
    fun test_sakila_film1_query_to_xml() {
        // given
        val query = SQLizeUseCase().fromsql("q1","""
            SELECT film_id,title,description,release_year,release_year,original_language_id,length,rating
            FROM film
        """.trimIndent())
        assertEquals(8, query.select.attributes.size)
        // when
        var queryXml = XMLSerializerJacksonAdapter().prettyQuery(query) as String
        // then
        assertEquals("""
            <query name="q1">
              <select>
                <attributes>
                  <attributes name="film_id"/>
                  <attributes name="title"/>
                  <attributes name="description"/>
                  <attributes name="release_year"/>
                  <attributes name="release_year"/>
                  <attributes name="original_language_id"/>
                  <attributes name="length"/>
                  <attributes name="rating"/>
                </attributes>
                <from>
                  <from tableName="film" alias="" joinOn=""/>
                </from>
                <where condition=""/>
              </select>
              <union>
                <selects/>
              </union>
              <minus>
                <selects/>
              </minus>
            </query>
        """.trimIndent(),queryXml)
    }


}