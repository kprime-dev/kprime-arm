package unibz.cs.semint.kprime.usecase.current

import unibz.cs.semint.kprime.domain.Transformation
import unibz.cs.semint.kprime.domain.ddl.Database
import unibz.cs.semint.kprime.usecase.common.ApplyChangeSetUseCase
import unibz.cs.semint.kprime.usecase.common.XPathTransformUseCase
import unibz.cs.semint.kprime.usecase.service.FileIOServiceI
import unibz.cs.semint.kprime.usecase.service.SerializerServiceI
import java.io.File
import java.lang.IllegalArgumentException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class TransformerXCompose (
            val serializer: SerializerServiceI,
            val fileIOAdapter: FileIOServiceI,
            val workingDir:String,
            val coTemplateFilePath:String,
            val coXPathsFilePath:String,
            val xpathTransform : XPathTransformUseCase
            ) {

    fun compose(db: Database,
                params: Map<String, Any>,
                checkRequiredParams : (xPathProperties: List<String>, transformerParams: Map<String, Any>)-> Pair<MutableList<String>, List<String>>
                ): Transformation {
        val dbFilePath = fileIOAdapter.writeOnWorkingFilePath(
                serializer.prettyDatabase(db), workingDir +  "db_worked.xml")
//        println("Updated db file db_worked.xml")

        var xPaths = File(coXPathsFilePath).readLines()
        val tranformerParmeters = mutableMapOf<String,Any>()
        tranformerParmeters.putAll(params)

        // check required parameters
        val failuresOrXPathProperties = checkRequiredParams(xPaths, tranformerParmeters)
        val failedCheckRequiredParams = failuresOrXPathProperties.first
        if (failedCheckRequiredParams.isNotEmpty())
            throw IllegalArgumentException("")
        xPaths = failuresOrXPathProperties.second

        val changeSet = xpathTransform.compute(
                dbFilePath, coTemplateFilePath,
                xPaths, tranformerParmeters)
        println("Computed changeSet : $changeSet")
        val changeSetFileName = "${LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_nnnnnnnnnn"))}_cs.xml"
        changeSet.id=changeSetFileName
        val changeSetFilePath = workingDir + changeSetFileName
        fileIOAdapter.writeOnWorkingFilePath(serializer.prettyChangeSet(changeSet), changeSetFilePath)

        val newdb = ApplyChangeSetUseCase(serializer).apply(db, changeSet)
        val dbFileName = "${LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_nnnnnnnnnn"))}_db.xml"
        newdb.name=dbFileName
        val newDbFilePath = workingDir + dbFileName
        fileIOAdapter.writeOnWorkingFilePath(serializer.prettyDatabase(newdb), newDbFilePath)
        println("Written new db file : $newDbFilePath")

        return Transformation(changeSet, newdb, "TransformerXUseCase.decomposed ")
    }

}