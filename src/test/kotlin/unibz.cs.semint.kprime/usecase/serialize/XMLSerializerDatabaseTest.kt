package unibz.cs.semint.kprime.usecase.serialize

import org.junit.Assert
import org.junit.Test
import org.xmlunit.builder.DiffBuilder
import unibz.cs.semint.kprime.adapter.XMLSerializerJacksonAdapter
import unibz.cs.semint.kprime.domain.Database
import unibz.cs.semint.kprime.domain.Schema
import unibz.cs.semint.kprime.domain.Table
import unibz.cs.semint.kprime.usecase.XMLSerializeUseCase
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
        database.schema.tables.add(Table())
        database.schema.tables.add(Table())
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


}