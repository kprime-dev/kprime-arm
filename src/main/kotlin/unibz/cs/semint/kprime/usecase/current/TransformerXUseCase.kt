package unibz.cs.semint.kprime.usecase.current

import unibz.cs.semint.kprime.domain.dtl.Applicability
import unibz.cs.semint.kprime.domain.dtl.Transformation
import unibz.cs.semint.kprime.domain.dtl.TransformationStrategy
import unibz.cs.semint.kprime.domain.db.Database
import unibz.cs.semint.kprime.usecase.TransformerUseCase
import unibz.cs.semint.kprime.usecase.common.XPathTransformUseCase
import unibz.cs.semint.kprime.usecase.service.FileIOServiceI
import unibz.cs.semint.kprime.usecase.service.SerializerServiceI
import java.io.File

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
    private val decomposer = TransformerXDecompose(
            serializer,
            fileIOAdapter,
            workingDir,
            docoTemplateFilePath,
            decoXPathsFilePath,
            xpathTransform
    )
    private val composer = TransformerXCompose(
            serializer,
            fileIOAdapter,
            workingDir,
            docoTemplateFilePath,
            decoXPathsFilePath,
            xpathTransform
    )


    override fun decompose(db: Database, params: Map<String, Any>): Transformation {
        return decomposer.decompose(db,params)
    }

    override fun compose(db: Database, params: Map<String, Any>): Transformation {
        return composer.compose(db,params,::checkRequiredParams)
    }

    override fun decomposeApplicable(db: Database, transformationStrategy: TransformationStrategy, transformerParams: Map<String,Any>): Applicability {

        var xPathProperties = File(decoXPathsFilePath).readLines()

        if (db.name.isEmpty()) return Applicability(false,"db name empty", transformerParams)
        val dbFilePath = workingDir + db.name + ".xml"
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

    fun checkRequiredParams(xPathProperties: List<String>, transformerParams: Map<String, Any>): Pair<MutableList<String>, List<String>> {
        var xPathProperties1 = xPathProperties
        val failedCheckRequiredParams = mutableListOf<String>()
        if (xPathProperties1.size > 0 && xPathProperties1[0].startsWith("((")) {
            // check required params
            val requireds = xPathProperties1[0].split(",")
            for (required in requireds) {
                val para = required.replace("((","").replace("))","")
                //println("££$para££")
                if (transformerParams[para] == null || transformerParams[para] == "") {
                    failedCheckRequiredParams.add(para)
                }
            }
            xPathProperties1 = xPathProperties1.drop(1)
            //println("xPathProperties1:::::::::::::::::::::::>>>>>>>>"+xPathProperties1)
        }
        return Pair(failedCheckRequiredParams, xPathProperties1)
    }

    override fun composeApplicable(db: Database, transformationStrategy: TransformationStrategy, transformerParams: Map<String,Any>): Applicability {

        var xPathProperties = File(coXPathsFilePath).readLines()

        if (db.name.isEmpty()) return Applicability(false,"db name empty", transformerParams)
        val dbFilePath = workingDir + db.name + ".xml"
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