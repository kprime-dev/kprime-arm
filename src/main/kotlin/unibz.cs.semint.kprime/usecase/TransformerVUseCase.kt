package unibz.cs.semint.kprime.usecase

import unibz.cs.semint.kprime.domain.Database
import unibz.cs.semint.kprime.usecase.service.IXMLSerializerService

class TransformerVUseCase(serializer:IXMLSerializerService) :TransformerUseCase {
    val serializer = serializer

    override fun decompose(db: Database): Database {
        val changeSet = VSplitUseCase().compute(db)
        return ApplyChangeSetUseCase(serializer).apply(db,changeSet)
    }

    override fun compose(db: Database): Database {
        val changeSet = VJoinUseCase().compute(db)
        return ApplyChangeSetUseCase(serializer).apply(db,changeSet)
    }

}