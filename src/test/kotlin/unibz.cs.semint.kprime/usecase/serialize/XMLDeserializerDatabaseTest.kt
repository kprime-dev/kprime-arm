package unibz.cs.semint.kprime.usecase.serialize

import org.junit.Test
import unibz.cs.semint.kprime.adapter.service.XMLSerializerJacksonAdapter
import unibz.cs.semint.kprime.usecase.XMLSerializeUseCase
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
}