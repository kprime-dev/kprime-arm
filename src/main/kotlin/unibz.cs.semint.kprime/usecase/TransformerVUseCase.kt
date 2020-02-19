package unibz.cs.semint.kprime.usecase

import unibz.cs.semint.kprime.adapter.file.FileIOAdapter
import unibz.cs.semint.kprime.domain.ddl.Database
import unibz.cs.semint.kprime.usecase.service.IXMLSerializerService
import java.io.OutputStreamWriter
import java.io.StringWriter

class TransformerVUseCase(serializer: IXMLSerializerService, fileIOAdapter: FileIOAdapter) :TransformerUseCase {
    val serializer = serializer
    val fileIOAdapter = fileIOAdapter

    override fun decompose(db: Database, vararg params:String): Database {
        val tranformerParmeters = mutableMapOf<String,Any>()
        tranformerParmeters["originTable"]=params[0]
        tranformerParmeters["targetTable1"]=params[1]
        tranformerParmeters["targetTable2"]=params[2]
        val changeSet = XPathTransformUseCase().compute(fileIOAdapter.workingFilePath(serializer.prettyDatabase(db)),
                "vertical",
                "decompose",
                "1",
                tranformerParmeters,
                StringWriter())
        return ApplyChangeSetUseCase(serializer).apply(db,changeSet)
    }

    override fun compose(db: Database, vararg params:String): Database {
        val changeSet = VJoinUseCase().compute(db)
        return ApplyChangeSetUseCase(serializer).apply(db,changeSet)
    }

}