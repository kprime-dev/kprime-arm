package unibz.cs.semint.kprime.domain

import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.Test
import unibz.cs.semint.kprime.domain.ddl.Column
import unibz.cs.semint.kprime.domain.ddl.Constraint
import unibz.cs.semint.kprime.domain.ddl.Schema

class SchemaTest {


    @Test
    fun test_basic() {
        val time = Column.of("Time")
        val classroom = Column.of("Classroom")
        val course = Column.of("Course")
        val fd = Constraint.of("${time.name},${classroom.name}","${course.name}")

        println(" $time , $classroom , $course")
        println(" $fd")
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
        println(keys)
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
        println(keys)
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
        println(closure)
    }

    @Test
    fun test_remove_trivials() {
        val constraints = Constraint.set("A-->B;"
                + "A,B-->B;"
                + "A,B-->A;"
                + "C-->C;"
                + "C,D,E,F-->C,D,F")
        println(constraints)
        val result= Schema.removeTrivial(constraints)
        println(result)
    }

    @Test
    fun test_equivalent() {
        val setA = Constraint.set("A-->C; A,C-->D; E-->A,D; E-->H")
        val setB = Constraint.set("A-->C,D; E-->A,H")
        assertTrue(Schema.equivalent(setA,setB))
    }
}