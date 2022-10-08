package unibz.cs.semint.kprime.scenario.sakila

import junit.framework.Assert.assertEquals
import org.junit.Test
import unibz.cs.semint.kprime.adapter.repository.JdbcAdapter
import unibz.cs.semint.kprime.adapter.repository.JdbcPrinter
import unibz.cs.semint.kprime.adapter.service.XMLSerializerJacksonAdapter
import unibz.cs.semint.kprime.domain.datasource.DataSource
import unibz.cs.semint.kprime.domain.dql.Query
import unibz.cs.semint.kprime.usecase.common.UnSQLizeSelectUseCase

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
        val adapter = JdbcAdapter()
        // when
        val result = adapter.query(sakilaSource,"select * from film where film_id=1",JdbcPrinter::printResultSet)
        // then
        assertEquals("""
             film_id: 1
             title: ACADEMY DINOSAUR
             description: A Epic Drama of a Feminist And a Mad Scientist who must Battle a Teacher in The Canadian Rockies
             release_year: 2006
             language_id: 1
             original_language_id: null
             rental_duration: 6
             rental_rate: 0.99
             length: 86
             replacement_cost: 20.99
             rating: PG
             last_update: 2006-02-15 05:03:42
             special_features: {"Deleted Scenes","Behind the Scenes"}
             fulltext: 'academi':1 'battl':15 'canadian':20 'dinosaur':2 'drama':5 'epic':4 'feminist':8 'mad':11 'must':14 'rocki':21 'scientist':12 'teacher':17
            
            """.trimIndent()
            ,result)

    }


    @Test
    fun test_read_sakila_query_films() {
        //given
        val sakilaSource = DataSource(type,name,driver,path,user,pass)
        // when
        val sqlquery = Query.build("film")
        val adapter = JdbcAdapter()
        val result = adapter.query(sakilaSource, sqlquery,JdbcPrinter::printResultSet)
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
        val adapter = JdbcAdapter()
        val result = adapter.query(sakilaSource, sqlquery,JdbcPrinter::printResultSet)
        // then
        //assertEquals("read-meta-schema done.","")
    }


    @Test
    fun test_sakila_film1_query_to_xml() {
        // given
        val query = UnSQLizeSelectUseCase().fromsql("q1","""
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

    @Test
    fun test_read_sakila_query_appici() {
        //given
        val sakilaSource = DataSource(type,name,driver,path,user,pass)
        // when
        val adapter = JdbcAdapter()
        val result = adapter.query(sakilaSource, "select id,name,address,'zip code' FROM staff_list  LIMIT 10",JdbcPrinter::printResultSet)
        // then
        //assertEquals("read-meta-schema done.","")
    }

}