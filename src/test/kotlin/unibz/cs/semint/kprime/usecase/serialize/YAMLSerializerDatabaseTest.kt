package unibz.cs.semint.kprime.usecase.serialize

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import org.junit.Test
import unibz.cs.semint.kprime.domain.db.Database
import unibz.cs.semint.kprime.domain.db.Schema
import unibz.cs.semint.kprime.domain.db.Table
import java.io.StringWriter
import kotlin.test.assertEquals

class YAMLSerializerDatabaseTest {

    @Test
    fun test_database_serialize_with_two_empty_tables() {
        // given
        val database = Database()
        database.id = "iddb"
        database.name = "dbname"
        database.schema = Schema()
        database.schema.id = "idschema"
        database.schema.tables().add(Table())
        database.schema.tables().add(Table())

        val objectMapper = ObjectMapper(YAMLFactory())
        val outWriter = StringWriter()
        objectMapper.writeValue(outWriter,database)
        assertEquals("""
            ---
            name: "dbname"
            id: "iddb"
            schema:
              name: ""
              id: "idschema"
              tables:
              - name: ""
                id: ""
                view: ""
                condition: ""
                parent: null
                columns: []
                catalog: null
                schema: null
                source: null
                labels: null
                var: null
              - name: ""
                id: ""
                view: ""
                condition: ""
                parent: null
                columns: []
                catalog: null
                schema: null
                source: null
                labels: null
                var: null
              constraints: []
            mappings: []
            source: ""
            vocabulary: ""

        """.trimIndent(),outWriter.toString())
    }
}