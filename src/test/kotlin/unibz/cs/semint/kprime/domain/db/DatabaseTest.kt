package unibz.cs.semint.kprime.domain.db

import org.junit.Test
import unibz.cs.semint.kprime.adapter.service.XMLSerializerJacksonAdapter
import kotlin.test.assertEquals

class DatabaseTest {

    @Test
    fun test_parse_trans1_database() {
        // given
        val dbXml = DatabaseTest::class.java.getResource("/db/trans1/database.xml").readText()
        // when
        val db = XMLSerializerJacksonAdapter().deserializeDatabase(dbXml)
        // then
        assertEquals("",db.id)

    }

    @Test
    fun test_parse_trans2_database() {
        // given
        val dbXml = DatabaseTest::class.java.getResource("/db/trans2/ex2_database.xml").readText()
        // when
        val db = XMLSerializerJacksonAdapter().deserializeDatabase(dbXml)
        // then
        assertEquals("",db.id)

    }

    @Test
    fun test_parse_trans3_database() {
        // given
        val dbXml = DatabaseTest::class.java.getResource("/db/trans3/ex3_database.xml").readText()
        // when
        val db = XMLSerializerJacksonAdapter().deserializeDatabase(dbXml)
        // then
        assertEquals("",db.id)

    }

    @Test
    fun test_table_with_condition() {
        // given
        val table = Table()
        table.name= "pure_person"
        table.view="person"
        table.condition="person.T=null AND person.S=null"
        val colSSN = Column("SSN", "id.SSN", "dbname.SSN")
        colSSN.nullable=false
        table.columns.add(colSSN)
        val db = Database()
        db.schema.tables().add(table)
        // when
        val dbXml = XMLSerializerJacksonAdapter().prettyDatabase(db)
        // then
        assertEquals("""
            <database name="" id="" source="" vocabulary="">
              <schema name="" id="">
                <tables>
                  <table name="pure_person" id="" view="person" condition="person.T=null AND person.S=null">
                    <columns>
                      <column name="SSN" id="id.SSN" dbname="dbname.SSN" nullable="false" dbtype=""/>
                    </columns>
                  </table>
                </tables>
                <constraints/>
              </schema>
              <mappings/>
            </database>
        """.trimIndent(),dbXml)

    }
}