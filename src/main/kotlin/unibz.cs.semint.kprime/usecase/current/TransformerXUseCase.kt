package unibz.cs.semint.kprime.usecase.current

import unibz.cs.semint.kprime.domain.Applicability
import unibz.cs.semint.kprime.domain.Transformation
import unibz.cs.semint.kprime.domain.TransformationStrategy
import unibz.cs.semint.kprime.domain.ddl.Database
import unibz.cs.semint.kprime.usecase.TransformerUseCase
import unibz.cs.semint.kprime.usecase.common.ApplyChangeSetUseCase
import unibz.cs.semint.kprime.usecase.common.XPathTransformUseCase
import unibz.cs.semint.kprime.usecase.service.FileIOService
import unibz.cs.semint.kprime.usecase.service.IXMLSerializerService
import java.io.File
import java.io.FileInputStream
import java.io.FileReader
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

/**
 * Usage e.g.:
 *  TransformerXUseCase(
 *      XMLSerializerJacksonAdapter(),
 *      FileIOAdapter(),
 *      settingService.getWorkingDir() + TRACES_DIR +"/"
 *      settingService.getWorkingDir() + TRANSFORMERS_DIR +"/"+ transformerName +"/"+ transformerDocoTemplateFile
 *      settingService.getWorkingDir() + TRANSFORMERS_DIR +"/"+ transformerName +"/"+ transformerDocoXPathFile
 *      settingService.getWorkingDir() + TRANSFORMERS_DIR +"/"+ transformerName +"/"+ transformerCoTemplateFile
 *      settingService.getWorkingDir() + TRANSFORMERS_DIR +"/"+ transformerName +"/"+ transformerCoXPathFile
 *      transformerName)
 *
 */
class TransformerXUseCase(
        val serializer: IXMLSerializerService,
        val fileIOAdapter: FileIOService,
        val workingDir:String,
        val docoTemplateFilePath:String,
        val decoXPathsFilePath:String,
        val coTemplateFilePath:String,
        val coXPathsFilePath:String,
        val traceName:String,
        val name:String)
    : TransformerUseCase {
    private val xpathTransform = XPathTransformUseCase()


    override fun decompose(db: Database, params: Map<String, Any>): Transformation {

//        lateinit var traceDir :String
//        if (traceName.endsWith("/")) traceDir = traceName
//        else  traceDir = traceName + "/"

        val functionals = db.schema.functionals()
        if (functionals.isEmpty()) return errorTransformation(db,
                "ERROR: TransformerXUseCase no functionals")


        val dbFilePath = fileIOAdapter.writeOnWorkingFilePath(
                serializer.prettyDatabase(db), workingDir +  "db_worked.xml")
        println("Updated db file db_worked.xml")

        val xPaths = Properties()
        xPaths.load(FileReader(decoXPathsFilePath))
        val tranformerParmeters = mutableMapOf<String,Any>()
        tranformerParmeters.putAll(params)
        val changeSet = xpathTransform.compute(
                dbFilePath, docoTemplateFilePath,
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

    override fun compose(db: Database, params: Map<String, Any>): Transformation {
        TODO("Not yet implemented")
    }

    override fun decomposeApplicable(db: Database, transformationStrategy: TransformationStrategy): Applicability {
        val transformerParams = mutableMapOf<String,Any>()

        val xPathProperties = Properties()
        xPathProperties.load(FileInputStream(decoXPathsFilePath))

        if (db.name.isEmpty()) return Applicability(false,"db name empty", transformerParams)
//        lateinit var traceDir :String
//        if (traceName.endsWith("/")) traceDir = traceName
//        else  traceDir = traceName + "/"

        println("workingDir:"+workingDir)
//        println("traceDir:"+traceDir)
        println("db name:"+db.name)
        val dbFilePath = workingDir + db.name
        if (!File(dbFilePath).isFile) return Applicability(false,"db name ${dbFilePath} not exists", transformerParams)

        val (mutableMap, violation) = xpathTransform.getTemplateModel(dbFilePath, xPathProperties, transformerParams)
        return Applicability(violation.isEmpty(),"decomposeApplicable ${violation.isEmpty()} ${violation}", transformerParams)
    }

    override fun composeApplicable(db: Database, transformationStrategy: TransformationStrategy): Applicability {
        return Applicability(false,"composeApplicable", mutableMapOf())
    }
}