package unibz.cs.semint.kprime.domain.ddl

enum class DataType {
    int,timestamp,date,time,bigint,varchar,boolean,currency,unknown;

    companion object {
        fun fromString(value: String?): DataType {
            return when (value?.toLowerCase()) {
                "int" -> int
                "time" -> time
                "timestamp" -> timestamp
                "date" -> date
                "currency" -> currency
                "boolean" -> boolean
                "bigint" -> bigint
                "varchar" -> varchar
                else -> unknown
            }
        }
    }
}