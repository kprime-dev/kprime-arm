package unibz.cs.semint.kprime.usecase

import unibz.cs.semint.kprime.domain.dtl.Transformation
import unibz.cs.semint.kprime.domain.dtl.TransformationStrategy
import unibz.cs.semint.kprime.domain.db.Database
import unibz.cs.semint.kprime.domain.ddl.ChangeSet
import kotlin.collections.ArrayList

class OptimusUseCase(transformationStrategy: TransformationStrategy) {

    val transfomers : MutableList<TransformerUseCase> = ArrayList<TransformerUseCase>()
    val transformationStrategy = transformationStrategy


    fun addTrasnsformers(transformers : List<TransformerUseCase>): OptimusUseCase {
        this.transfomers.addAll(transformers)
        return this
    }

    fun transfom(database: Database, params: Map<String, Any>):List<Transformation> {


        var newdb = database
        if (transfomers.size==0) {
            return listOf(errorTransformation(database, "Required at least one transformer. Use addTrasnsformers()."))
        }

        var totalTransformationPath = mutableListOf<Transformation>()
        var tryMoreSteps = true
        var maxSteps = 10
        var steps = 1
        while(tryMoreSteps && steps < maxSteps) {
            println("------------------------------------- $steps ---------------------")

            val transformersApplicable = transfomers
                    .filter { t -> t.decomposeApplicable(newdb, transformationStrategy, emptyMap()).ok }
            println("transformersApplicable : $transformersApplicable ")

            val transfomersChoosed: List<TransformerUseCase> = transformationStrategy.choose(transformersApplicable)
            println("transfomersChoosed : $transfomersChoosed ")

            val transformationPath = transfomersChoosed
                    .map { t -> t.decompose(newdb, params) }.toList()

            if (transformationPath.size > 0)
                newdb = transformationPath.last().newdb
            println("transformationPath : $transformationPath ")
            totalTransformationPath.addAll(transformationPath)
            tryMoreSteps = false
            for (transformation in transformationPath) {
                if (transformation.changeset.size()>0) tryMoreSteps = true
            }
            steps++
        }
        println("-------------------------------------------- END ---------------------")
        return totalTransformationPath
    }

    private fun errorTransformation(db: Database, message: String): Transformation {
        return Transformation(ChangeSet(),db,message)

    }

}