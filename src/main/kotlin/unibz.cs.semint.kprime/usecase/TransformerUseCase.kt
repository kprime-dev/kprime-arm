package unibz.cs.semint.kprime.usecase

import unibz.cs.semint.kprime.domain.ddl.Database

interface TransformerUseCase {
    fun decompose(db: Database): Database
    fun compose(db: Database): Database
}