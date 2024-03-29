package unibz.cs.semint.kprime.usecase.serialize

import junit.framework.Assert.assertEquals
import org.junit.Assert
import org.junit.Test
import org.xmlunit.builder.DiffBuilder
import unibz.cs.semint.kprime.adapter.service.XMLSerializerJacksonAdapter
import unibz.cs.semint.kprime.domain.db.Database
import unibz.cs.semint.kprime.domain.db.Schema
import unibz.cs.semint.kprime.domain.db.Table
import unibz.cs.semint.kprime.domain.dql.Attribute
import unibz.cs.semint.kprime.domain.dql.From
import unibz.cs.semint.kprime.domain.dql.Mapping
import unibz.cs.semint.kprime.domain.dql.Query
import unibz.cs.semint.kprime.usecase.common.XMLSerializeUseCase
import java.io.File

class XMLSerializerDatabaseTest {

    @Test
    fun test_database_serialize_with_two_empty_tables() {
        // given
        val serializer = XMLSerializeUseCase(XMLSerializerJacksonAdapter())
        val database = Database()
        database.id="iddb"
        database.name="dbname"
        database.schema= Schema()
        database.schema.id="idschema"
        database.schema.tables().add(Table())
        database.schema.tables().add(Table())
        // when
        val serializedDatabase = serializer.serializeDatabase(database).ok
        // then
        val fileContent = File("target/test-classes/database_with_two_empty_tables.xml")
            .readLines().joinToString(System.lineSeparator())

        val myDiff = DiffBuilder.compare(serializedDatabase)
            .ignoreWhitespace()
            .withTest(fileContent)
            .checkForSimilar().build()
        Assert.assertFalse(myDiff.toString(), myDiff.hasDifferences());
    }


    @Test
    fun test_database_serialize_with_mappings() {
        // given
        val serializer = XMLSerializeUseCase(XMLSerializerJacksonAdapter())
        val database = Database()
        database.id = "iddb"
        database.name = "dbname"
        database.schema = Schema()
        database.schema.id = "idschema"
        database.schema.tables().add(Table())
        val query = Query()
        val attr = Attribute()
        attr.name="name"
        query.select.attributes.add(attr)
        query.select.from = From("people")
        query.name = "query1"
        database.mappings!!.add(
            Mapping.fromQuery(query))
        // when
        val serializedDatabase = serializer.prettyDatabase(database).ok
        // then
        assertEquals("""
            <database name="dbname" id="iddb" source="" vocabulary="">
              <schema name="" id="idschema">
                <tables>
                  <table name="" id="" view="" condition="">
                    <columns/>
                  </table>
                </tables>
                <constraints/>
              </schema>
              <mappings>
                <query id="" name="query1">
                  <select>
                    <distinct>false</distinct>
                    <attributes>
                      <attribute name="name"/>
                    </attributes>
                    <from tableName="people" alias=""/>
                    <where condition=""/>
                    <groupBy/>
                  </select>
                  <union/>
                  <minus/>
                  <options/>
                </query>
              </mappings>
            </database>
        """.trimIndent(),serializedDatabase)
    }

}