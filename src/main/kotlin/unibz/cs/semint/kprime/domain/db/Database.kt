package unibz.cs.semint.kprime.domain.db

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import unibz.cs.semint.kprime.domain.datasource.DataSource
import unibz.cs.semint.kprime.domain.Gid
import unibz.cs.semint.kprime.domain.dql.Mapping
import unibz.cs.semint.kprime.usecase.common.SQLizeSelectUseCase
import java.io.NotActiveException
import java.util.UUID
import kotlin.collections.ArrayList

@JacksonXmlRootElement(localName = "database")
@JsonInclude(JsonInclude.Include.NON_NULL)
open class Database () {

    @JacksonXmlProperty(isAttribute = true)
    var gid: Gid? = null

    @JacksonXmlProperty(isAttribute = true)
    var name: String =""

    @JacksonXmlProperty(isAttribute = true)
    var id: String = ""

    @JacksonXmlProperty(isAttribute = true)
    var author: String? = null

    @JacksonXmlProperty(isAttribute = true)
    var time: String? = null

    @JacksonXmlProperty(isAttribute = true)
    var parent: String? = null

    var schema: Schema = Schema()

    @JacksonXmlElementWrapper(localName = "mappings")
    @JacksonXmlProperty(localName = "query")
    var mappings : MutableList<Mapping>? = ArrayList()

    @JacksonXmlProperty(isAttribute = true)
    var source: String = ""

    @JacksonXmlElementWrapper(localName = "sources")
    var sources : MutableList<DataSource>? = null

    @JacksonXmlProperty(isAttribute = true)
    var vocabulary: String? = ""

    @JacksonXmlElementWrapper(localName = "vocabularies")
    var vocabularies : MutableList<DataSource>? = null

    init {
        this.mappings = mutableListOf()
//        this.sources = mutableListOf()
//        this.vocabularies = mutableListOf()
    }

    fun lineage(tableName:String) : List<String> {
        val result = mutableListOf<String>()
        var viewTable = tableName
        while (viewTable.isNotEmpty()) {
            result.add(viewTable)
            if (schema.table(viewTable)==null) viewTable = ""
            else viewTable = (schema.table(viewTable) as Table).view
        }
        return result
    }

    fun mappings():MutableList<Mapping> {
        if (mappings!=null) return mappings as MutableList<Mapping>
        return ArrayList()
    }

    fun mapping(name:String): Mapping? {
        return mappings().firstOrNull { m -> m.name == name }
    }

    fun mappingAsTable(name:String): Table? {
        val mapping = mapping(name) ?: return null
        val table = Table()
        table.name = mapping.name
        for(attribute in mapping.select.attributes.toSet()) {
            val col = Column()
            col.name = attribute.name
            table.columns.add(col)
        }
        return table
    }

    @JsonIgnore
    fun isEmpty(): Boolean {
        return schema.tables().size == 0
    }


    override fun toString():String  {
        var result = ""
        result += schema.tables?.joinToString(System.lineSeparator()) + System.lineSeparator()
        result += schema.constraints?.joinToString(System.lineSeparator())
        result += mappings().joinToString(System.lineSeparator())
        return result
    }

    fun withGid():Database {
        this.gid = UUID.randomUUID().toString()
        return this
    }

    fun tableGid(tableName:String):String {
        return this.gid+schema.table(tableName)?.id
    }

    fun mappingSql(mappingName:String):Result<String> {
        val rootMapping = mapping(mappingName) ?: return Result.failure(NotActiveException(mappingName))
        val rootSql = SQLizeSelectUseCase().sqlize(rootMapping)
        var resultSql = rootSql
        for (subMappingName in mappings().map { it.name }) {
            if (rootSql.contains("($subMappingName)")) {
                mapping(subMappingName) ?: return Result.failure(NotActiveException(subMappingName))
                mappingSql(subMappingName).onSuccess {
                    resultSql = resultSql.replace("SELECT ($subMappingName)", "($it)")
                    resultSql = resultSql.replace("($subMappingName)", "($it)  $subMappingName")
                }
            }
        }
        val mappingSql = SQLizeSelectUseCase().sqlize(rootMapping)
        resultSql = resultSql.replace("SELECT ($mappingName)", mappingSql)
        resultSql = resultSql.replace("($mappingName)", mappingSql)
        return Result.success(resultSql)
    }

}
