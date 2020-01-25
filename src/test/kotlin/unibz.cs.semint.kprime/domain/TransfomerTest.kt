package unibz.cs.semint.kprime.domain

import org.junit.Test
import unibz.cs.semint.kprime.adapter.service.XMLSerializerJacksonAdapter
import unibz.cs.semint.kprime.usecase.XMLSerializeUseCase

class TransfomerTest {

    @Test
    fun test_empty_trasfomer_xml(){

        // given
        val transformer = Transformer()
        // when
        val serializedTransformer = XMLSerializeUseCase(XMLSerializerJacksonAdapter()).serializeTransformer(transformer)
        // then
        println(serializedTransformer)

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
        println(serializedTransformer)

    }

}