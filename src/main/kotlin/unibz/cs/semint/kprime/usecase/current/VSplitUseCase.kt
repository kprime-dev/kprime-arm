package unibz.cs.semint.kprime.usecase.current

import unibz.cs.semint.kprime.domain.db.Constraint
import unibz.cs.semint.kprime.domain.db.Database
import unibz.cs.semint.kprime.domain.dml.ChangeSet
import unibz.cs.semint.kprime.domain.dml.CreateView

class VSplitUseCase {

    fun compute(metadataDatabase: Database): ChangeSet {
        // create changeset
        var changeSet = ChangeSet()

        // precondition: check for first table with functional dep, get the name
        var tableWithFunctionalName= metadataDatabase.schema.constraints()
                .filter { c -> c.type== Constraint.TYPE.FUNCTIONAL.name }
                .map { c -> c.source.table }.first()
        if (tableWithFunctionalName.isEmpty()) return changeSet

        // compute K
        val keyCols = metadataDatabase.schema.keyCols(tableWithFunctionalName)
        var key = keyCols.map { x -> x.name }.toSet()
        println("key $key")

        // compute LHS
        var lhsCols = metadataDatabase.schema.functionalLHS(tableWithFunctionalName)
                var lhs= lhsCols.map { x -> x.name }.toSet()
        println("lhs $lhs")
        if (lhs.isEmpty()) return changeSet

        // compute RHS
        val rhsCols = metadataDatabase.schema.functionalRHS(tableWithFunctionalName)
        var rhs = rhsCols.map { x -> x.name }.toSet()
        println("rhs $rhs")

        // compute Rest
        val table = metadataDatabase.schema.table(tableWithFunctionalName)
        if (table==null)  return changeSet

        val allCols = table.columns.toSet()
        val all = allCols.map { x -> x.name }.toSet()
        var rest = all.minus(key).minus(lhs).minus(rhs)
        val allNotKey = all.minus(key)
        val allNotKeyCols = allCols.minus(keyCols)

        println("rest $rest")

        // create view1 = select K,LHS,Rest
        var view1cols = "select "+key.plus(lhs).plus(rest).joinToString()+" from $tableWithFunctionalName"
        val view1 = CreateView()
        view1.viewName="tableName1"
        view1.text=view1cols
        changeSet.createView.add(view1)

        // create view2 = select LHS,RHS
        var view2cols = "select "+lhs.plus(rhs).joinToString()+" from $tableWithFunctionalName"
        val view2 = CreateView()
        view2.viewName="tableName2"
        view2.text=view2cols
        changeSet.createView.add(view2)

        // create key constraint tab2
        val keyTab2 = Constraint()
        keyTab2.type= Constraint.TYPE.PRIMARY_KEY.name
        keyTab2.source.table="tableName2"
        keyTab2.source.columns.addAll(lhsCols)
        changeSet.createConstraint.add(keyTab2)

        // create inclusion constraint tab2 tab1
        val inclusionTab2Tab1 = Constraint()
        inclusionTab2Tab1.type= Constraint.TYPE.DOUBLE_INCLUSION.name
        inclusionTab2Tab1.source.table="tableName2"
        inclusionTab2Tab1.source.columns.addAll(lhsCols)
        inclusionTab2Tab1.target.table="tableName1"
        inclusionTab2Tab1.target.columns.addAll(lhsCols)
        changeSet.createConstraint.add(inclusionTab2Tab1)
        return changeSet
    }
}