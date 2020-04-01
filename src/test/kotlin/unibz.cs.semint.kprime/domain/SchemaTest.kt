package unibz.cs.semint.kprime.domain

import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.Test
import unibz.cs.semint.kprime.domain.ddl.*

class SchemaTest {


    @Test
    fun test_basic() {
        val time = Column.of("Time")
        val classroom = Column.of("Classroom")
        val course = Column.of("Course")
        val fd = Constraint.of("${time.name},${classroom.name}","${course.name}")

        assertEquals(" Time , Classroom , Course"," $time , $classroom , $course")
        assertEquals(" Time , Classroom --> Course ; "," $fd")
        assertEquals("Time , Classroom --> Course ; ",fd.toString())
    }

    @Test
    fun test_find_superkeys() {
        val exprs = listOf(
                "A, B --> C",
                "C, D --> E",
                "C --> A",
                "C --> D",
                "D-->B"
        )

        val expr = "A, B --> C; C, D --> E; C --> A; C --> D; D-->B"
        val fds = Constraint.set(exprs)
        val attrs = Column.set("A, B, C, D, E")
        val keys = Schema.superkeys(attrs,fds)
        assertEquals(
                "[[A, B], [C], [A, C], [A, D], [B, C], [A, B, C], [A, B, D], [C, D], [A, B, E], [A, C, D], [C, E], [A, C, E], [B, C, D], [A, B, C, D], [A, D, E], [B, C, E], [A, B, C, E], [A, B, D, E], [C, D, E], [A, C, D, E], [B, C, D, E], [A, B, C, D, E]]"
                ,keys.toString()
                )
    }

    @Test
    fun test_find_keys() {
        val constraints = Constraint.set("A, B --> C; C, D --> E; C --> A; C --> D; D --> B")
        val columns = Column.set("A, B, C, D, E")
        val keys = Schema.keys(columns, constraints)
        assertEquals(
                "[[A, B], [C], [A, D]]",
                keys.toString()
        )
    }

    @Test
    fun test_closure() {
        val columns = Column.set("C,S")
        val constraints = Constraint.set("C-->T;H,R-->C;H,T-->R;C,S-->G;H,S-->R")

        val closure = Schema.closure(columns, constraints)
        assertEquals("[C, S, T, G]",closure.toString())
    }

    @Test
    fun test_remove_trivials() {
        val constraints = Constraint.set("A-->B;"
                + "A,B-->B;"
                + "A,B-->A;"
                + "C-->C;"
                + "C,D,E,F-->C,D,F")
        val result= Schema.removeTrivial(constraints)
        assertEquals("[A --> B ; ]",result.toString())
    }

    @Test
    fun test_equivalent() {
        val setA = Constraint.set("A-->C; A,C-->D; E-->A,D; E-->H")
        val setB = Constraint.set("A-->C,D; E-->A,H")
        assertTrue(Schema.equivalent(setA,setB))
    }


    @Test
    fun test_powerset() {
        // given
        val attrs = Column.set("A,B,C")
        val notin = Column.set("D,E")
        val fds = Constraint.set("A-->B,C;C,D-->E;E-->A;B-->D")
        // when
        val powerSet = Schema.powerSet(attrs)
        // then
        val map = HashMap<Set<Column>,Set<Column>>()
        for (sa in powerSet) {
            map.put(sa,Schema.closure(sa,fds))
        }
        var result = ""
        for (k in map.keys) {
            var v = map.get(k)
            if (v!=null) {
                v = v.minus(notin)
                result+="$k = $v "+System.lineSeparator()
            }
        }
        assertEquals("""
             [] = [] 
             [A] = [A, B, C] 
             [B] = [B] 
             [A, B] = [A, B, C] 
             [C] = [C] 
             [A, C] = [A, B, C] 
             [B, C] = [A, B, C] 
             [A, B, C] = [A, B, C]
        """.trimIndent(),result.trim())
    }

    @Test
    fun test_removeUnnecessaryEntireFD() {
        var fds = Constraint.set("A-->B,C;B-->C;A-->B;A,B-->C")
        fds = Schema.splitRight(fds)
        val removed = Schema.removeUnnecessaryEntireFD(fds)
        assertEquals("[B --> C ; , A --> B ; ]",removed.toString())
    }

    @Test
    fun test_projection() {
        // given
        val attrs = Column.set("name, location, favAppl, appl")
        val fds = Constraint.set("name-->location,favAppl; appl-->provider")
        // when
        val result : Set<Constraint> = Schema.projection(attrs,fds)
        // then
        assertEquals(2,result.size)
        assertTrue(result.contains(Constraint.of("name --> favAppl")))
        assertTrue(result.contains(Constraint.of("name --> location")))
    }

    @Test
    fun test_minimalbasis() {
        // given
        val fds = Constraint.set("name --> location;name --> favAppl;appl, name --> favAppl")
        // when
        val basis = Schema.minimalBasis(fds)
        // then
        assertEquals(2,basis.size)
        assertTrue(basis.contains(Constraint.of("name --> favAppl")))
        assertTrue(basis.contains(Constraint.of("name --> location")))
}

    @Test
    fun test_combineRight() {
        // given
        var fds = Constraint.set("A-->B;"
                + "A,B-->B,C;"
                + "A-->C;"
                + "B,C-->D;"
                + "B,C-->C,E")
        // when
        fds = Schema.combineRight(fds)
        fds = Schema.removeTrivial(fds)
        // then
        assertEquals(3,fds.size)
        assertTrue(fds.contains(Constraint.of("A , B --> C")))
        assertTrue(fds.contains(Constraint.of("B , C --> D , E")))
        assertTrue(fds.contains(Constraint.of("A --> B, C")))
    }

    @Test
    fun test_addFunctional() {
        // given
        var schema = Schema()
        assertEquals(0,schema.functionals().size)
        // when
        schema.addFunctional("person:dep_name-->dep_address")
        // then
        assertEquals(1,schema.functionals().size)
    }

    @Test
    fun test_addTable() {
        // given
        var schema = Schema()
        assertEquals(0,schema.tables().size)
        // when
        schema.addTable("person:name,dep_name,dep_address")
        // then
        assertEquals(1,schema.tables().size)
    }

    @Test
    fun test_addKey() {
        // given
        var schema = Schema()
        assertEquals(0,schema.keys().size)
        // when
        schema.addKey("person:name,surname")
        // then
        assertEquals(1,schema.keys().size)
    }

    @Test
    fun test_addForeignKey() {
        // given
        var schema = Schema()
        assertEquals(0,schema.foreignKeys().size)
        // when
        schema.addForeignKey("person:dep_id-->department:dep_id")
        // then
        assertEquals(1,schema.foreignKeys().size)
    }


    @Test
    fun test_addDoubleInc() {
        // given
        var schema = Schema()
        assertEquals(0,schema.doubleIncs().size)
        // when
        schema.addDoubleInc("person:dep_id-->department:dep_id")
        // then
        assertEquals(1,schema.doubleIncs().size)
    }

    @Test
    fun test_dropTable() {
        // given
        var schema = Schema()
        schema.addTable("person:name,surname")
        assertEquals(1,schema.tables().size)
        // when
        schema.dropTable("person")
        // then
        assertEquals(0, schema.tables().size)

    }

    @Test
    fun test_dropConstraint() {
        // given
        var schema = Schema()
        schema.addKey("person:id")
        schema.addForeignKey("person:id-->employee:id")
        assertEquals(2,schema.constraints().size)
        // when
        schema.dropConstraint("person.primaryKey")
        schema.dropConstraint("person_employee.foreignKey")
        // then
        assertEquals(0,schema.constraints().size)
    }
}