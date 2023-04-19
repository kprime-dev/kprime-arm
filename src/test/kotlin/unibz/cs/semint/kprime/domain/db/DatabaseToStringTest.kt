package unibz.cs.semint.kprime.domain.db

import org.junit.Ignore
import org.junit.Test
import unibz.cs.semint.kprime.domain.dql.Mapping
import unibz.cs.semint.kprime.usecase.common.UnSQLizeSelectUseCase
import kotlin.test.assertEquals

class DatabaseToStringTest {

    @Test
    fun test_print_db_one_table() {
        // given
        val db = Database()
        db.schema.addTable("person:name,surname")
        // when
        val result = db.toString()
        //
        assertEquals("""
            Table(name='person', id='t1', view='', condition='', parent=null, columns=[name, surname], catalog=null, schema=null, source=null)
            
        """.trimIndent(),result)
    }

    @Test
    fun test_print_db_one_table_with_one_functional_dependenncy() {
        // given
        val db = Database()
        db.schema.addTable("person:name,surname")
        db.schema.addFunctional("person:name-->surname")
        // when
        val result = db.toString()
        //R
        assertEquals("""
            Table(name='person', id='t1', view='', condition='', parent=null, columns=[name, surname], catalog=null, schema=null, source=null)
            FUNCTIONAL person:name --> person:surname ; 
        """.trimIndent(),result)
    }

    @Test
    @Ignore
    fun test_print_db_one_table_one_mapping() {
        // given
        val db = Database()
        db.schema.addTable("person:name,surname")
        db.mappings = mutableListOf()
        db.mappings.apply {  this?.add( Mapping.fromQuery(
            UnSQLizeSelectUseCase().fromsql("query2","""
            SELECT *  
            FROM alias 
        """.trimIndent()))
        )
        }
        // when
        val result = db.toString()
        // then
        assertEquals("""
            
        """.trimIndent(),result)
    }
}