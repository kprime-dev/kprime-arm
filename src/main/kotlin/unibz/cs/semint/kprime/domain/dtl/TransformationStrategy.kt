package unibz.cs.semint.kprime.domain.dtl

import unibz.cs.semint.kprime.usecase.TransformerUseCase

interface TransformationStrategy {
    fun choose(transformersApplicable: List<TransformerUseCase>): List<TransformerUseCase>
}