package unibz.cs.semint.kprime.usecase

import unibz.cs.semint.kprime.domain.Applicability
import unibz.cs.semint.kprime.domain.Transformation
import unibz.cs.semint.kprime.domain.ddl.Database
import unibz.cs.semint.kprime.usecase.current.TransformerHUseCase
import unibz.cs.semint.kprime.usecase.current.TransformerVUseCase
import unibz.cs.semint.kprime.usecase.service.FileIOService
import unibz.cs.semint.kprime.usecase.service.IXMLSerializerService

class OptimusUseCase {

    val transfomers : MutableList<TransformerUseCase> = ArrayList<TransformerUseCase>()

    constructor(serializer : IXMLSerializerService, fileIOService: FileIOService) {
        transfomers.add(TransformerHUseCase())
        transfomers.add(TransformerVUseCase(serializer,fileIOService))
    }

    fun transfom(db: Database):Database {
        var dbTrasformable = db
        var trasformable = true
        while (trasformable) {
            trasformable = false
            val pair = tryUseTransfomers(db, dbTrasformable, trasformable)
            dbTrasformable = pair.first
            trasformable = pair.second
        }
        return dbTrasformable
    }

    private fun tryUseTransfomers(db: Database, dbTrasformable: Database, trasformable: Boolean): Pair<Database, Boolean> {
        var dbTrasformable1 = dbTrasformable
        var trasformable1 = trasformable
        for (transfomer in transfomers) {
            val pair = checkAndTransform(transfomer, db, dbTrasformable1, trasformable1)
            dbTrasformable1 = pair.first
            trasformable1 = pair.second
        }
        return Pair(dbTrasformable1, trasformable1)
    }

    private fun checkAndTransform(transfomer: TransformerUseCase, db: Database, dbTrasformable1: Database, trasformable1: Boolean): Pair<Database, Boolean> {
        var dbTrasformable11 = dbTrasformable1
        var trasformable11 = trasformable1
        val decomposability : Applicability = transfomer.decomposeApplicable()
        if (decomposability.ok) {
            if (userAknowledge(decomposability.message)) {
                val transformation: Transformation = transfomer.decompose(db)
                print(transformation.changeset)
                print(transformation.newdb)
                dbTrasformable11 = transformation.newdb
                trasformable11 = true
            }
        }
        return Pair(dbTrasformable11, trasformable11)
    }

    private fun userAknowledge(message: Any): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}