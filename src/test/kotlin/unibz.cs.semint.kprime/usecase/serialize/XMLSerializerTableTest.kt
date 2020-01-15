package unibz.cs.semint.kprime.usecase.serialize

import org.junit.Assert.assertFalse
import org.junit.Test
import org.xmlunit.builder.DiffBuilder
import unibz.cs.semint.kprime.adapter.service.XMLSerializerJacksonAdapter
import unibz.cs.semint.kprime.domain.Column
import unibz.cs.semint.kprime.domain.Table
import unibz.cs.semint.kprime.usecase.XMLSerializeUseCase
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertNotNull


class XMLSerializerTableTest {

    @Test
    fun test_empty_table_serialize() {
        // given
        val serializer = XMLSerializeUseCase(XMLSerializerJacksonAdapter())
        // when
        var table = Table()
        table.name="Gigi"
        table.id="22"
        val result = serializer.serializeTable(table).ok
        // then
        assertEquals("<table name=\"Gigi\" id=\"22\"><columns/></table>",result)
    }

    @Test
    fun test_table_with_two_columns_serialize() {
        // given
        val serializer = XMLSerializeUseCase(XMLSerializerJacksonAdapter())
        // when
        var table = Table()
        table.name="Gigi"
        table.id="22"
        table.columns.add(Column(name="col1",id = "id1", dbname = "dbname1"))
        table.columns.add(Column(name="col2",id = "id2", dbname = "dbname2"))

        val result = serializer.serializeTable(table).ok
        // then
        assertEquals("<table name=\"Gigi\" id=\"22\"><columns><columns name=\"col1\" id=\"id1\" dbname=\"dbname1\" nullable=\"false\" dbtype=\"\"/><columns name=\"col2\" id=\"id2\" dbname=\"dbname2\" nullable=\"false\" dbtype=\"\"/></columns></table>",result)
    }

    @Test
    fun test_table_with_two_columns_serialize_from_file() {
        // given
        val serializer = XMLSerializeUseCase(XMLSerializerJacksonAdapter())
        var table = Table()
        table.name="Gigi"
        table.id="23"
        table.columns.add(Column(name="col1",id = "id1", dbname = "dbname1"))
        table.columns.add(Column(name="col2",id = "id2", dbname = "dbname2"))
        // when
        val result = serializer.prettyTable(table).ok
        print(result)
        // then
        val file1 = File("target/test-classes/table_two_columns.xml")
        println(file1.absoluteFile)
        val fileContent = file1.readLines().joinToString(System.lineSeparator())

        val myDiff = DiffBuilder.compare(fileContent)
            .ignoreWhitespace()
            .withTest(result)
            .checkForSimilar().build()
        assertFalse(myDiff.toString(), myDiff.hasDifferences());
    }

    @Test
    fun test_table_deserialize() {
        // given
        val serializer = XMLSerializeUseCase(XMLSerializerJacksonAdapter())
        // when
        val result = serializer.deserializeTable("<Table><name>Gigi</name></Table>").ok
        // then
        assertNotNull(result)
        assertEquals("Gigi",result.name)
        assertEquals("",result.id)
    }

    @Test
    fun test_table_deserialize_attributes() {
        // given
        val serializer = XMLSerializeUseCase(XMLSerializerJacksonAdapter())
        // when
        val result = serializer.deserializeTable("<table name=\"Gigi\" id=\"22\"/>").ok
        // then
        assertNotNull(result)
        assertEquals("Gigi",result.name)
        assertEquals("22",result.id)
    }

    @Test
    fun test_table_with_columns_from_file() {
        // given
        val serializer = XMLSerializeUseCase(XMLSerializerJacksonAdapter())
        val file1 = File("target/test-classes/table_minimal.xml")
        println(file1.absoluteFile)
        val fileContent = file1.readLines().joinToString(System.lineSeparator())
        // when
        val result = serializer.deserializeTable(fileContent).ok
        // then
        assertNotNull(result)
        assertEquals("Gigi",result.name)
        assertEquals("22",result.id)

    }
}