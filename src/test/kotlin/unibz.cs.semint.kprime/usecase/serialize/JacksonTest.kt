import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.junit.Test
import kotlin.test.assertEquals


import unibz.cs.semint.kprime.domain.ddl.Database

class JacksonTest {

    @Test
    fun test_map_xml_serialize() {
        val map = mapOf(
                "key1" to "val1",
                "key2" to "val2")
        // when
        val mapper = XmlMapper().registerModule(KotlinModule())
        val writer = mapper.writerWithDefaultPrettyPrinter()
        val result = writer.writeValueAsString(map)
        // then
        assertEquals("""
            <LinkedHashMap>
              <key1>val1</key1>
              <key2>val2</key2>
            </LinkedHashMap>
        """.trimIndent(),result)

    }

    @Test
    fun test_db_serialize() {
        val db = Database()
        // when
        val mapper = XmlMapper().registerModule(KotlinModule())
        val writer = mapper.writerWithDefaultPrettyPrinter()
        val result = writer.writeValueAsString(db)
        // then
        assertEquals("""
            <database name="" id="" source="">
              <schema name="" id="">
                <tables/>
                <constraints/>
              </schema>
              <mappings/>
            </database>
        """.trimIndent(),result)
    }


    @Test
    fun test_deserialize_db() {
        // given
        val dbxml = """
            <database name="" id="" source="">
              <schema name="" id="">
                <tables/>
                <constraints/>
              </schema>
              <mappings/>
            </database> 
        """.trimIndent()
        // when
        val mapper = XmlMapper()
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
        val newdb = mapper.readValue(dbxml, Database::class.java)

        val mapper2 = XmlMapper().registerModule(KotlinModule())
        mapper2.setSerializationInclusion(JsonInclude.Include.NON_NULL)
        val writer = mapper2.writerWithDefaultPrettyPrinter()
        val result = writer.writeValueAsString(newdb)

        // then
        assertEquals(0,newdb.schema.tables().size)
        assertEquals("""
            <database name="" id="" source="">
              <schema name="" id=""/>
            </database>
        """.trimIndent(),result)

    }
}