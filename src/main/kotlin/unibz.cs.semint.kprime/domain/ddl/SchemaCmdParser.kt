package unibz.cs.semint.kprime.domain.ddl

object SchemaCmdParser {

    fun parseTable(commandArgs: String): Table {
        val tableName: String = commandArgs.split(":")[0]
        val attributes = commandArgs.split(":")[1].split(",")
        val table = Table()
        table.name = tableName
        for (att in attributes) table withColumn att
        return table
    }

    fun parseFunctionals(tableName: String,setExpression: String): Set<Constraint> {
        return parseConstraint(setExpression, tableName, Constraint.TYPE.FUNCTIONAL.name)
    }

    fun parseMultivalued(tableName: String,setExpression: String): Set<Constraint> {
        return parseConstraint(setExpression, tableName, Constraint.TYPE.MULTIVALUED.name)
    }

    fun parseInclusion(tableName: String,setExpression: String): Set<Constraint> {
        return parseConstraint(setExpression, tableName, Constraint.TYPE.INCLUSION.name)
    }

    fun parseDoubleInclusion(tableName: String,setExpression: String): Set<Constraint> {
        return parseConstraint(setExpression, tableName, Constraint.TYPE.DOUBLE_INCLUSION.name)
    }

    private fun parseConstraint(setExpression: String, tableName: String, type:String): Set<Constraint> {
        val constraintsToAdd = Constraint.set(setExpression)
        for (constraint in constraintsToAdd) {
            constraint.name = "$tableName.$type"
            constraint.type = type
            constraint.source.table = tableName
            constraint.target.table = tableName
        }
        return constraintsToAdd
    }


}