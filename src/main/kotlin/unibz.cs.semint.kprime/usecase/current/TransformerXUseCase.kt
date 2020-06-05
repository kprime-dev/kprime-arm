package unibz.cs.semint.kprime.usecase.current

import unibz.cs.semint.kprime.domain.Applicability
import unibz.cs.semint.kprime.domain.Transformation
import unibz.cs.semint.kprime.domain.TransformationStrategy
import unibz.cs.semint.kprime.domain.ddl.Database
import unibz.cs.semint.kprime.usecase.TransformerUseCase
import unibz.cs.semint.kprime.usecase.common.ApplyChangeSetUseCase
import unibz.cs.semint.kprime.usecase.common.XPathTransformUseCase
import unibz.cs.semint.kprime.usecase.service.FileIOServiceI
import unibz.cs.semint.kprime.usecase.service.SerializerServiceI
import java.io.File
import java.lang.IllegalArgumentException
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
        val serializer: SerializerServiceI,
        val fileIOAdapter: FileIOServiceI,
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

//        val functionals = db.schema.functionals()
//        if (functionals.isEmpty()) return errorTransformation(db,
//                "ERROR: TransformerXUseCase no functionals")


        val dbFilePath = fileIOAdapter.writeOnWorkingFilePath(
                serializer.prettyDatabase(db), workingDir +  "db_worked.xml")
        println("Updated db file db_worked.xml")

        val xPaths = File(decoXPathsFilePath).readLines()
        val tranformerParmeters = mutableMapOf<String,Any>()
        tranformerParmeters.putAll(params)

        // check requred parameters

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
        val dbFilePath = fileIOAdapter.writeOnWorkingFilePath(
                serializer.prettyDatabase(db), workingDir +  "db_worked.xml")
        println("Updated db file db_worked.xml")

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

    override fun decomposeApplicable(db: Database, transformationStrategy: TransformationStrategy, transformerParams: Map<String,Any>): Applicability {

        var xPathProperties = File(decoXPathsFilePath).readLines()

        if (db.name.isEmpty()) return Applicability(false,"db name empty", transformerParams)
        val dbFilePath = workingDir + db.name
        if (!File(dbFilePath).isFile) return Applicability(false,"db name ${dbFilePath} not exists", transformerParams)

        val failuresOrXPathProperties = checkRequiredParams(xPathProperties, transformerParams)
        val failedCheckRequiredParams = failuresOrXPathProperties.first
        if (failedCheckRequiredParams.isNotEmpty())
            return Applicability(false, "required ${failedCheckRequiredParams} parameter(s).", transformerParams)
        xPathProperties = failuresOrXPathProperties.second


        //println("decomposeApplicable 1:")
        var message = ""
        var applicable = false
        var mutableMap = mutableMapOf<String,Any>()
        try {
            //println("decomposeApplicable 2:")
            val (templateMap, violation) = xpathTransform.getTemplateModel(dbFilePath, xPathProperties, transformerParams.toMutableMap())
            if (xPathProperties.isEmpty()) return Applicability(false,"Empty rules.", mutableMap)
            //println("decomposeApplicable 3:")
            applicable = violation.isEmpty()
            message = "decomposeApplicable ${violation.isEmpty()} ${violation}"
            mutableMap = templateMap as MutableMap<String, Any>
        } catch (e:Exception) {
            message = e.message.toString()
            e.printStackTrace()
        }
        return Applicability(applicable, message, mutableMap)
    }

    private fun checkRequiredParams(xPathProperties: List<String>, transformerParams: Map<String, Any>): Pair<MutableList<String>, List<String>> {
        var xPathProperties1 = xPathProperties
        val failedCheckRequiredParams = mutableListOf<String>()
        if (xPathProperties1.size > 0 && xPathProperties1[0].startsWith("((")) {
            // check required params
            val requireds = xPathProperties1[0].split(",")
            for (required in requireds) {
                if (transformerParams[required] == null || transformerParams[required] == "") {
                    failedCheckRequiredParams.add(required)
                }
            }
            xPathProperties1 = xPathProperties1.drop(1)
        }
        return Pair(failedCheckRequiredParams, xPathProperties1)
    }

    override fun composeApplicable(db: Database, transformationStrategy: TransformationStrategy, transformerParams: Map<String,Any>): Applicability {

        var xPathProperties = File(coXPathsFilePath).readLines()

        if (db.name.isEmpty()) return Applicability(false,"db name empty", transformerParams)
        val dbFilePath = workingDir + db.name
        if (!File(dbFilePath).isFile) return Applicability(false,"db name ${dbFilePath} not exists", transformerParams)


        if (xPathProperties.size>0 && xPathProperties[0].startsWith("((")) {
            // check required params
            val requireds = xPathProperties[0].split(",")
            for (required in requireds) {
                if (transformerParams[required] == null || transformerParams[required] == "") {
                    return Applicability(false, "required ${required} parameter not exists.", transformerParams)
                }
            }
           xPathProperties = xPathProperties.drop(1)
        }

        //println("composeApplicable 1:")
        var message = ""
        var applicable = false
        var mutableMap = mutableMapOf<String,Any>()
        try {
            //println("composeApplicable 2:")
            val (templateMap, violation) = xpathTransform.getTemplateModel(dbFilePath, xPathProperties, transformerParams.toMutableMap())
            if (xPathProperties.isEmpty()) return Applicability(false,"Empty rules.", mutableMap)
            //println("composeApplicable 3:")
            applicable = violation.isEmpty()
            message = "composeApplicable ${violation.isEmpty()} ${violation}"
            mutableMap = templateMap as MutableMap<String, Any>
        } catch (e:Exception) {
            message = e.message.toString()
            e.printStackTrace()
        }
        return Applicability(applicable, message, mutableMap)
    }
}