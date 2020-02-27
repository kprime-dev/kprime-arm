package unibz.cs.semint.kprime.usecase.current

import unibz.cs.semint.kprime.domain.Applicability
import unibz.cs.semint.kprime.domain.Transformation
import unibz.cs.semint.kprime.domain.TransformationStrategy
import unibz.cs.semint.kprime.domain.ddl.Database
import unibz.cs.semint.kprime.usecase.common.ApplyChangeSetUseCase
import unibz.cs.semint.kprime.usecase.TransformerUseCase
import unibz.cs.semint.kprime.usecase.common.XPathTransformUseCase
import unibz.cs.semint.kprime.usecase.service.FileIOService
import unibz.cs.semint.kprime.usecase.service.IXMLSerializerService
import java.io.StringWriter

class TransformerVUseCase(serializer: IXMLSerializerService, fileIOAdapter: FileIOService) : TransformerUseCase {
    val serializer = serializer
    val fileIOAdapter = fileIOAdapter

    override fun decompose(db: Database, params:Map<String,Any>): Transformation {
        val tranformerParmeters = mutableMapOf<String,Any>()
        tranformerParmeters["originTable"]=params["originTable"]!!
        tranformerParmeters["targetTable1"]=params["targetTable1"]!!
        tranformerParmeters["targetTable2"]=params["targetTable2"]!!
        val workingDir = params["workingDir"] as String
        val changeSet = XPathTransformUseCase().compute(fileIOAdapter.writeOnWorkingFilePath(serializer.prettyDatabase(db), workingDir+"db.xml"),
                "vertical",
                "decompose",
                "1",
                tranformerParmeters,
                StringWriter())
        return Transformation(changeSet, ApplyChangeSetUseCase(serializer).apply(db,changeSet))
    }

    override fun compose(db: Database, params: Map<String,Any>): Transformation {
        val changeSet = VJoinUseCase().compute(db)
        val newdb = ApplyChangeSetUseCase(serializer).apply(db, changeSet)
        return Transformation(changeSet, newdb)
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
        val tranformerParmeters = mutableMapOf<String,Any>()
        return Applicability(true,"TransformerVUseCase.decomposeApplicable", tranformerParmeters)
    }

    override fun composeApplicable(db: Database, transformationStrategy: TransformationStrategy): Applicability {
        // TODO("not implemented compose applicable logic.")
        val tranformerParmeters = mutableMapOf<String,Any>()
        return Applicability(true, "TransformerVUseCase.composeApplicable", tranformerParmeters)
    }

}