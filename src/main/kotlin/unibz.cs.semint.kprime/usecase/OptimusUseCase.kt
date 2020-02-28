package unibz.cs.semint.kprime.usecase

import unibz.cs.semint.kprime.domain.Applicability
import unibz.cs.semint.kprime.domain.Transformation
import unibz.cs.semint.kprime.domain.TransformationStrategy
import unibz.cs.semint.kprime.domain.ddl.Database
import unibz.cs.semint.kprime.domain.dml.ChangeSet
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.mapOf

class OptimusUseCase(transformationStrategy: TransformationStrategy) {

    val transfomers : MutableList<TransformerUseCase> = ArrayList<TransformerUseCase>()
    val transformationStrategy = transformationStrategy


    fun addTrasnsformers(transformers : List<TransformerUseCase>): OptimusUseCase {
        this.transfomers.addAll(transformers)
        return this
    }

    fun transfom(db: Database, params: Map<String, Any>):List<Transformation> {
        if (transfomers.size==0) {
            return listOf(errorTransformation(db, "Required at least one transformer. Use addTrasnsformers()."))
        }
        var totalTransformationPath = mutableListOf<Transformation>()
        var tryMoreSteps = true
        var maxSteps = 10
        var steps = 0
        while(tryMoreSteps && steps < maxSteps) {
            val transformersApplicable = transfomers
                    .filter { t -> t.decomposeApplicable(db, transformationStrategy).ok }
            println("transformersApplicable : $transformersApplicable ")
            val transfomersChoosed: List<TransformerUseCase> = transformationStrategy.choose(transformersApplicable)
            println("transfomersChoosed : $transfomersChoosed ")
            val transformationPath = transfomersChoosed
                    .map { t -> t.decompose(db, params) }.toList()
            println("transformationPath : $transformationPath ")
            totalTransformationPath.addAll(transformationPath)
            tryMoreSteps = false
            for (transformation in transformationPath) {
                if (transformation.changeset.size()>0) tryMoreSteps = true
            }
            steps++
        }
        return totalTransformationPath
    }

    private fun errorTransformation(db: Database, message: String): Transformation {
        return Transformation(ChangeSet(),db,message)

    }

}