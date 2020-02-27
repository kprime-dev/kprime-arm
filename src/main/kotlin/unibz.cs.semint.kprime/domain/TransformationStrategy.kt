package unibz.cs.semint.kprime.domain

import unibz.cs.semint.kprime.usecase.TransformerUseCase

interface TransformationStrategy {
    fun choose(transformersApplicable: List<TransformerUseCase>): List<TransformerUseCase>
}