package unibz.cs.semint.kprime.usecase.current

import unibz.cs.semint.kprime.adapter.file.FileIOAdapter
import unibz.cs.semint.kprime.domain.Applicability
import unibz.cs.semint.kprime.domain.Transformation
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

    override fun decompose(db: Database, vararg params:String): Transformation {
        val tranformerParmeters = mutableMapOf<String,Any>()
        tranformerParmeters["originTable"]=params[0]
        tranformerParmeters["targetTable1"]=params[1]
        tranformerParmeters["targetTable2"]=params[2]
        val workingDir = params[3]
        val changeSet = XPathTransformUseCase().compute(fileIOAdapter.writeOnWorkingFilePath(serializer.prettyDatabase(db), workingDir+"db.xml"),
                "vertical",
                "decompose",
                "1",
                tranformerParmeters,
                StringWriter())
        return Transformation(changeSet, ApplyChangeSetUseCase(serializer).apply(db,changeSet))
    }

    override fun compose(db: Database, vararg params:String): Transformation {
        val changeSet = VJoinUseCase().compute(db)
        val newdb = ApplyChangeSetUseCase(serializer).apply(db, changeSet)
        return Transformation(changeSet, newdb)
    }

    override fun decomposeApplicable(): Applicability {
        // TODO("not implemented decompose applicable logic.")
        return Applicability(true,"TransformerVUseCase.decomposeApplicable")
    }

    override fun composeApplicable(): Applicability {
        // TODO("not implemented compose applicable logic.")
        return Applicability(true,"TransformerVUseCase.composeApplicable")
    }

}