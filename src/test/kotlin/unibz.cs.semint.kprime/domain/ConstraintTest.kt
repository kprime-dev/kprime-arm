package unibz.cs.semint.kprime.domain

import junit.framework.TestCase.assertEquals
import org.junit.Test
import unibz.cs.semint.kprime.domain.ddl.Column
import unibz.cs.semint.kprime.domain.ddl.Constraint
import unibz.cs.semint.kprime.domain.ddl.Schema

class ConstraintTest {

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
}