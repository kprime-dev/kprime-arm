package unibz.cs.semint.kprime.domain

import junit.framework.Assert.assertEquals
import org.junit.Test
import unibz.cs.semint.kprime.domain.ddl.Constraint

class ConstraintTest {

    @Test
    fun test_equals() {
        assertEquals(Constraint.of("A , B --> C"),
                Constraint.of("A , B --> C"))
    }
}