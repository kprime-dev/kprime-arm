package unibz.cs.semint.kprime.adapter.jackson

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.junit.Test
import unibz.cs.semint.kprime.domain.DataSource
import kotlin.test.assertEquals

class JacksonOptionalFields {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    class ToSer() {
        @JacksonXmlProperty(isAttribute = true)
        var vocabulary: String = ""
        @JacksonXmlProperty(isAttribute = true)
        var source: String = "sss"
        @JacksonXmlElementWrapper(localName = "vocabularies")
        var vocabularies : MutableList<DataSource>? = null
    }

    @Test
    fun test_serialize_one_optional_field_unused() {
        // given
        val mapper = jacksonObjectMapper()
        // when
        val toSerString = mapper.writeValueAsString(ToSer())
        // then
        assertEquals("{\"vocabulary\":\"\",\"source\":\"sss\"}",toSerString)
    }

    @Test
    fun test_deserialize_one_optional_field_unused() {
        // given
        val mapper = jacksonObjectMapper()
//        val json = "{\"vocabulary\":\"aaa\",\"vocabularies\":null}"
        val json = "{\"vocabulary\":\"aaa\"}"
        // when
        val toSerString = mapper.readValue<ToSer>(json)
        // then
        assertEquals("aaa",toSerString.vocabulary)
        assertEquals("sss",toSerString.source)
    }

}