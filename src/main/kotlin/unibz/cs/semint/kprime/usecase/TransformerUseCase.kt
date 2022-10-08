package unibz.cs.semint.kprime.usecase

import unibz.cs.semint.kprime.domain.dtl.Applicability
import unibz.cs.semint.kprime.domain.dtl.Transformation
import unibz.cs.semint.kprime.domain.dtl.TransformationStrategy
import unibz.cs.semint.kprime.domain.db.Database
import unibz.cs.semint.kprime.domain.ddl.ChangeSet

interface TransformerUseCase {
    fun decompose(db: Database, params:Map<String,Any>): Transformation
    fun compose(db: Database, params:Map<String,Any>): Transformation
    fun decomposeApplicable(db: Database, transformationStrategy : TransformationStrategy, params:Map<String,Any> ): Applicability
    fun composeApplicable(db: Database, transformationStrategy : TransformationStrategy, params:Map<String,Any>): Applicability
    fun errorTransformation(db: Database, message: String): Transformation {
        return Transformation(ChangeSet(),db,message)
    }
}