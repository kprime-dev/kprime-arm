package unibz.cs.semint.kprime.usecase.xstl

import org.junit.Test
import unibz.cs.semint.kprime.adapter.XSLTransformerJaxpAdapter
import unibz.cs.semint.kprime.usecase.XMLSerializeUseCase
import unibz.cs.semint.kprime.usecase.XSLTrasformUseCase

class XSTLTransformTest {

    @Test
    fun test_xstl() {
        // given
        val transformer = XSLTrasformUseCase(XSLTransformerJaxpAdapter())
        //val path = XSTLTransformTest::class.java.getResource("xslt/stylesheet.xsl").path
        //println(path)
        // when
        transformer.transform(
            "target/test-classes/xslt/stylesheet.xsl",
            "target/test-classes/xslt/report.xml",
            "target/test-classes/xslt/report.html"
        )
        // then
        //  check output file
    }
}