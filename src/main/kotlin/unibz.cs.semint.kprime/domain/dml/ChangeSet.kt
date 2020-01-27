package unibz.cs.semint.kprime.domain.dml

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement

fun initChangeSet(alfa:ChangeSet.()->Unit):ChangeSet {
    val changeSet = ChangeSet()
    return changeSet
}

@JacksonXmlRootElement(localName = "changeSet")
class ChangeSet() {


    @JacksonXmlProperty(isAttribute = true)
    var id: String = ""

    @JacksonXmlElementWrapper(useWrapping=false)
    var createView= ArrayList<CreateView>()

    @JacksonXmlElementWrapper(useWrapping=false)
    var createTable= ArrayList<CreateTable>()

    @JacksonXmlElementWrapper(useWrapping=false)
    var createConstraint= ArrayList<CreateConstraint>()

    @JacksonXmlElementWrapper(useWrapping=false)
    var dropView= ArrayList<DropView>()

    @JacksonXmlElementWrapper(useWrapping=false)
    var dropTable= ArrayList<DropTable>()

    @JacksonXmlElementWrapper(useWrapping=false)
    var dropConstraint= ArrayList<DropConstraint>()

    infix fun withId(id:String) = apply {
        this.id=id
    }

    infix fun plusView(createView: CreateView)= apply{
        this.createView.add(createView)
    }

    infix fun plusTable(createTable: CreateTable)= apply{
        this.createTable.add(createTable)
    }
    infix fun plusConstraint(createConstraint: CreateConstraint)= apply{
        this.createConstraint.add(createConstraint)
    }
    infix fun minusView(view: DropView)= apply{
        this.dropView.add(view)
    }
    infix fun minus(droptable:DropTable) = apply {
        this.dropTable.add(droptable)
    }
    infix fun minus(dropconstraint:DropConstraint) = apply {
        this.dropConstraint.add(dropconstraint)
    }
}