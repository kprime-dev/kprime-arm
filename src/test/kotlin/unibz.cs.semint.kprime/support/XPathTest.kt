package unibz.cs.semint.kprime.support

import org.junit.Test
import org.w3c.dom.NodeList
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathFactory


class XPathTest {

    @Test
    fun test_xpath_query() {
        //given
        val xml = "<changeSet id=\"234\">Value</changeSet>"
        val docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()        //when
        val doc = docBuilder.parse(xml.byteInputStream())
        val xpath = XPathFactory.newInstance().newXPath()
        //when
        val result = xpath.compile("changeSet").evaluate(doc, XPathConstants.NODESET) as NodeList
        //then
        println(result.length)
        val resultMessage= asValueList(result).joinToString(",")
        println(resultMessage)
    }

    @Test
    fun test_xpath_query2() {
        //given
        val xml = "<xml><changeSet id=\"234\">Value</changeSet>"+"<changeSet id=\"234\">Value</changeSet></xml>"
        val docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()        //when
        val doc = docBuilder.parse(xml.byteInputStream())
        val xpath = XPathFactory.newInstance().newXPath()
        //when
        val result = xpath.compile("xml/changeSet").evaluate(doc, XPathConstants.NODESET) as NodeList
        //then
        println(result.length)
        val resultMessage= asValueList(result).joinToString(",")
        println(resultMessage)
    }

    private fun asValueList(xpathResultNodes: NodeList): MutableList<String> {
        val listNodeValues = mutableListOf<String>()
        for (nodeId in 0..xpathResultNodes.length) {
            val item = xpathResultNodes.item(nodeId)
            if (item==null) continue
            listNodeValues.add(item.nodeName)
            if (item.nodeValue==null) continue
            listNodeValues.add(item.nodeValue)
        }
        return listNodeValues
    }

}
