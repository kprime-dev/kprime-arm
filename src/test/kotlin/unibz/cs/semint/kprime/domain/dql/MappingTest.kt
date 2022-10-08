package unibz.cs.semint.kprime.domain.dql

import org.junit.Test
import unibz.cs.semint.kprime.usecase.common.SQLizeCreateUseCase
import unibz.cs.semint.kprime.usecase.common.SQLizeSelectUseCase
import unibz.cs.semint.kprime.usecase.common.UnSQLizeSelectUseCase
import kotlin.test.assertEquals

class MappingTest {

    @Test
    /* from demo1

        create view poids
            select ssn as poid
            from person_employee
     */
    fun test_from_entity_mapping() {
        // given
        val sqlQuery = """
            SELECT ssn AS poid
            FROM person_employee
        """.trimIndent()
        // when
        val query = UnSQLizeSelectUseCase().fromsql("query1", sqlQuery)
        // then
        assertEquals("ssn", query.select.attributes[0].name)
        assertEquals("poid", query.select.attributes[0].asName)
        assertEquals("person_employee", query.select.from.tableName)
    }

    @Test
    fun test_from_entity_mapping_as_lowercase() {
        // given
        val sqlQuery = """
            select ssn as poid
            from person_employee
        """.trimIndent()
        // when
        val query = UnSQLizeSelectUseCase().fromsql("query1", sqlQuery)
        // then
        assertEquals("ssn", query.select.attributes[0].name)
        assertEquals("poid", query.select.attributes[0].asName)
        assertEquals("person_employee", query.select.from.tableName)
    }

    @Test
    fun test_from_entity_mapping_as_singleline() {
        // given
        val sqlQuery = """
            select ssn as poid from person_employee
        """.trimIndent()
        // when
        val query = UnSQLizeSelectUseCase().fromsql("query1", sqlQuery)
        // then
        assertEquals("ssn", query.select.attributes[0].name)
        assertEquals("poid", query.select.attributes[0].asName)
        assertEquals("person_employee", query.select.from.tableName)
    }

    @Test
    /* from demo1

        create view poids_phone as
            select ssn as poid, phone
            from person_employee

     */
    fun test_from_relation_mapping() {
        // given
        val sqlQuery = """
            select ssn as poid, phone
            from person_employee
        """.trimIndent()
        // when
        val query = UnSQLizeSelectUseCase().fromsql("query1", sqlQuery)
        // then
        assertEquals("ssn", query.select.attributes[0].name)
        assertEquals("poid", query.select.attributes[0].asName)
        assertEquals("phone", query.select.attributes[1].name)
        assertEquals("", query.select.attributes[1].asName)
        assertEquals("person_employee", query.select.from.tableName)
    }

    @Test
    /* from demo1

        create view poid_ssn as
        select t1.ssn as poid as ,t2.ssn as ssn
        from T t1 join T t2 on t1.ssn=t2.ssn2 ;
     */
    fun test_from_reference_mapping() {
        // given
        val sqlQuery = """
            select t1.ssn as poid , t2.ssn as ssn
            from T as t1 join T as t2 on t1.ssn=t2.ssn2 
        """.trimIndent()
        // when
        val query = UnSQLizeSelectUseCase().fromsql("query1", sqlQuery)
        val actualSql = SQLizeSelectUseCase().sqlize(query)
        // then
        assertEquals("t1.ssn", query.select.attributes[0].name)
        assertEquals("poid", query.select.attributes[0].asName)
        assertEquals("t2.ssn", query.select.attributes[1].name)
        assertEquals("ssn", query.select.attributes[1].asName)
        assertEquals("T", query.select.from.tableName)
        assertEquals("t1", query.select.from.alias)
        assertEquals(1, query.select.from.joins?.size)
        assertEquals("T", query.select.from.joins?.get(0)?.joinRightTable)
        assertEquals("t2", query.select.from.joins?.get(0)?.joinRightTableAlias)
        assertEquals("t1", query.select.from.joins?.get(0)?.joinLeftTableAlias)
        assertEquals("t1.ssn", query.select.from.joins?.get(0)?.joinOnLeft)
        assertEquals("t2.ssn2", query.select.from.joins?.get(0)?.joinOnRight)
        assertEquals("""
SELECT t1.ssn AS poid,t2.ssn AS ssn
FROM   T AS t1
 JOIN T AS t2
ON t1.ssn = t2.ssn2
 LIMIT 10
          """.trimIndent(),actualSql)
    }

    @Test
    fun test_mapping_with_where() {
        // given
        val sqlQuery = """
                select SSN,DepName,DepAddress
                from table0
                where DepName is not null and DepAddress is not null            
        """.trimIndent()
        // when
        val mapping = UnSQLizeSelectUseCase().fromsql("query1", sqlQuery)
        val actualSQL = SQLizeCreateUseCase().createViewCommand(mapping)
        // then
        assertEquals("DepName is not null and DepAddress is not null", mapping.select.where.condition)
        assertEquals("""
CREATE OR REPLACE VIEW public.query1 AS
SELECT SSN,DepName,DepAddress
FROM   table0
WHERE DepName is not null and DepAddress is not null LIMIT 10
        """.trimIndent(),actualSQL)
    }

    @Test
    fun test_mapping_with_where_oneline() {
        // given
        val sqlQuery = "select SSN,DepName,DepAddress from table0 where DepName is not null and DepAddress is not null"
        // when
        val mapping = UnSQLizeSelectUseCase().fromsql("query1", sqlQuery)
        val actualSQL = SQLizeCreateUseCase().createViewCommand(mapping)
        // then
        assertEquals("DepName is not null and DepAddress is not null", mapping.select.where.condition)
        assertEquals("""
CREATE OR REPLACE VIEW public.query1 AS
SELECT SSN,DepName,DepAddress
FROM   table0
WHERE DepName is not null and DepAddress is not null LIMIT 10
        """.trimIndent(),actualSQL)
    }

    @Test
    fun test_multi_join() {
        // given
        val sqlQuery = """
            SELECT student.first_name, student.last_name, course.name
            FROM student
            JOIN student_course
              ON student.id = student_course.student_id
            JOIN course
              ON course.id = student_course.course_id
          """.trimIndent()
        // when
        val mapping = UnSQLizeSelectUseCase().fromsql("query1", sqlQuery)
        // then
        assertEquals("student.first_name", mapping.select.attributes[0].name)
        assertEquals("student.last_name", mapping.select.attributes[1].name)
        assertEquals("course.name", mapping.select.attributes[2].name)
        assertEquals("student", mapping.select.from.tableName)
        assertEquals(2, mapping.select.from.joins?.size)

        val join0 = mapping.select.from.joins?.get(0)
        assertEquals("student_course", join0?.joinRightTable)
        assertEquals("student", join0?.joinLeftTableAlias)
        assertEquals("student.id", join0?.joinOnLeft)
        assertEquals("student_course.student_id", join0?.joinOnRight)

        val join1 = mapping.select.from.joins?.get(1)
        assertEquals("course", join1?.joinRightTable)
        assertEquals("student", join1?.joinLeftTableAlias)
        assertEquals("course.id", join1?.joinOnLeft)
        assertEquals("student_course.course_id", join1?.joinOnRight)

        val actualSQL = SQLizeCreateUseCase().createViewCommand(mapping)
        assertEquals("""
CREATE OR REPLACE VIEW public.query1 AS
SELECT student.first_name,student.last_name,course.name
FROM   student
 JOIN student_course
ON student.id = student_course.student_id
 JOIN course
ON course.id = student_course.course_id
 LIMIT 10
          """.trimIndent(), actualSQL)

    }

    @Test
    fun test_to_create_reference_mapping() {
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
SELECT t1.ssn AS poids,t2.ssn AS ssn
FROM   T AS t1
 JOIN T AS t2
ON t1.ssn = t2.ssn2
 LIMIT 10
          """.trimIndent(), actualSQL)
    }
}