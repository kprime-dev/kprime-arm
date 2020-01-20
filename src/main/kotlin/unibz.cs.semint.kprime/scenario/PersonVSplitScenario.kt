package unibz.cs.semint.kprime.scenario

import unibz.cs.semint.kprime.adapter.service.XMLSerializerJacksonAdapter
import unibz.cs.semint.kprime.domain.*
import unibz.cs.semint.kprime.usecase.XMLSerializeUseCase

class PersonVSplitScenario {

    fun run(): ChangeSet {
        val personMetadata = buildPersonMetadata()
        println(XMLSerializeUseCase(XMLSerializerJacksonAdapter()).prettyDatabase(personMetadata))
        return vsplitPersonMetadata(personMetadata)
    }

    private fun buildPersonMetadata(): Database {
        val db = Database()
        val personTable = Table()
        personTable.name= "person"
        val colSSN = Column("SSN", "id.SSN", "dbname.SSN")
        personTable.columns.add(colSSN)
        colSSN.nullable=false

        val colT = Column("T", "id.T", "dbname.T")
        personTable.columns.add(colT)
        colT.nullable=false

        val colS = Column("S", "id.S", "dbname.S")
        colS.nullable=true
        personTable.columns.add(colS)

        val colX = Column("X", "id.X", "dbname.X")
        colX.nullable=true
        personTable.columns.add(colX)

        db.schema.key("person", mutableSetOf(colSSN))
        db.schema.functional("person", mutableSetOf(colT), mutableSetOf(colS))

        db.schema.tables.add(personTable)

        return db
    }

    private fun vsplitPersonMetadata(personMetadata: Database): ChangeSet {
        // check for functional dep
        // create changeset
        var changeSet = ChangeSet()

        // compute K
        val keyCols = personMetadata.schema.key("person")
        var key = keyCols.map { x -> x.name }.toSet()
        println("key $key")
        // compute LHS
        var lhsCols = personMetadata.schema.functionalLHS("person")
                var lhs= lhsCols.map { x -> x.name }.toSet()
        println("lhs $lhs")
        if (lhs.isEmpty()) return changeSet
        // compute RHS
        val rhsCols = personMetadata.schema.functionalRHS("person")
        var rhs = rhsCols.map { x -> x.name }.toSet()
        println("rhs $rhs")
        // compute Rest
        val allCols = personMetadata.schema.table("person").columns.toSet()
        val all = allCols.map { x -> x.name }.toSet()
        var rest = all.minus(key).minus(lhs).minus(rhs)
        val allNotKey = all.minus(key)
        val allNotKeyCols = allCols.minus(keyCols)

        println("rest $rest")

        // create view1 = select K,LHS,Rest
        var view1cols = "select "+key.plus(lhs).plus(rest).joinToString()+" from person"
        val view1 = CreateView()
        view1.viewName="tableName1"
        view1.text=view1cols
        changeSet.createView.add(view1)
        // create view2 = select LHS,RHS
        var view2cols = "select "+lhs.plus(rhs).joinToString()+" from person"
        val view2 = CreateView()
        view2.viewName="tableName2"
        view2.text=view2cols
        changeSet.createView.add(view2)
        // create inclusion constraint tab1 tab2
        val inclusionTab1Tab2 = Constraint()
        inclusionTab1Tab2.type=Constraint.TYPE.INCLUSION.name
        inclusionTab1Tab2.source.table="tableName1"
        inclusionTab1Tab2.source.columns.addAll(lhsCols)
        inclusionTab1Tab2.target.table="tableName2"
        inclusionTab1Tab2.target.columns.addAll(lhsCols)
        changeSet.createConstraint.add(inclusionTab1Tab2)
        // create inclusion constraint tab2 tab1
        val inclusionTab2Tab1 = Constraint()
        inclusionTab2Tab1.type=Constraint.TYPE.INCLUSION.name
        inclusionTab2Tab1.source.table="tableName2"
        inclusionTab2Tab1.source.columns.addAll(lhsCols)
        inclusionTab2Tab1.target.table="tableName1"
        inclusionTab2Tab1.target.columns.addAll(lhsCols)
        changeSet.createConstraint.add(inclusionTab2Tab1)
        // create primary key on tab2
        val primaryTab2 = Constraint()
        primaryTab2.type=Constraint.TYPE.PRIMARY_KEY.name
        primaryTab2.source.table="tableName2"
        primaryTab2.source.columns.addAll(lhsCols)
        primaryTab2.target.table="tableName2"
        primaryTab2.target.columns.addAll(rhsCols)
        changeSet.createConstraint.add(primaryTab2)
        return changeSet
    }
}