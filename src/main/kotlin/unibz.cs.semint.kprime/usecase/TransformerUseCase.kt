package unibz.cs.semint.kprime.usecase

import unibz.cs.semint.kprime.domain.Applicability
import unibz.cs.semint.kprime.domain.Transformation
import unibz.cs.semint.kprime.domain.TransformationStrategy
import unibz.cs.semint.kprime.domain.ddl.Database

interface TransformerUseCase {
    fun decompose(db: Database, params:Map<String,Any>): Transformation
    fun compose(db: Database, params:Map<String,Any>): Transformation
    fun decomposeApplicable(db: Database, transformationStrategy : TransformationStrategy): Applicability
    fun composeApplicable(db: Database, transformationStrategy : TransformationStrategy): Applicability
}