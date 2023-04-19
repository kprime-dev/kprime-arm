package unibz.cs.semint.kprime.domain.dql

class Mapping : Query() {
    private val aliases = HashMap<String,String>()

    fun queryAliased():Query {
        return this
    }

    companion object {
        fun fromQuery(query:Query):Mapping {
            val mapping = Mapping()
            mapping.name = query.name
            mapping.select = query.select
            mapping.union = query.union
            mapping.minus = query.minus
            mapping.options = query.options
            return mapping
        }
    }

}