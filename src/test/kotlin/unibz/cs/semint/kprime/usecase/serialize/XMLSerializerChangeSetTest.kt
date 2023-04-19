package unibz.cs.semint.kprime.usecase.serialize

import junit.framework.Assert.assertEquals
import org.junit.Test
import unibz.cs.semint.kprime.adapter.service.XMLSerializerJacksonAdapter
import unibz.cs.semint.kprime.domain.dql.QueryTest
import unibz.cs.semint.kprime.domain.ddl.ChangeSet
import unibz.cs.semint.kprime.domain.ddl.CreateView
import unibz.cs.semint.kprime.domain.dql.Mapping
import unibz.cs.semint.kprime.usecase.common.XMLSerializeUseCase

class XMLSerializerChangeSetTest  {


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
        assertEquals("""
            UseCaseResult(message=done, ok=<changeSet id=""><createView path="" schemaName="" viewName="">select * from table</createView><createView path="" schemaName="" viewName="">select * from table</createView></changeSet>, ko=kotlin.Unit)
        """.trimIndent(),serializedChangeSet.toString())
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
        assertEquals("""
            UseCaseResult(message=done, ok=<changeSet id="createView-example" author="liquibase-docs">
              <createView path="A String" schemaName="public" viewName="v_person">select id, name from person where id > 10</createView>
            </changeSet>, ko=kotlin.Unit)
        """.trimIndent(),xmlSerializeUseCase.prettyChangeSet(changeSet.ok!!).toString())
    }

    @Test
    fun test_changeset_with_create_mapping() {
        // given
        val xmlSerializeUseCase = XMLSerializeUseCase(XMLSerializerJacksonAdapter())
        val changeSet = ChangeSet()
        val view = CreateView()
        view.text="select * from table"
        changeSet.createView.add(view)
        val mapping = Mapping.fromQuery(QueryTest().simpleQueryFixture("film"))
        mapping.name="film1"
        changeSet.createMapping.add(mapping)
        // when
        val changeSetXml = xmlSerializeUseCase.prettyChangeSet(changeSet)
        // then
        assertEquals("""
            <changeSet id="">
              <createView path="" schemaName="" viewName="">select * from table</createView>
              <createMapping id="" name="film1">
                <select>
                  <distinct>false</distinct>
                  <attributes>
                    <attribute name="Name"/>
                    <attribute name="Surname"/>
                  </attributes>
                  <from tableName="film" alias=""/>
                  <where condition="Name='Gigi'"/>
                </select>
                <union/>
                <minus/>
                <options/>
              </createMapping>
            </changeSet>
        """.trimIndent(),changeSetXml.ok)
    }
}