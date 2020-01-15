package unibz.cs.semint.kprime.adapter.service

import unibz.cs.semint.kprime.usecase.service.IXSLTransformerService
import java.io.FileOutputStream
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerFactory
import javax.xml.transform.stream.StreamResult
import javax.xml.transform.stream.StreamSource

class XSLTransformerJaxpAdapter:IXSLTransformerService {
    override fun trasform(xsl: String, xml: String, out: String) {
        val tFactory = TransformerFactory.newInstance()
        val fileOutputStream = FileOutputStream(out)
        val transformer = tFactory.newTransformer(StreamSource(xsl))
        transformer.setOutputProperty(OutputKeys.INDENT, "yes")
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4")
        transformer.transform(StreamSource(xml), StreamResult(fileOutputStream))
    }

}