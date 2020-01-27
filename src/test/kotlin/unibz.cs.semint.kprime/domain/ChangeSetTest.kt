package unibz.cs.semint.kprime.domain

import org.junit.Test
import unibz.cs.semint.kprime.adapter.service.XMLSerializerJacksonAdapter
import unibz.cs.semint.kprime.domain.dml.ChangeSet
import unibz.cs.semint.kprime.domain.dml.CreateView
import unibz.cs.semint.kprime.usecase.XMLSerializeUseCase

class ChangeSetTest  {


    @Test
    fun test_serialize_changeset_xml() {
        // given
        val changeSet = ChangeSet()
        val view = CreateView()
        view.text="select * from table"
        changeSet.createView.add(view)
        changeSet.createView.add(view)
        // when
        val serializedChangeSet = XMLSerializeUseCase(XMLSerializerJacksonAdapter()).serializeChangeSet(changeSet)
        // then
        println(serializedChangeSet)
    }

    @Test
    fun test_parse_changeset_xml() {
       // given
        val changesetXml = """
            <changeSet author="liquibase-docs" id="createView-example">
                <createView catalogName="cat"
                        encoding="UTF-8"
                        fullDefinition="true"
                        path="A String"
                        relativeToChangelogFile="true"
                        remarks="A String"
                        replaceIfExists="false"
                        schemaName="public"
                        viewName="v_person">select id, name from person where id > 10</createView>
            </changeSet>
        """.trimIndent()
        // when
        val xmlSerializeUseCase = XMLSerializeUseCase(XMLSerializerJacksonAdapter())
        val changeSet = xmlSerializeUseCase.deserializeChangeSet(changesetXml)
        // then
        println(xmlSerializeUseCase.prettyChangeSet(changeSet.ok!!))
    }
}