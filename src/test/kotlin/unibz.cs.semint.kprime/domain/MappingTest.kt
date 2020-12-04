package unibz.cs.semint.kprime.domain

import junit.framework.Assert
import org.junit.Test
import unibz.cs.semint.kprime.domain.dql.Attribute
import unibz.cs.semint.kprime.domain.dql.From
import unibz.cs.semint.kprime.domain.dql.Join
import unibz.cs.semint.kprime.domain.dql.Mapping
import unibz.cs.semint.kprime.usecase.common.SQLizeCreateUseCase
import unibz.cs.semint.kprime.usecase.common.UnSQLizeSelectUseCase
import kotlin.test.assertEquals

class MappingTest {

    @Test
    /*
        create view poids
            select ssn as poid
            from person_employee
     */
    fun test_from_entity_mapping() {
        // given
        val sqlQuery = """
            SELECT *
            FROM tab1
        """.trimIndent()
        // when
        val query = UnSQLizeSelectUseCase().fromsql("query1", sqlQuery)
        // then
        assertEquals("*", query.select.attributes[0].name)
        assertEquals("tab1", query.select.from.tableName)
    }

    @Test
    /*
        create view poids_phone as
            select ssn as poid, phone
            from person_employee

     */
    fun test_from_relation_mapping() {
        // given
        // then
        // when
    }

    @Test
    /*
        create view poid_ssn as
        select t1.ssn as poid as , t2.ssn as ssn
        from T t1 join T t2 on t1.ssn=t2.ssn2 ;
     */
    fun test_from_reference_mapping() {
        // given
        // then
        // when
    }

    @Test
    fun test_to_reference_mapping() {
        // given
        val mapping = Mapping()
        mapping.name = "poid_ssn"
        mapping.select.attributes.add(Attribute("t1.ssn","poids"))
        mapping.select.attributes.add(Attribute("t2.ssn","ssn"))
        val from = From("T", "t1")
        val join = Join()
        join.joinLeftTableAlias = "t1"
        join.joinOnLeft="t1.ssn"
        join.joinOnRight="t2.ssn2"
        join.joinRightTable = "T"
        join.joinRightTableAlias = "t2"
        from.addJoin(join)
        mapping.select.from = from
        //when
        val actualSQL = SQLizeCreateUseCase().createViewCommand(mapping)
        // then
        assertEquals("""
CREATE OR REPLACE VIEW public.poid_ssn AS
SELECT 't1.ssn','t2.ssn'
FROM   T AS t1
 JOIN T AS t2
ON t1.t1.ssn = t2.t2.ssn2
 LIMIT 10
         """.trimIndent(), actualSQL)
    }
}