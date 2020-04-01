package unibz.cs.semint.kprime.domain

import junit.framework.Assert.assertEquals
import org.junit.Test
import unibz.cs.semint.kprime.domain.ddl.Column
import unibz.cs.semint.kprime.domain.ddl.Constraint

class ConstraintTest {

    @Test
    fun test_equals() {
        assertEquals(Constraint.of("A , B --> C"),
                Constraint.of("A , B --> C"))
    }

    @Test
    fun test_clone() {
        // given
        val constr = Constraint()
        constr.type = Constraint.TYPE.DOUBLE_INCLUSION.name
        constr.source.columns.add(Column())
        constr.target.columns.add(Column())
        constr.target.columns.add(Column())
        // when
        val constr2 = constr.clone()
        // then
        assertEquals(Constraint.TYPE.DOUBLE_INCLUSION.name,constr2.type)
        assertEquals(1,constr2.left().size)
        assertEquals(2,constr2.right().size)
    }
}