package unibz.cs.semint.kprime.adapter.strategy

import unibz.cs.semint.kprime.domain.TransformationStrategy
import unibz.cs.semint.kprime.usecase.TransformerUseCase

class TransformationStrategyYesAdapter : TransformationStrategy {
    override fun choose(transformersApplicable: List<TransformerUseCase>): List<TransformerUseCase> {
        return listOf(transformersApplicable.first())
    }
}