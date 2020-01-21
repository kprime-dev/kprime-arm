package unibz.cs.semint.kprime.usecase

import unibz.cs.semint.kprime.domain.ChangeSet
import unibz.cs.semint.kprime.domain.Database

interface TransformerUseCase {
    fun decompose(db: Database):Database
    fun compose(db:Database): Database
}