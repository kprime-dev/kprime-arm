package unibz.cs.semint.kprime.usecase

import unibz.cs.semint.kprime.domain.Applicability
import unibz.cs.semint.kprime.domain.Transformation
import unibz.cs.semint.kprime.domain.TransformationStrategy
import unibz.cs.semint.kprime.domain.ddl.Database
import unibz.cs.semint.kprime.usecase.current.TransformerHUseCase
import unibz.cs.semint.kprime.usecase.current.TransformerVUseCase
import unibz.cs.semint.kprime.usecase.service.FileIOService
import unibz.cs.semint.kprime.usecase.service.IXMLSerializerService
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

    fun transfom(db: Database):List<Transformation> {
        if (transfomers.size==0) {
            println("Required at least one transformer. Use addTrasnsformers().")
        }
        val transformationPath = transfomers
                .filter { t -> t.decomposeApplicable(db,transformationStrategy).ok }
                .map { t ->
                    val params = mapOf(
                            "workingDir" to ""
                    )
                    t.decompose(db, params)
                }.toList()
        return transformationPath
    }


    // FIXME deprecated
    private fun oldtransfom(db: Database):Database {
        var dbTrasformable = db
        var moreTrasformable = true
        while (moreTrasformable) {
            moreTrasformable = false
            val pair = tryUseAnyTransfomers(db, dbTrasformable, moreTrasformable)
            dbTrasformable = pair.first
            moreTrasformable = pair.second
        }
        return dbTrasformable
    }

    // FIXME deprecated
    private fun tryUseAnyTransfomers(db: Database, dbTrasformable: Database, trasformable: Boolean): Pair<Database, Boolean> {
        var dbTrasformable1 = dbTrasformable
        var moreTrasformable = trasformable
        for (transfomer in transfomers) {
            val pair = checkDecomposabilityUserAknowledgeAndTransform(
                    transfomer, db, dbTrasformable1, moreTrasformable)
            dbTrasformable1 = pair.first
            moreTrasformable = pair.second
        }
        return Pair(dbTrasformable1, moreTrasformable)
    }

    // FIXME deprecated
    private fun checkDecomposabilityUserAknowledgeAndTransform(
            transfomer: TransformerUseCase, db: Database, dbTrasformable1: Database, trasformable1: Boolean): Pair<Database, Boolean> {
        var dbTrasformable11 = dbTrasformable1
        var moreTrasformable = trasformable1
        val decomposability : Applicability = transfomer.decomposeApplicable(db,transformationStrategy)
        moreTrasformable = decomposability.ok
        if (decomposability.ok) {
            if (mockedUserAknowledge(decomposability.message)) {
                val params = mapOf(
                        "" to ""
                )
                val transformation: Transformation = transfomer.decompose(db, params)
                print(transformation.changeset)
                print(transformation.newdb)
                dbTrasformable11 = transformation.newdb
            }
        }
        return Pair(dbTrasformable11, moreTrasformable)
    }

    // FIXME deprecated by strategy
    private fun userAknowledge(message: String): Boolean {
        println()
        println(message)
        println("Y/N")
        val reader = Scanner(System.`in`)
        val response = reader.next()
        return response.toUpperCase().equals("Y")
    }

    // FIXME deprecated
    private fun mockedUserAknowledge(message: String): Boolean {
        println()
        println(message)
        println("Y/N")
        return true
    }
}