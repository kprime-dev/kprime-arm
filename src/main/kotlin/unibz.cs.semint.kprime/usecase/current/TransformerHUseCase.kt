package unibz.cs.semint.kprime.usecase.current

import unibz.cs.semint.kprime.domain.Applicability
import unibz.cs.semint.kprime.domain.Transformation
import unibz.cs.semint.kprime.domain.TransformationStrategy
import unibz.cs.semint.kprime.domain.ddl.Database
import unibz.cs.semint.kprime.domain.dml.ChangeSet
import unibz.cs.semint.kprime.usecase.TransformerUseCase

class TransformerHUseCase : TransformerUseCase {
    override fun decompose(db: Database, vararg params:String): Transformation {
        // TODO("not implemented")
        return Transformation(ChangeSet(), Database())
    }

    override fun compose(db: Database, vararg params:String): Transformation {
        // TODO("not implemented")
        return Transformation(ChangeSet(), Database())
    }

    override fun decomposeApplicable(db: Database, transformationStrategy: TransformationStrategy): Applicability {
        // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        return Applicability(false,"TransformerHUseCase.decomposeApplicable")
    }

    override fun composeApplicable(db: Database, transformationStrategy: TransformationStrategy): Applicability {
        // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        return Applicability(false,"TransformerHUseCase.composeApplicable")
    }

}