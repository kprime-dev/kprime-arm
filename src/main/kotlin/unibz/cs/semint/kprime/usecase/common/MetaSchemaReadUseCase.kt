package unibz.cs.semint.kprime.usecase.common

import unibz.cs.semint.kprime.domain.*
import unibz.cs.semint.kprime.domain.ddl.Database
import unibz.cs.semint.kprime.usecase.UseCaseResult
import unibz.cs.semint.kprime.usecase.repository.IMetaSchemaRepository
import unibz.cs.semint.kprime.usecase.service.SerializerServiceI
import java.util.*


class MetaSchemaReadUseCase() {

    companion object {
        fun name(): String {
            return "read-meta-schema"
        }
    }

    fun usage(): String {
        return "${name()} SOURCE_NAME TABLE_NAME"
    }

    fun oneline(): String {
        return "Reads meta informations about a jdbc database or specific table."
    }

    fun description(): String {
        return """
            Reads metadata informations about SOURCE_NAME . 
            example syntax:
              ${name()} sakila-source
        """.trimIndent()
    }

    fun doit(datasource: DataSource,line: String, metaSchemaRepository: IMetaSchemaRepository, xmlSerializer:SerializerServiceI) : UseCaseResult<Database> {
        val tokens = tokenize(line)
        if (tokens.size<2) { return UseCaseResult("Usage:" + usage(), null);}
        if (tokens[1] == "?") { return UseCaseResult(oneline(), null);}
        if (tokens[1] == "??") { return UseCaseResult(description(), null);}
        lateinit var db : Database
        if (tokens[0] == name()) {
            val sourceName = tokens[1]
            var table = ""
            if (tokens.size==3)
                table = tokens[2]
            db = metaSchemaRepository.metaDatabase(datasource)
            db.source = datasource.name
            //print(xmlSerializer.prettyDatabase(db))
        }
        return UseCaseResult("${name()} done.", db)
    }

    fun tokenize(line: String): List<String> {
        val tokenizer = StringTokenizer(line)
        val tokens = ArrayList<String>()
        while (tokenizer.hasMoreElements()) {
            tokens.add(tokenizer.nextToken())
        }
        return tokens
    }

}