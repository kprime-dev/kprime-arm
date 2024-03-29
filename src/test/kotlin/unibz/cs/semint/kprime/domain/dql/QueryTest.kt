package unibz.cs.semint.kprime.domain.dql

import junit.framework.Assert.assertEquals
import org.junit.Test
import unibz.cs.semint.kprime.adapter.service.XMLSerializerJacksonAdapter
import unibz.cs.semint.kprime.domain.db.Database
import unibz.cs.semint.kprime.domain.db.SchemaCmdParser
import unibz.cs.semint.kprime.usecase.common.SQLizeSelectUseCase
import unibz.cs.semint.kprime.usecase.common.UnSQLizeSelectUseCase
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
        select.from = fromT1
        select.where.condition = "Name='Gigi'"
        return query
    }

    private fun unionFixtureQuery(): Query {
        var query = Query()
        query.select = simpleQueryFixture("Table1").select
        var simple2 = simpleQueryFixture("Table2")
        val union = Union()
        union.selects().add(simple2.select)
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
            <query id="" name="">
              <select>
                <distinct>false</distinct>
                <attributes>
                  <attribute name="Name"/>
                  <attribute name="Surname"/>
                </attributes>
                <from tableName="Table1" alias=""/>
                <where condition="Name='Gigi'"/>
                <groupBy/>
              </select>
              <union/>
              <minus/>
              <options/>
            </query>
        """.trimIndent(),queryXml)

    }

    @Test
    fun test_from_simple_query_to_sql() {
        // given
        val query = simpleQueryFixture("Table1")
        // when
        var querySql = SQLizeSelectUseCase().sqlize(query)
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
        select.from = from
        select.where.condition="a = b"
        // when
        val selectSql = SQLizeSelectUseCase().sqlizeSelect(select)
        // then
        assertEquals1("""
            SELECT ww
            FROM   tab
            WHERE a = b
        """.trimIndent(),selectSql)
    }

    @Test
    fun test_simple_join_to_sql(){
        // given
        val select = Select()
        val att = Attribute()
        att.name="ww"
        select.attributes.add(att)
        val from = From()
        from.tableName="Orders"
        val join = Join()
        join.joinLeftTableAlias = "Orders"
        join.joinOnLeft = "Orders.customerId"
        join.joinRightTable = "Customer"
        join.joinOnRight = "Customer.customerId"
        join.joinType = "INNER"
        from.addJoin(join)
        select.from = from
        select.where.condition="a = b"
        // when
        val selectSql = SQLizeSelectUseCase().sqlizeSelect(select)
        // then
        assertEquals1("""
            SELECT ww
            FROM   Orders
            INNER JOIN Customer
            ON Orders.customerId = Customer.customerId
            WHERE a = b
        """.trimIndent(),selectSql)
    }

    @Test
    fun test_multiple_join_to_sql(){
        // given
        val select = Select()
        val att = Attribute()
        att.name="ww"
        select.attributes.add(att)
        val from = From()
        from.tableName="Orders"
        val join = Join()
        join.joinLeftTableAlias = "Orders"
        join.joinOnLeft = "Orders.customerId"
        join.joinRightTable = "Customer"
        join.joinOnRight = "Customer.customerId"
        join.joinType = "INNER"
        from.addJoin(join)
        val join2 = Join()
        join2.joinLeftTableAlias = "Customer"
        join2.joinOnLeft = "Customer.orderId"
        join2.joinRightTable = "Sales"
        join2.joinOnRight = "Sales.orderId"
        join2.joinType = "LEFT"
        from.addJoin(join2)
        select.from= from
        select.where.condition="a = b"
        // when
        val selectSql = SQLizeSelectUseCase().sqlizeSelect(select)
        // then
        assertEquals1("""
            SELECT ww
            FROM   Orders
            INNER JOIN Customer
            ON Orders.customerId = Customer.customerId
            LEFT JOIN Sales
            ON Customer.orderId = Sales.orderId
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
            <query id="" name="">
              <select>
                <distinct>false</distinct>
                <attributes>
                  <attribute name="Name"/>
                  <attribute name="Surname"/>
                </attributes>
                <from tableName="Table1" alias=""/>
                <where condition="Name='Gigi'"/>
                <groupBy/>
              </select>
              <union>
                <selects>
                  <selects>
                    <distinct>false</distinct>
                    <attributes>
                      <attribute name="Name"/>
                      <attribute name="Surname"/>
                    </attributes>
                    <from tableName="Table2" alias=""/>
                    <where condition="Name='Gigi'"/>
                    <groupBy/>
                  </selects>
                </selects>
              </union>
              <minus/>
              <options/>
            </query>
        """.trimIndent(),queryXml)

    }

    @Test
    fun test_from_union_query_to_sql() {
        // given
        val query = unionFixtureQuery()
        // when
        var querySql = SQLizeSelectUseCase().sqlize(query)
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
        val query = UnSQLizeSelectUseCase().fromsql("query1",sqlQuery)
        assertEquals("*",query.select.attributes[0].name)
        assertEquals("tab1",query.select.from.tableName)
    }

    @Test
    fun test_from_conditional_sql_to_query() {
        // given
        val sqlQuery="""
            SELECT alfa,beta
            FROM tab1
            WHERE a = b
        """.trimIndent()
        val query = UnSQLizeSelectUseCase().fromsql("query1",sqlQuery)
        assertEquals("alfa",query.select.attributes[0].name)
        assertEquals("beta",query.select.attributes[1].name)
        assertEquals("tab1",query.select.from.tableName)
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
        val query = UnSQLizeSelectUseCase().fromsql("query1",sqlQuery)
        assertEquals(1,query.union?.selects()?.size)
        assertEquals("alfa",   query.select.attributes[0].name)
        assertEquals("beta",   query.select.attributes[1].name)
        assertEquals("tab1",    query.select.from.tableName)
        assertEquals("a = b",   query.select.where.condition)

        val select = query.union!!.selects()[0]
        assertEquals("gamma",    select.attributes[0].name)
        assertEquals("theta",    select.attributes[1].name)
        assertEquals("tab2",    select.from.tableName)
        assertEquals("c = d",    select.where.condition)
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
        val query = UnSQLizeSelectUseCase().fromsql("query1",sqlQuery)
        assertEquals(2,query.union!!.selects().size)
        assertEquals("alfa",   query.select.attributes[0].name)
        assertEquals("beta",   query.select.attributes[1].name)
        assertEquals("tab1",    query.select.from.tableName)
        assertEquals("a = b",   query.select.where.condition)

        val select = query.union!!.selects()[0]
        assertEquals("gamma",    select.attributes[0].name)
        assertEquals("theta",    select.attributes[1].name)
        assertEquals("tab2",    select.from.tableName)
        assertEquals("c = d",    select.where.condition)

        val select1 = query.union!!.selects()[1]
        assertEquals("delta",    select1.attributes[0].name)
        assertEquals("zeta",    select1.attributes[1].name)
        assertEquals("tab3",    select1.from.tableName)
        assertEquals("e = f",    select1.where.condition)

        val sqlize = SQLizeSelectUseCase().sqlize(query)
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

    @Test
    fun test_query_from_table() {
        // given
        val table = SchemaCmdParser.parseTable("person:name,surname")
        // when
        val query = Query.buildFromTable(table)
        // then
        val sql = SQLizeSelectUseCase().sqlize(query)
        assertEquals("""
            SELECT name,surname
            FROM   person
        """.trimIndent(),sql)
    }

}