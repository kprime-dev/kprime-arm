package unibz.cs.semint.kprime.usecase.catalog

import unibz.cs.semint.kprime.domain.dtl.Applicability
import unibz.cs.semint.kprime.domain.dtl.Transformation
import unibz.cs.semint.kprime.domain.dtl.TransformationStrategy
import unibz.cs.semint.kprime.domain.db.Database
import unibz.cs.semint.kprime.domain.ddl.ChangeSet
import unibz.cs.semint.kprime.usecase.TransformerUseCase

class VerticalTransformer : TransformerUseCase {
    override fun decompose(db: Database, params: Map<String, Any>): Transformation {
        TODO("Not yet implemented")
        val change = ChangeSet()
        return Transformation(change,db,"NOT TRANSFORMED")
    }

    override fun compose(db: Database, params: Map<String, Any>): Transformation {
        TODO("Not yet implemented")
    }

    override fun decomposeApplicable(db: Database, transformationStrategy: TransformationStrategy, params: Map<String, Any>): Applicability {
        TODO("Not yet implemented")
    }

    override fun composeApplicable(db: Database, transformationStrategy: TransformationStrategy, params: Map<String, Any>): Applicability {
        TODO("Not yet implemented")
    }
}