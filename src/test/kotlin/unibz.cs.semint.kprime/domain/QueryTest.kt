package unibz.cs.semint.kprime.domain

import org.junit.Test
import unibz.cs.semint.kprime.adapter.service.XMLSerializerJacksonAdapter
import unibz.cs.semint.kprime.domain.dql.*
import unibz.cs.semint.kprime.usecase.SQLizeUseCase

class QueryTest {

    private fun simpleQueryFixture(tableName:String): Query {
        val query = Query()
        var select = query.select
        var attr = Attribute()
        attr.name = "Name"
        select.attributes.add(attr)
        attr = Attribute()
        attr.name = "Surname"
        select.attributes.add(attr)
        val fromT1 = From()
        fromT1.tableName = tableName
        select.from.add(fromT1)
        select.where.condition = "Name='Gigi'"
        return query
    }

    private fun unionFixtureQuery(): Query {
        var query = Query()
        query.select = simpleQueryFixture("Table1").select
        var simple2 = simpleQueryFixture("Table2")
        val union = Union()
        union.selects.add(simple2.select)
        query.union.add(union)
        return query
    }

    @Test
    fun test_from_simple_query_to_xml() {
        // given
        val query = simpleQueryFixture("Table1")
        // when
        var queryXml = XMLSerializerJacksonAdapter().prettyQuery(query) as String
        // then
        println(queryXml)

    }

    @Test
    fun test_from_simple_query_to_sql() {
        // given
        val query = simpleQueryFixture("Table1")
        // when
        var queryXml = SQLizeUseCase().sqlize(query)
        // then
        println(queryXml)

    }


    @Test
    fun test_from_union_query_to_xml() {
        // given
        val query = unionFixtureQuery()
        // when
        var queryXml = XMLSerializerJacksonAdapter().prettyQuery(query) as String
        // then
        println(queryXml)

    }

    @Test
    fun test_from_union_query_to_sql() {
        // given
        val query = unionFixtureQuery()
        // when
        var queryXml = SQLizeUseCase().sqlize(query)
        // then
        println(queryXml)

    }


    @Test
    fun test_from_xml_to_query() {

    }

    @Test
    fun test_from_sql_to_query() {

    }

}