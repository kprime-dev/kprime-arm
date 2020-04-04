package unibz.cs.semint.kprime.usecase.current

import unibz.cs.semint.kprime.domain.Applicability
import unibz.cs.semint.kprime.domain.Transformation
import unibz.cs.semint.kprime.domain.TransformationStrategy
import unibz.cs.semint.kprime.domain.ddl.Database
import unibz.cs.semint.kprime.usecase.common.ApplyChangeSetUseCase
import unibz.cs.semint.kprime.usecase.TransformerUseCase
import unibz.cs.semint.kprime.usecase.common.XPathTransformUseCase
import unibz.cs.semint.kprime.usecase.service.FileIOServiceI
import unibz.cs.semint.kprime.usecase.service.SerializerServiceI
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class TransformerVUseCase(val serializer: SerializerServiceI, val fileIOAdapter: FileIOServiceI) : TransformerUseCase {

    override fun decompose(db: Database, params:Map<String,Any>): Transformation {
        val tranformerParmeters = mutableMapOf<String,Any>()

        val functionals = db.schema.functionals()
        if (functionals.size==0) return errorTransformation(db,"ERROR: TransformerVUseCase no functionals")

        val tableToSplit = functionals.first().source.table
        tranformerParmeters["originTable"] = tableToSplit
        tranformerParmeters["targetTable1"] = tableToSplit +"_1"
        tranformerParmeters["targetTable2"] = tableToSplit +"_2"

        if (params["workingDir"]==null) return errorTransformation(db,"ERROR: TransformerVUseCase workingDir null")
        val workingDir = params["workingDir"] as String

        val timestamp = LocalDateTime.now()

        val dbFilePath = fileIOAdapter.writeOnWorkingFilePath(serializer.prettyDatabase(db), workingDir + "db_worked.xml")
        println("Updated db file db_worked.xml")

        val changeSet = XPathTransformUseCase().compute(dbFilePath,
                "vertical",
                "decompose",
                "1",
                tranformerParmeters)
        if (changeSet.size()!=0) {
            val csFileName = workingDir + "${timestamp.format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_nnnnnnnnnn"))}_cs.xml"
            println("Written cs file $tableToSplit : $csFileName ")
            fileIOAdapter.writeOnWorkingFilePath(serializer.prettyChangeSet(changeSet), csFileName)
        }

        val newdb = ApplyChangeSetUseCase(serializer).apply(db, changeSet)
        val newDbFileName = workingDir + "${timestamp.format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_nnnnnnnnnn"))}_db.xml"
        fileIOAdapter.writeOnWorkingFilePath(serializer.prettyDatabase(newdb), newDbFileName)
        println("Written db files $tableToSplit : $newDbFileName")

        return Transformation(changeSet, newdb, "TransformerVUseCase.decompose ($tableToSplit)")
    }

    override fun compose(db: Database, params: Map<String,Any>): Transformation {
        val changeSet = VJoinUseCase().compute(db)
        val newdb = ApplyChangeSetUseCase(serializer).apply(db, changeSet)
        return Transformation(changeSet, newdb, "TransformerVUseCase.compose")
    }

    override fun decomposeApplicable(db: Database, transformationStrategy: TransformationStrategy): Applicability {
        // TODO("not implemented decompose applicable logic.")
        // check if there is a functional dependency
        // transformationStrategy.askToProceed
        // then extract orginalTable
        // then extract targetTable1 transformationStrategy.askParameter
        // then extract targetTable2 transformationStrategy.askParameter
        // then extract workingDir fileIOAdapter
        // then extract workingFileName
        val applicability = db.schema.functionals().size > 0
        val tranformerParmeters = mutableMapOf<String,Any>()
        return Applicability(applicability,"TransformerVUseCase.decomposeApplicable", tranformerParmeters)
    }

    override fun composeApplicable(db: Database, transformationStrategy: TransformationStrategy): Applicability {
        // TODO("not implemented compose applicable logic.")
        val tranformerParmeters = mutableMapOf<String,Any>()
        return Applicability(true, "TransformerVUseCase.composeApplicable", tranformerParmeters)
    }

    override fun toString(): String {
        return "TransformerVUseCase"
    }
}