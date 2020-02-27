package unibz.cs.semint.kprime.usecase

import unibz.cs.semint.kprime.domain.Applicability
import unibz.cs.semint.kprime.domain.Transformation
import unibz.cs.semint.kprime.domain.ddl.Database

interface TransformerUseCase {
    fun decompose(db: Database, vararg params:String): Transformation
    fun compose(db: Database, vararg params:String): Transformation
    fun decomposeApplicable(): Applicability
    fun composeApplicable(): Applicability
}