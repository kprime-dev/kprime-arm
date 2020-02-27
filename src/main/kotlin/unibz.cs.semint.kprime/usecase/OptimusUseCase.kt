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

class OptimusUseCase {

    val transfomers : MutableList<TransformerUseCase> = ArrayList<TransformerUseCase>()
    val transformationStrategy : TransformationStrategy

    constructor(serializer : IXMLSerializerService, fileIOService: FileIOService, transformationStrategy: TransformationStrategy) {
        transfomers.add(TransformerHUseCase())
        transfomers.add(TransformerVUseCase(serializer,fileIOService))
        this.transformationStrategy = transformationStrategy
    }

    fun transfom(db: Database):List<Transformation> {
        val transformationPath = transfomers
                .filter { t -> t.decomposeApplicable(db,transformationStrategy).ok }
                .map { t -> t.decompose(db) }.toList()
        return transformationPath
    }


    fun oldtransfom(db: Database):Database {
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

    private fun checkDecomposabilityUserAknowledgeAndTransform(
            transfomer: TransformerUseCase, db: Database, dbTrasformable1: Database, trasformable1: Boolean): Pair<Database, Boolean> {
        var dbTrasformable11 = dbTrasformable1
        var moreTrasformable = trasformable1
        val decomposability : Applicability = transfomer.decomposeApplicable(db,transformationStrategy)
        moreTrasformable = decomposability.ok
        if (decomposability.ok) {
            if (mockedUserAknowledge(decomposability.message)) {
                val transformation: Transformation = transfomer.decompose(db)
                print(transformation.changeset)
                print(transformation.newdb)
                dbTrasformable11 = transformation.newdb
            }
        }
        return Pair(dbTrasformable11, moreTrasformable)
    }

    private fun userAknowledge(message: String): Boolean {
        println()
        println(message)
        println("Y/N")
        val reader = Scanner(System.`in`)
        val response = reader.next()
        return response.toUpperCase().equals("Y")
    }

    private fun mockedUserAknowledge(message: String): Boolean {
        println()
        println(message)
        println("Y/N")
        return true
    }
}