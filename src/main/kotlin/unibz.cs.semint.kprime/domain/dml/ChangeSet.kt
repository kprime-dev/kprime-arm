package unibz.cs.semint.kprime.domain.dml

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import unibz.cs.semint.kprime.domain.ddl.Database

fun initChangeSet(alfa:ChangeSet.()->Unit):ChangeSet {
    val changeSet = ChangeSet()
    return changeSet
}

@JacksonXmlRootElement(localName = "changeSet")
class ChangeSet() {


    @JacksonXmlProperty(isAttribute = true)
    var id: String = ""

    @JacksonXmlProperty(isAttribute = true)
    var author: String? = null

    @JacksonXmlProperty(isAttribute = true)
    var time: String? = null

    @JacksonXmlProperty(isAttribute = true)
    var parent: String? = null

    @JacksonXmlProperty(isAttribute = true)
    var sqlCommands: MutableList<String>? = null

    @JacksonXmlElementWrapper(useWrapping=false)
    var createView= ArrayList<CreateView>()

    @JacksonXmlElementWrapper(useWrapping=false)
    var createTable= ArrayList<CreateTable>()

    @JacksonXmlElementWrapper(useWrapping = false)
    var createColumn= ArrayList<CreateColumn>()

    @JacksonXmlElementWrapper(useWrapping=false)
    var createConstraint= ArrayList<CreateConstraint>()

    @JacksonXmlElementWrapper(useWrapping=false)
    var createMapping= ArrayList<CreateMapping>()

    @JacksonXmlElementWrapper(useWrapping=false)
    var dropView= ArrayList<DropView>()

    @JacksonXmlElementWrapper(useWrapping=false)
    var dropTable= ArrayList<DropTable>()

    @JacksonXmlElementWrapper(useWrapping = false)
    var dropColumn = ArrayList<DropColumn>()

    @JacksonXmlElementWrapper(useWrapping=false)
    var dropConstraint= ArrayList<DropConstraint>()

    @JacksonXmlElementWrapper(useWrapping=false)
    var dropMapping= ArrayList<DropMapping>()

    @JacksonXmlElementWrapper(useWrapping=false)
    var alterTable : MutableList<AlterTable>? = ArrayList()

    infix fun withId(id:String) = apply {
        this.id=id
    }

    infix fun plus(alterTable: AlterTable)= apply {
        this.alterTable?.add(alterTable)
    }

    infix fun plus(createView: CreateView)= apply{
        this.createView.add(createView)
    }

    infix fun plus(createTable: CreateTable)= apply{
        this.createTable.add(createTable)
    }

    infix fun plus(createConstraint: CreateConstraint)= apply{
//        println("plus  $createConstraint")
        this.createConstraint.add(createConstraint)
    }

    infix fun plus(createMapping: CreateMapping)= apply{
        this.createMapping.add(createMapping)
    }

    infix fun minus(view: DropView)= apply{
        this.dropView.add(view)
    }

    infix fun minus(droptable:DropTable) = apply {
        this.dropTable.add(droptable)
    }

    infix fun minus(dropconstraint:DropConstraint) = apply {
        this.dropConstraint.add(dropconstraint)
    }

    infix fun minus(dropMapping: DropMapping) = apply {
        this.dropMapping.add(dropMapping)
    }

    fun size(): Int {
        return createView.size +createTable.size + createConstraint.size + createMapping.size + dropView.size + dropTable.size + dropConstraint.size + dropMapping.size
    }

    @JsonIgnore
    fun isEmpty() : Boolean {
        return this.size()==0
    }

    override fun toString(): String {
        return "ChangeSet(id='$id', author=$author, time=$time, parent=$parent, createView=$createView, createTable=$createTable, createConstraint=$createConstraint, createMapping=$createMapping, dropView=$dropView, dropTable=$dropTable, dropConstraint=$dropConstraint, dropMapping=$dropMapping)"
    }

    companion object {
        fun fromDatabase(db:Database):ChangeSet {
            var cs = ChangeSet()
            if (db.schema.tables!=null) cs.createTable.addAll(db.schema.tables!!)
            if (db.schema.constraints!=null) cs.createConstraint.addAll(db.schema.constraints!!)
            return cs
        }
    }

    fun add(changeSetToMerge:ChangeSet) {
        this.sqlCommands = this.sqlCommands?:ArrayList()
        if (changeSetToMerge.sqlCommands!=null) {
            this.sqlCommands!!.addAll(changeSetToMerge.sqlCommands!!)
        }
        this.createConstraint.addAll(changeSetToMerge.createConstraint)
        this.createTable.addAll(changeSetToMerge.createTable)
        this.createMapping.addAll(changeSetToMerge.createMapping)
        this.createColumn.addAll(changeSetToMerge.createColumn)
        this.createView.addAll(changeSetToMerge.createView)

        this.dropConstraint.addAll(changeSetToMerge.dropConstraint)
        this.dropTable.addAll(changeSetToMerge.dropTable)
        this.dropMapping.addAll(changeSetToMerge.dropMapping)
        this.dropColumn.addAll(changeSetToMerge.dropColumn)
        this.dropView.addAll(changeSetToMerge.dropView)
    }
}