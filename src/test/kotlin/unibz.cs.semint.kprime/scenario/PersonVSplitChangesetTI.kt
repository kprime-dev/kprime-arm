package unibz.cs.semint.kprime.scenario

import org.junit.Test
import unibz.cs.semint.kprime.adapter.service.XMLSerializerJacksonAdapter
import unibz.cs.semint.kprime.domain.ddl.Constraint
import unibz.cs.semint.kprime.domain.ddl.doubleInclusion
import unibz.cs.semint.kprime.domain.ddl.key
import unibz.cs.semint.kprime.domain.dml.*
import kotlin.test.assertEquals

class PersonVSplitChangesetTI {

    @Test
    fun test_costruction_of_empty_changset(){
        val vsplitChangeSet = ChangeSet()
        val serializedChangeSet = XMLSerializerJacksonAdapter().serializeChangeSet(vsplitChangeSet)
        assertEquals("<changeSet id=\"\"/>",serializedChangeSet)
    }

    @Test
    fun test_costruction_of_vsplit_changset(){
        val dropPersonTable = DropTable()  name "person"
        val dropPrimaryKeyConstraint = DropConstraint() name "person.primaryKey"
        val vsplitChangeSet = initChangeSet {} withId  "234"
        vsplitChangeSet minus dropPersonTable minus dropPrimaryKeyConstraint
        val table1 = CreateTable() name "person1" withColumn  "K" withColumn "T" withColumn "S"
        val table2 = CreateTable() name "person2" withColumn "T" withColumn "S"
        val doubleInc = doubleInclusion {}
        val person2Key = key {  }
        vsplitChangeSet plusTable table1 plusTable  table2 plusConstraint doubleInc plusConstraint person2Key
        val serializedChangeSet = XMLSerializerJacksonAdapter().prettyChangeSet(vsplitChangeSet)
        println(serializedChangeSet)
        //assertEquals("<ChangeSet id=\"\"/>",serializedChangeSet)
    }

}