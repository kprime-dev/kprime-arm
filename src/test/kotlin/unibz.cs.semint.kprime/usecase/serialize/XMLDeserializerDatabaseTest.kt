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
        assertEquals("univ",deserialized.ok!!.schema.tables[0].name)

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
              <mapping>
                <query1>
                  <select>
                    <attributes>
                      <attributes name="name"/>
                    </attributes>
                    <from>
                      <from tableName="people" alias="" joinOn=""/>
                    </from>
                    <where condition=""/>
                  </select>
                </query1>
              </mapping>
            </database>
        """.trimIndent()
        // when
        val deserialized = serializer.deserializeDatabase(fileContent)
        // then
        assertNotNull(deserialized)
        assertNotNull(deserialized.ok)
        assertNotNull((deserialized.ok as Database).mapping)
        assertNotNull((deserialized.ok as Database).mapping!!.get("query1"))
        assertEquals("people",deserialized!!.ok!!.mapping!!.get("query1")!!.select!!.from[0]?.tableName)

        val db = deserialized.ok!!

        db.mapping!!["query2"]= SQLizeUseCase().fromsql("""
            SELECT *  
            FROM alias 
        """.trimIndent())

        assertNotNull((deserialized.ok as Database).mapping!!.get("query2"))
        assertEquals("*",deserialized!!.ok!!.mapping!!.get("query2")!!.select!!.attributes[0]?.name)
        assertEquals("alias",deserialized!!.ok!!.mapping!!.get("query2")!!.select!!.from[0]?.tableName)

    }

}