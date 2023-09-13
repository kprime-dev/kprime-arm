package unibz.cs.semint.kprime.usecase

import org.junit.Test
import unibz.cs.semint.kprime.adapter.service.XMLSerializerJacksonAdapter
import unibz.cs.semint.kprime.domain.db.*
import unibz.cs.semint.kprime.domain.ddl.*
import unibz.cs.semint.kprime.usecase.common.ApplyChangeSetUseCase
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
        assertEquals(1,newdb.schema.constraints().size)
        assertEquals(Constraint.TYPE.PRIMARY_KEY.name,newdb.schema.constraints()[0].type)
        assertEquals(1,newdb.schema.tables().size)
        newdb.schema.constraints()[0].type= Constraint.TYPE.FOREIGN_KEY.name
        assertEquals(Constraint.TYPE.PRIMARY_KEY.name,db.schema.constraints()[0].type)

    }

    @Test
    fun test_serialize_deserialize_db() {
        val serializer = XMLSerializerJacksonAdapter()
        val db = setUpPersonDb()
        //db.mappings = mutableListOf()
        val serializeDb = serializer.serializeDatabase(db)
        val newDb = serializer.deserializeDatabase(serializeDb)
        //newDb.mappings = mutableListOf()
        val serializeNewDb = serializer.serializeDatabase(newDb)
        assertEquals(serializeDb,serializeNewDb)
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
    fun test_serialize() {
        val db = setUpPersonDb()
        val serialized = XMLSerializerJacksonAdapter().prettyDatabase(db)
        assertEquals("""
            <database name="person" id="" source="" vocabulary="">
              <schema name="" id="">
                <tables>
                  <table name="person" id="" view="" condition="">
                    <columns>
                      <column name="" id="" nullable="false" dbtype=""/>
                    </columns>
                  </table>
                </tables>
                <constraints>
                  <constraint name="person.primaryKey" id="" type="PRIMARY_KEY">
                    <source name="" id="" table="">
                      <columns>
                        <column name="" id="" nullable="false" dbtype=""/>
                      </columns>
                    </source>
                    <target name="" id="" table="">
                      <columns>
                        <column name="" id="" nullable="false" dbtype=""/>
                      </columns>
                    </target>
                  </constraint>
                </constraints>
              </schema>
              <mappings/>
            </database>
        """.trimIndent(), serialized)
    }

    @Test
    fun test_apply_changeset_to_person_db() {
        //given
        val db = setUpPersonDb()
        val changeset = setUpPersonChangeSetSplitTable()
        val serializer = XMLSerializerJacksonAdapter()
        // when
        val newdb = ApplyChangeSetUseCase(serializer).apply(db, changeset)
        //then
        // checks identity
        val serializeNewDb = serializer.prettyDatabase(newdb)
        val expectedDb = """
            <database name="person" id="" source="" vocabulary="">
              <schema name="" id="">
                <tables>
                  <table name="person1" id="" view="" condition="">
                    <columns>
                      <column name="K" id="" nullable="false" dbtype="" type="K" dbtable=""/>
                      <column name="T" id="" nullable="false" dbtype="" type="T" dbtable=""/>
                      <column name="S" id="" nullable="false" dbtype="" type="S" dbtable=""/>
                    </columns>
                  </table>
                  <table name="person2" id="" view="" condition="">
                    <columns>
                      <column name="T" id="" nullable="false" dbtype="" type="T" dbtable=""/>
                      <column name="S" id="" nullable="false" dbtype="" type="S" dbtable=""/>
                    </columns>
                  </table>
                </tables>
                <constraints>
                  <constraint name="" id="" type="DOUBLE_INCLUSION">
                    <source name="" id="" table="">
                      <columns/>
                    </source>
                    <target name="" id="" table="">
                      <columns/>
                    </target>
                  </constraint>
                  <constraint name="" id="" type="PRIMARY_KEY">
                    <source name="" id="" table="">
                      <columns/>
                    </source>
                    <target name="" id="" table="">
                      <columns/>
                    </target>
                  </constraint>
                </constraints>
              </schema>
              <mappings/>
            </database>
        """.trimIndent()
        assertEquals(expectedDb,serializeNewDb)

    }

    private fun setUpPersonDb(): Database {
        val db = Database()
        db.name="person"
        val constraint = Constraint()
        val sourceCol = Column()
        constraint.source.columns.add(sourceCol)
        val targetCol = Column()
        constraint.target.columns.add(targetCol)
        constraint.type= Constraint.TYPE.PRIMARY_KEY.name
        constraint.name="person.primaryKey"
        db.schema.constraints().add(constraint)
        val table = Table()
        table.name = "person"
        val column = Column()
        table.columns.add(column)
        db.schema.tables().add(table)
        return db
    }


    private fun setUpPersonChangeSetSplitTable(): ChangeSet {
        val dropPersonTable = DropTable()  name "person"
        val dropPrimaryKeyConstraint = DropConstraint() name "person.primaryKey"
        val vsplitChangeSet = initChangeSet {} withId  "234"
        vsplitChangeSet minus dropPersonTable minus dropPrimaryKeyConstraint
        val table1 = CreateTable() name "person1" withColumn  "K" withColumn "T" withColumn "S"
        val table2 = CreateTable() name "person2" withColumn "T" withColumn "S"
        val doubleInc = Constraint.doubleInclusion()
        val person2Key = Constraint.addKey()
        vsplitChangeSet plus table1 plus  table2 plus doubleInc plus person2Key
        return vsplitChangeSet
    }

    private fun setUpPersonChangeSetAddKey(): ChangeSet {
        val changeSet = ChangeSet()
        val schema = Schema()
        schema.addTable("person:name,surname,address")
        val key = schema.buildKey("person", Column.set("name,surname"), Constraint.TYPE.PRIMARY_KEY.name)
        changeSet plus key
        assertEquals(1,changeSet.createConstraint.size)
        return changeSet
    }

    @Test
    fun test_apply_changeset_to_person_db2() {
        // given
        val personDB = Database()
        personDB.schema.addTable("person:name,surname,address")
        val personCS = setUpPersonChangeSetAddKey()
        assertEquals(1, personCS.createConstraint.size)
        val serializer = XMLSerializerJacksonAdapter()
        val cs_xml = serializer.prettyChangeSet(personCS)
        assertEquals("""
<changeSet id="">
  <createConstraint name="pkey_person_surname_name" id="" type="PRIMARY_KEY">
    <source name="" id="" table="person">
      <columns>
        <column name="surname" id="" nullable="false" dbtype="" dbtable=""/>
        <column name="name" id="" nullable="false" dbtype="" dbtable=""/>
      </columns>
    </source>
    <target name="" id="" table="person">
      <columns>
        <column name="surname" id="" nullable="false" dbtype="" dbtable=""/>
        <column name="name" id="" nullable="false" dbtype="" dbtable=""/>
      </columns>
    </target>
  </createConstraint>
</changeSet>
        """.trimIndent(),cs_xml)
        // when
        val newDB = ApplyChangeSetUseCase(serializer).apply(personDB, personCS)
        //then
        assertEquals(1,newDB.schema.constraints?.size)
        // checks identity
        val serializeNewDb = serializer.prettyDatabase(newDB)
        val expectedDb = """
<database name="" id="" source="" vocabulary="">
  <schema name="" id="">
    <tables>
      <table name="person" id="t1" view="" condition="">
        <columns>
          <column name="name" id="" nullable="false" dbtype="" type="name" dbtable=""/>
          <column name="surname" id="" nullable="false" dbtype="" type="surname" dbtable=""/>
          <column name="address" id="" nullable="false" dbtype="" type="address" dbtable=""/>
        </columns>
      </table>
    </tables>
    <constraints>
      <constraint name="pkey_person_surname_name" id="" type="PRIMARY_KEY">
        <source name="" id="" table="person">
          <columns>
            <column name="surname" id="" nullable="false" dbtype="" dbtable=""/>
            <column name="name" id="" nullable="false" dbtype="" dbtable=""/>
          </columns>
        </source>
        <target name="" id="" table="person">
          <columns>
            <column name="surname" id="" nullable="false" dbtype="" dbtable=""/>
            <column name="name" id="" nullable="false" dbtype="" dbtable=""/>
          </columns>
        </target>
      </constraint>
    </constraints>
  </schema>
  <mappings/>
</database>
        """.trimIndent()
        assertEquals(expectedDb,serializeNewDb)

    }


    private fun setUpPersonChangeSetAddDoubleInc(): ChangeSet {
        val changeSet = ChangeSet()
        val schema = Schema()
        val doubleInc = schema.buildDoubleInc("person",
                "employee","name,surname","name,surname")
        changeSet plus doubleInc
        return changeSet
    }


    // TODO Test Person with inclusion constraint changeset.
    @Test
    fun test_apply_changeset_to_person_double_inc() {
        // given
        val personDB = Database()
        personDB.schema.addTable("person:name,surname,address")
        personDB.schema.addTable("employee:name,surname,salary")
        val personCS = setUpPersonChangeSetAddDoubleInc()
        assertEquals(1, personCS.createConstraint.size)
        val serializer = XMLSerializerJacksonAdapter()
        val cs_xml = serializer.prettyChangeSet(personCS)
        assertEquals("""<changeSet id="">
  <createConstraint name="person_employee.doubleInc1" id="cdi1" type="DOUBLE_INCLUSION">
    <source name="" id="" table="person">
      <columns>
        <column name="surname" id="" nullable="false" dbtype="" dbtable=""/>
        <column name="name" id="" nullable="false" dbtype="" dbtable=""/>
      </columns>
    </source>
    <target name="" id="" table="employee">
      <columns>
        <column name="surname" id="" nullable="false" dbtype="" dbtable=""/>
        <column name="name" id="" nullable="false" dbtype="" dbtable=""/>
      </columns>
    </target>
  </createConstraint>
</changeSet>""",cs_xml)
        // when
        val newDB = ApplyChangeSetUseCase(serializer).apply(personDB, personCS)
        //then
        assertEquals(1,newDB.schema.constraints?.size)
        // checks identity
        val serializeNewDb = serializer.prettyDatabase(newDB)
        val expectedDb ="""<database name="" id="" source="" vocabulary="">
  <schema name="" id="">
    <tables>
      <table name="person" id="t1" view="" condition="">
        <columns>
          <column name="name" id="" nullable="false" dbtype="" type="name" dbtable=""/>
          <column name="surname" id="" nullable="false" dbtype="" type="surname" dbtable=""/>
          <column name="address" id="" nullable="false" dbtype="" type="address" dbtable=""/>
        </columns>
      </table>
      <table name="employee" id="t2" view="" condition="">
        <columns>
          <column name="name" id="" nullable="false" dbtype="" type="name" dbtable=""/>
          <column name="surname" id="" nullable="false" dbtype="" type="surname" dbtable=""/>
          <column name="salary" id="" nullable="false" dbtype="" type="salary" dbtable=""/>
        </columns>
      </table>
    </tables>
    <constraints>
      <constraint name="person_employee.doubleInc1" id="cdi1" type="DOUBLE_INCLUSION">
        <source name="" id="" table="person">
          <columns>
            <column name="surname" id="" nullable="false" dbtype="" dbtable=""/>
            <column name="name" id="" nullable="false" dbtype="" dbtable=""/>
          </columns>
        </source>
        <target name="" id="" table="employee">
          <columns>
            <column name="surname" id="" nullable="false" dbtype="" dbtable=""/>
            <column name="name" id="" nullable="false" dbtype="" dbtable=""/>
          </columns>
        </target>
      </constraint>
    </constraints>
  </schema>
  <mappings/>
</database>"""
        assertEquals(expectedDb,serializeNewDb)

    }


}