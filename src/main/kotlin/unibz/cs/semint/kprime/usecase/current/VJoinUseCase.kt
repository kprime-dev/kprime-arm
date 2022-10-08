package unibz.cs.semint.kprime.usecase.current

import unibz.cs.semint.kprime.domain.dml.ChangeSet
import unibz.cs.semint.kprime.domain.db.Constraint
import unibz.cs.semint.kprime.domain.dml.CreateView
import unibz.cs.semint.kprime.domain.db.Database

class VJoinUseCase {

    fun compute(database: Database): ChangeSet {
        val changeSet: ChangeSet = ChangeSet()


        var tab1 = ""
        var tab2 = ""
        var colJoin1 = ""
        var colJoin2 = ""

        // if there is a double inclusion in tab1 and tab2 and a primary key on colJoin
        for (const in database.schema.constraints()) {
            if (const.type== Constraint.TYPE.DOUBLE_INCLUSION.name) {
                tab1 = const.source.table
                tab2= const.target.table
                colJoin1 = const.source.columns[0].name
                colJoin2 = const.target.columns[0].name
                break
            }
        }
        if (tab1=="") {
            return changeSet
        }

        val view1cols = "select * from $tab1 join $tab2 on $tab1.$colJoin1 = $tab2.$colJoin2"
        val view1 = CreateView()
        view1.viewName="tableJoin"
        view1.text=view1cols
        changeSet.createView.add(view1)
        return changeSet
    }

}