package unibz.cs.semint.kprime.usecase

import org.junit.Test
import unibz.cs.semint.kprime.adapter.service.XMLSerializerJacksonAdapter
import unibz.cs.semint.kprime.domain.*
import kotlin.test.assertEquals

class ApplyChangeSetUseCaseTest {

    @Test
    fun test_deepclone_to_person_db() {
        //given
        val db = setUpPersonDb()
        val changeset = ChangeSet()
        val serializer = XMLSerializerJacksonAdapter()
        // when
        val newdb = ApplyChangeSetUseCase(serializer).apply(db, changeset)
        //then
        // checks that a mutation of original db isn't reflected on newdb.
        assertEquals("person",newdb.name)
        assertEquals(1,newdb.schema.constraints.size)
        assertEquals(Constraint.TYPE.PRIMARY_KEY.name,newdb.schema.constraints[0].type)
        assertEquals(1,newdb.schema.tables.size)
        newdb.schema.constraints[0].type=Constraint.TYPE.FOREIGN_KEY.name
        assertEquals(Constraint.TYPE.PRIMARY_KEY.name,db.schema.constraints[0].type)

    }

    @Test
    fun test_apply_empty_changeset_to_person_db_as_identity() {
        //given
        val db = setUpPersonDb()
        val changeset = ChangeSet()
        val serializer = XMLSerializerJacksonAdapter()
        // when
        val newdb = ApplyChangeSetUseCase(serializer).apply(db, changeset)
        //then
        // checks identity
        val serializeDb = serializer.serializeDatabase(db)
        val serializeNewDb = serializer.serializeDatabase(newdb)
        assertEquals(serializeDb,serializeNewDb)

    }

    fun setUpPersonDb():Database {
        val db = Database()
        db.name="person"
        val constraint = Constraint()
        val sourceCol = Column()
        constraint.source.columns.add(sourceCol)
        val targetCol = Column()
        constraint.target.columns.add(targetCol)
        constraint.type=Constraint.TYPE.PRIMARY_KEY.name
        db.schema.constraints.add(constraint)
        val table = Table()
        val column = Column()
        table.columns.add(column)
        db.schema.tables.add(table)
        return db
    }
}