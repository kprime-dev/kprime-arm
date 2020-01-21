package unibz.cs.semint.kprime.usecase

import unibz.cs.semint.kprime.domain.Database

class TransformerVUseCase :TransformerUseCase {

    override fun decompose(db: Database): Database {
        val changeSet = VSplitUseCase().compute(db)
        return ApplyChangeSetUseCase().apply(db,changeSet)
    }

    override fun compose(db: Database): Database {
        val changeSet = VJoinUseCase().compute(db)
        return ApplyChangeSetUseCase().apply(db,changeSet)
    }

}