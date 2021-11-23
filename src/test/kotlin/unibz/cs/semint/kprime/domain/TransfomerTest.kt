package unibz.cs.semint.kprime.domain

import junit.framework.Assert.assertEquals
import org.junit.Test
import unibz.cs.semint.kprime.adapter.service.XMLSerializerJacksonAdapter
import unibz.cs.semint.kprime.usecase.common.XMLSerializeUseCase

class TransfomerTest {

    @Test
    fun test_empty_trasfomer_xml(){

        // given
        val transformer = Transformer()
        // when
        val serializedTransformer = XMLSerializeUseCase(XMLSerializerJacksonAdapter()).serializeTransformer(transformer)
        // then
        assertEquals("""
            UseCaseResult(message=done, ok=<transformer name="">
              <composer>
                <xman>
                  <xrules/>
                </xman>
                <template filename=""/>
              </composer>
              <splitter>
                <xman>
                  <xrules/>
                </xman>
                <template filename=""/>
              </splitter>
            </transformer>
            , ko=kotlin.Unit)
        """.trimIndent(),serializedTransformer.toString())

    }


    @Test
    fun test_vsplit_trasfomer_xml(){

        // given
        val transformer = Transformer()
        val xrule = Xrule()
        xrule.name="all"
        xrule.rule="/database/schema/tables/tables[@name='%%table%%']/columns/columns/@name = 4"
        transformer.splitter.xman.xrules.add(xrule)
        // when
        val serializedTransformer = XMLSerializeUseCase(XMLSerializerJacksonAdapter()).serializeTransformer(transformer)
        // then
        assertEquals("""
            UseCaseResult(message=done, ok=<transformer name="">
              <composer>
                <xman>
                  <xrules/>
                </xman>
                <template filename=""/>
              </composer>
              <splitter>
                <xman>
                  <xrules>
                    <xrules name="all">/database/schema/tables/tables[@name='%%table%%']/columns/columns/@name = 4</xrules>
                  </xrules>
                </xman>
                <template filename=""/>
              </splitter>
            </transformer>
            , ko=kotlin.Unit)
        """.trimIndent(),serializedTransformer.toString())

    }

}