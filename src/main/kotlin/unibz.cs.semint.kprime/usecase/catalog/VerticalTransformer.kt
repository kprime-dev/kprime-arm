package unibz.cs.semint.kprime.usecase.catalog

import unibz.cs.semint.kprime.domain.Applicability
import unibz.cs.semint.kprime.domain.Transformation
import unibz.cs.semint.kprime.domain.TransformationStrategy
import unibz.cs.semint.kprime.domain.ddl.Database
import unibz.cs.semint.kprime.domain.dml.ChangeSet
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