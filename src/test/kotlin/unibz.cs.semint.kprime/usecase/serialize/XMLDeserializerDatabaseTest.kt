package unibz.cs.semint.kprime.usecase.serialize

import org.junit.Test
import unibz.cs.semint.kprime.adapter.service.XMLSerializerJacksonAdapter
import unibz.cs.semint.kprime.domain.ddl.Database
import unibz.cs.semint.kprime.usecase.common.SQLizeUseCase
import unibz.cs.semint.kprime.usecase.common.XMLSerializeUseCase
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class XMLDeserializerDatabaseTest {

    @Test
    fun test_deserialize_depemp_database(){
        //given
        val serializer = XMLSerializeUseCase(XMLSerializerJacksonAdapter())
        val fileContent = File("target/test-classes/depemp.xml")
            .readLines().joinToString(System.lineSeparator())
        // when
        val deserialized = serializer.deserializeDatabase(fileContent)
        // then
        assertNotNull(deserialized)
        assertEquals("univ",deserialized.ok!!.schema.tables()[0].name)

    }

    @Test
    fun test_deserialize_mapping_database(){
        //given
        val serializer = XMLSerializeUseCase(XMLSerializerJacksonAdapter())
        val fileContent = """
            <database name="dbname" id="iddb">
              <schema name="" id="idschema">
                <tables>
                  <tables name="" id="" view="" condition="">
                    <columns>
                        <columns name="SSN" id="" dbname=""/>
                        <columns name="Name" id="" dbname=""/>
                        <columns name="DepName" id="" dbname=""/>
                        <columns name="DepAddress" id="" dbname=""/>
                    </columns>
                </tables>
                </tables>
                <constraints>
                    <constraints name="depname-has-address-depaddress" id="" type="">
                        <source name="" id="" table="">
                            <columns>
                                <columns name="DepName" id="" dbname=""/>
                            </columns>
                        </source>
                        <target name="" id="" table="">
                            <columns>
                                <columns name="DepAddress" id="" dbname=""/>
                            </columns>
                        </target>
                    </constraints>
                </constraints>
              </schema>
              <mappings>
                <query name="query1">
                  <select>
                    <attributes>
                      <attributes name="name"/>
                    </attributes>
                    <from>
                      <from tableName="people" alias="" joinOn=""/>
                    </from>
                    <where condition=""/>
                  </select>
                </query>
                <query name="query3">
                  <select>
                    <attributes>
                      <attributes name="name"/>
                    </attributes>
                    <from>
                      <from tableName="people" alias="" joinOn=""/>
                    </from>
                    <where condition=""/>
                  </select>
                </query>
              </mappings>
            </database>
        """.trimIndent()
        // when
        val deserialized = serializer.deserializeDatabase(fileContent)
        // then
        assertNotNull(deserialized)
        assertNotNull(deserialized.ok)
        val database = deserialized.ok as Database
        assertNotNull(database.mappings)
        assertEquals(database.mappings().size,2)
        assertNotNull(database.mapping("query1"))
        assertEquals("people",deserialized!!.ok!!.mapping("query1")!!.select!!.from[0]?.tableName)

        val db = deserialized.ok!!

        db.mappings().add(SQLizeUseCase().fromsql("query2","""
            SELECT *  
            FROM alias 
        """.trimIndent()))

        assertEquals(3, db.mappings().size)
        assertNotNull(database.mapping("query2"))
        assertEquals("*",deserialized!!.ok!!.mapping("query2")!!.select!!.attributes[0]?.name)
        assertEquals("alias",deserialized!!.ok!!.mapping("query2")!!.select!!.from[0]?.tableName)

    }

}