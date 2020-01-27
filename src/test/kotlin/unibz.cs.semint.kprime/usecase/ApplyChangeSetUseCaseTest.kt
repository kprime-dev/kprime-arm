package unibz.cs.semint.kprime.usecase

import org.junit.Test
import unibz.cs.semint.kprime.adapter.service.XMLSerializerJacksonAdapter
import unibz.cs.semint.kprime.domain.ddl.*
import unibz.cs.semint.kprime.domain.dml.*
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
        newdb.schema.constraints[0].type= Constraint.TYPE.FOREIGN_KEY.name
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

    @Test
    fun test_apply_changeset_to_person_db() {
        //given
        val db = setUpPersonDb()
        val changeset = setUpChangeSet()
        val serializer = XMLSerializerJacksonAdapter()
        // when
        val newdb = ApplyChangeSetUseCase(serializer).apply(db, changeset)
        //then
        // checks identity
        val serializeDb = serializer.serializeDatabase(db)
        val serializeNewDb = serializer.prettyDatabase(newdb)
        println(serializer.prettyDatabase(newdb))
        val expectedDb = """
            <database name="person" id="">
              <schema name="" id="">
                <tables>
                  <tables name="person1" id="" view="" condition="">
                    <columns>
                      <columns name="K" id="" dbname="" nullable="false" dbtype=""/>
                      <columns name="T" id="" dbname="" nullable="false" dbtype=""/>
                      <columns name="S" id="" dbname="" nullable="false" dbtype=""/>
                    </columns>
                  </tables>
                  <tables name="person2" id="" view="" condition="">
                    <columns>
                      <columns name="T" id="" dbname="" nullable="false" dbtype=""/>
                      <columns name="S" id="" dbname="" nullable="false" dbtype=""/>
                    </columns>
                  </tables>
                </tables>
                <constraints>
                  <constraints name="" id="" type="DOUBLE_INCLUSION">
                    <source name="" id="" table="">
                      <columns/>
                    </source>
                    <target name="" id="" table="">
                      <columns/>
                    </target>
                  </constraints>
                  <constraints name="" id="" type="PRIMARY_KEY">
                    <source name="" id="" table="">
                      <columns/>
                    </source>
                    <target name="" id="" table="">
                      <columns/>
                    </target>
                  </constraints>
                </constraints>
              </schema>
            </database>
        """.trimIndent()
        assertEquals(expectedDb,serializeNewDb)

    }

    fun setUpPersonDb(): Database {
        val db = Database()
        db.name="person"
        val constraint = Constraint()
        val sourceCol = Column()
        constraint.source.columns.add(sourceCol)
        val targetCol = Column()
        constraint.target.columns.add(targetCol)
        constraint.type= Constraint.TYPE.PRIMARY_KEY.name
        constraint.name="person.primaryKey"
        db.schema.constraints.add(constraint)
        val table = Table()
        table.name="person"
        val column = Column()
        table.columns.add(column)
        db.schema.tables.add(table)
        return db
    }

    fun setUpChangeSet(): ChangeSet {
        val dropPersonTable = DropTable()  name "person"
        val dropPrimaryKeyConstraint = DropConstraint() name "person.primaryKey"
        val vsplitChangeSet = initChangeSet {} withId  "234"
        vsplitChangeSet minus dropPersonTable minus dropPrimaryKeyConstraint
        val table1 = CreateTable() name "person1" withColumn  "K" withColumn "T" withColumn "S"
        val table2 = CreateTable() name "person2" withColumn "T" withColumn "S"
        val doubleInc = doubleInclusion {}
        val person2Key = key {  }
        vsplitChangeSet plusTable table1 plusTable  table2 plusConstraint doubleInc plusConstraint person2Key
        return vsplitChangeSet
    }
}