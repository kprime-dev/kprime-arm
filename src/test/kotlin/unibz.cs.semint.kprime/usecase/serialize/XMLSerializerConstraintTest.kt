package unibz.cs.semint.kprime.usecase.serialize

import org.junit.Assert
import org.junit.Test
import org.xmlunit.builder.DiffBuilder
import unibz.cs.semint.kprime.adapter.service.XMLSerializerJacksonAdapter
import unibz.cs.semint.kprime.domain.ddl.Column
import unibz.cs.semint.kprime.domain.ddl.Constraint
import unibz.cs.semint.kprime.domain.ddl.Source
import unibz.cs.semint.kprime.usecase.common.XMLSerializeUseCase
import java.io.File

class XMLSerializerConstraintTest {

    @Test
    fun test_serialize_constraint_with_empty_source_and_target_columns() {
        // given
        val serializer = XMLSerializeUseCase(XMLSerializerJacksonAdapter())
        val constraint = Constraint()
        constraint.id="idconst1"
        constraint.source= Source()
        // when
        val serializedConstraint = serializer.serializeConstraint(constraint).ok
        // then
        val fileContent = File("target/test-classes/constraint_with_with_empty_source_and_target_columns.xml")
            .readLines().joinToString(System.lineSeparator())

        val myDiff = DiffBuilder.compare(serializedConstraint)
            .ignoreWhitespace()
            .withTest(fileContent)
            .checkForSimilar().build()
        Assert.assertFalse(myDiff.toString(), myDiff.hasDifferences());
        //Assert.assertEquals("",serializedConstraint)
    }

    @Test
    fun test_serialize_constraint_with_two_source_columns_three_target_columns() {
        // given
        val serializer = XMLSerializeUseCase(XMLSerializerJacksonAdapter())
        val constraint = Constraint()
        constraint.id="idconst1"
        constraint.source= Source()
        constraint.source.columns.add(Column())
        constraint.source.columns.add(Column())
        constraint.target.columns.add(Column())
        constraint.target.columns.add(Column())
        constraint.target.columns.add(Column())
        // when
        val serializedConstraint = serializer.serializeConstraint(constraint).ok
        // then
        val fileContent = File("target/test-classes/constraint_with_two_source_columns_three_target_columns.xml")
            .readLines().joinToString(System.lineSeparator())

        val myDiff = DiffBuilder.compare(serializedConstraint)
            .ignoreWhitespace()
            .withTest(fileContent)
            .checkForSimilar().build()
        Assert.assertFalse(myDiff.toString(), myDiff.hasDifferences());
        //assertEquals("",serializedConstraint)
    }

    @Test
    fun test_serialize_functional_dependency_constraint() {
        // given
        val serializer = XMLSerializeUseCase(XMLSerializerJacksonAdapter())
        val constraint = Constraint()
        constraint.id="idconst1"
        constraint.type= Constraint.TYPE.FOREIGN_KEY.name
        constraint.source.columns.add(Column())
        constraint.target.columns.add(Column())
        // when
        val serializedConstraint = serializer.serializeConstraint(constraint).ok
        // then
        val fileContent = File("target/test-classes/constraint_foreign_key.xml")
            .readLines().joinToString(System.lineSeparator())

        val myDiff = DiffBuilder.compare(serializedConstraint)
            .ignoreWhitespace()
            .withTest(fileContent)
            .checkForSimilar().build()
        Assert.assertFalse(myDiff.toString(), myDiff.hasDifferences());
        //assertEquals("",serializedConstraint)
    }

    @Test
    fun test_serialize_primary_key_constraint() {
        // given
        val serializer = XMLSerializeUseCase(XMLSerializerJacksonAdapter())
        val constraint = Constraint()
        constraint.id="idconst1"
        constraint.type= Constraint.TYPE.PRIMARY_KEY.name
        constraint.source.columns.add(Column())
        constraint.source.columns.add(Column())
        constraint.source.columns.add(Column())
        // when
        val serializedConstraint = serializer.serializeConstraint(constraint).ok

        // then
        val fileContent = File("target/test-classes/constraint_primary_key.xml")
            .readLines().joinToString(System.lineSeparator())

        val myDiff = DiffBuilder.compare(serializedConstraint)
            .ignoreWhitespace()
            .withTest(fileContent)
            .checkForSimilar().build()
        Assert.assertFalse(myDiff.toString(), myDiff.hasDifferences());
        //assertEquals("",serializedConstraint)

    }
}