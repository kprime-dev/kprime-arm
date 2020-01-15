package unibz.cs.semint.kprime.adapter.service

import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import unibz.cs.semint.kprime.domain.*
import unibz.cs.semint.kprime.usecase.service.IXMLSerializerService

class XMLSerializerJacksonAdapter : IXMLSerializerService {


    // table

    override fun serializeTable(table: Table): String {
        //val mapper = XmlMapper()
        val mapper = XmlMapper().registerModule(KotlinModule())
        return mapper.writeValueAsString(table)
    }

    override fun deserializeTable(s: String): Table {
        val mapper = XmlMapper()
        return mapper.readValue(s,Table::class.java)
    }

    override fun prettyTable(table: Table): String {
        val mapper = XmlMapper().registerModule(KotlinModule())
        val writer = mapper.writerWithDefaultPrettyPrinter()
        return writer.writeValueAsString(table)
    }

    // database

    override fun serializeDatabase(database: Database): String {
        val mapper = XmlMapper().registerModule(KotlinModule())
        return mapper.writeValueAsString(database)
    }

    override fun deserializeDatabase(s: String): Database {
        val mapper = XmlMapper()
        return mapper.readValue(s,Database::class.java)
    }

    override fun prettyDatabase(db: Database): String {
        val mapper = XmlMapper().registerModule(KotlinModule())
        val writer = mapper.writerWithDefaultPrettyPrinter()
        return writer.writeValueAsString(db)
    }

    // constraint

    override fun serializeConstraint(constraint: Constraint): String {
        val mapper = XmlMapper().registerModule(KotlinModule())
        return mapper.writeValueAsString(constraint)
    }

    override fun deserializeConstraint(s: String): Constraint {
        val mapper = XmlMapper()
        return mapper.readValue(s,Constraint::class.java)
    }
}