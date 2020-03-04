package unibz.cs.semint.kprime.domain

import junit.framework.Assert.assertEquals
import org.junit.Test
import unibz.cs.semint.kprime.adapter.service.XMLSerializerJacksonAdapter
import unibz.cs.semint.kprime.domain.dql.*
import unibz.cs.semint.kprime.usecase.common.SQLizeUseCase
import kotlin.test.assertEquals as assertEquals1

class QueryTest {

    fun simpleQueryFixture(tableName:String): Query {
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
        query.union=union
        return query
    }

    @Test
    fun test_from_simple_query_to_xml() {
        // given
        val query = simpleQueryFixture("Table1")
        // when
        var queryXml = XMLSerializerJacksonAdapter().prettyQuery(query) as String
        // then
        assertEquals("""
            <query name="">
              <select>
                <attributes>
                  <attributes name="Name"/>
                  <attributes name="Surname"/>
                </attributes>
                <from>
                  <from tableName="Table1" alias="" joinOn=""/>
                </from>
                <where condition="Name='Gigi'"/>
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
    fun test_from_simple_query_to_sql() {
        // given
        val query = simpleQueryFixture("Table1")
        // when
        var querySql = SQLizeUseCase().sqlize(query)
        // then
        assertEquals1("""
            SELECT Name,Surname
            FROM   Table1
            WHERE Name='Gigi'
            """.trimIndent(),querySql)

    }

    @Test
    fun test_from_simple_select_to_sql(){
        // given
        val select = Select()
        val att = Attribute()
            att.name="ww"
        select.attributes.add(att)
        val from = From()
        from.tableName="tab"
        select.from.add(from)
        select.where.condition="a = b"
        // when
        val selectSql = SQLizeUseCase().sqlize(select)
        // then
        assertEquals1("""
            SELECT ww
            FROM   tab
            WHERE a = b
        """.trimIndent(),selectSql)
    }

    @Test
    fun test_from_union_query_to_xml() {
        // given
        val query = unionFixtureQuery()
        // when
        var queryXml = XMLSerializerJacksonAdapter().prettyQuery(query) as String
        // then
        assertEquals("""
            <query name="">
              <select>
                <attributes>
                  <attributes name="Name"/>
                  <attributes name="Surname"/>
                </attributes>
                <from>
                  <from tableName="Table1" alias="" joinOn=""/>
                </from>
                <where condition="Name='Gigi'"/>
              </select>
              <union>
                <selects>
                  <selects>
                    <attributes>
                      <attributes name="Name"/>
                      <attributes name="Surname"/>
                    </attributes>
                    <from>
                      <from tableName="Table2" alias="" joinOn=""/>
                    </from>
                    <where condition="Name='Gigi'"/>
                  </selects>
                </selects>
              </union>
              <minus>
                <selects/>
              </minus>
            </query>
        """.trimIndent(),queryXml)

    }

    @Test
    fun test_from_union_query_to_sql() {
        // given
        val query = unionFixtureQuery()
        // when
        var querySql = SQLizeUseCase().sqlize(query)
        // then
        assertEquals1("""
            SELECT Name,Surname
            FROM   Table1
            WHERE Name='Gigi'
            UNION
            SELECT Name,Surname
            FROM   Table2
            WHERE Name='Gigi'
            """.trimIndent(),querySql)

    }


    @Test
    fun test_from_xml_to_query() {

    }

    @Test
    fun test_from_minimal_sql_to_query() {
        // given
        val sqlQuery="""
            SELECT *
            FROM tab1
        """.trimIndent()
        val query = SQLizeUseCase().fromsql("query1",sqlQuery)
        assertEquals("*",query.select.attributes[0].name)
        assertEquals("tab1",query.select.from[0].tableName)
    }

    @Test
    fun test_from_conditional_sql_to_query() {
        // given
        val sqlQuery="""
            SELECT alfa,beta
            FROM tab1
            WHERE a = b
        """.trimIndent()
        val query = SQLizeUseCase().fromsql("query1",sqlQuery)
        assertEquals("alfa",query.select.attributes[0].name)
        assertEquals("beta",query.select.attributes[1].name)
        assertEquals("tab1",query.select.from[0].tableName)
        assertEquals("a = b",query.select.where.condition)
    }

    @Test
    fun test_from_3_union_sql_to_query() {
        // given
        val sqlQuery="""
            SELECT alfa,beta
            FROM tab1
            WHERE a = b
            UNION
            SELECT gamma,theta
            FROM tab2
            WHERE c = d
        """.trimIndent()
        val query = SQLizeUseCase().fromsql("query1",sqlQuery)
        assertEquals(1,query.union.selects.size)
        assertEquals("alfa",   query.select.attributes[0].name)
        assertEquals("beta",   query.select.attributes[1].name)
        assertEquals("tab1",    query.select.from[0].tableName)
        assertEquals("a = b",   query.select.where.condition)

        assertEquals("gamma",    query.union.selects[0].attributes[0].name)
        assertEquals("theta",    query.union.selects[0].attributes[1].name)
        assertEquals("tab2",    query.union.selects[0].from[0].tableName)
        assertEquals("c = d",    query.union.selects[0].where.condition)
    }

    @Test
    fun test_from_union_sql_to_query() {
        // given
        val sqlQuery="""
            SELECT alfa,beta
            FROM tab1
            WHERE a = b
            UNION
            SELECT gamma,theta
            FROM tab2
            WHERE c = d
            UNION
            SELECT delta,zeta
            FROM tab3
            WHERE e = f
        """.trimIndent()
        val query = SQLizeUseCase().fromsql("query1",sqlQuery)
        assertEquals(2,query.union.selects.size)
        assertEquals("alfa",   query.select.attributes[0].name)
        assertEquals("beta",   query.select.attributes[1].name)
        assertEquals("tab1",    query.select.from[0].tableName)
        assertEquals("a = b",   query.select.where.condition)

        assertEquals("gamma",    query.union.selects[0].attributes[0].name)
        assertEquals("theta",    query.union.selects[0].attributes[1].name)
        assertEquals("tab2",    query.union.selects[0].from[0].tableName)
        assertEquals("c = d",    query.union.selects[0].where.condition)

        assertEquals("delta",    query.union.selects[1].attributes[0].name)
        assertEquals("zeta",    query.union.selects[1].attributes[1].name)
        assertEquals("tab3",    query.union.selects[1].from[0].tableName)
        assertEquals("e = f",    query.union.selects[1].where.condition)

        val sqlize = SQLizeUseCase().sqlize(query)
        assertEquals("""
            SELECT alfa,beta
            FROM   tab1
            WHERE a = b
            UNION
            SELECT gamma,theta
            FROM   tab2
            WHERE c = d
            UNION
            SELECT delta,zeta
            FROM   tab3
            WHERE e = f
        """.trimIndent(),sqlize)
    }


}