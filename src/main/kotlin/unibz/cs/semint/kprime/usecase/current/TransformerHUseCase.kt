package unibz.cs.semint.kprime.usecase.current

import unibz.cs.semint.kprime.domain.dtl.Applicability
import unibz.cs.semint.kprime.domain.dtl.Transformation
import unibz.cs.semint.kprime.domain.dtl.TransformationStrategy
import unibz.cs.semint.kprime.domain.db.Database
import unibz.cs.semint.kprime.domain.ddl.ChangeSet
import unibz.cs.semint.kprime.usecase.TransformerUseCase

class TransformerHUseCase : TransformerUseCase {
    override fun decompose(db: Database, params:Map<String,Any>): Transformation {
        // TODO("not implemented")
        return Transformation(ChangeSet(), Database(), "TransformerHUseCase.decompose")
    }

    override fun compose(db: Database, params:Map<String,Any>): Transformation {
        // TODO("not implemented")
        return Transformation(ChangeSet(), Database(), "TransformerHUseCase.compose")
    }

    override fun decomposeApplicable(db: Database, transformationStrategy: TransformationStrategy, params: Map<String, Any>): Applicability {
        // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        val tranformerParmeters = mutableMapOf<String,Any>()
        return Applicability(false, "TransformerHUseCase.decomposeApplicable", tranformerParmeters)
    }

    override fun composeApplicable(db: Database, transformationStrategy: TransformationStrategy, params: Map<String, Any>): Applicability {
        // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        val tranformerParmeters = mutableMapOf<String,Any>()
        return Applicability(false, "TransformerHUseCase.composeApplicable", tranformerParmeters)
    }

    override fun toString(): String {
        return "TransformerHUseCase()"
    }


}