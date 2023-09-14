package unibz.cs.semint.kprime.domain.db

object SchemaCmdParser {

    // table:a,b,c
    // table(pk/nk):a,b,c
    fun parseTable(commandArgs: String): Table {
        val table = parseTableName(commandArgs.substringBefore(":"))
        val attributes = commandArgs.substringAfter(":").split(",")
        for (att in attributes) table withColumn att
        return table
    }

    internal fun parseTableName(arg0: String): Table {
        val table = Table()
        if (!arg0.contains("(")) {
            table.name = arg0
        } else {
            table.name = arg0.substringBefore("(")
            val key = arg0.substringAfter("(").substringBefore(")")
            table.primaryKey = key.substringBefore("/")
            table.naturalKey = key.substringAfter("/")
        }
        return table
    }

    // table:a-->b,c
    fun parseFunctionals(index: Int, tableName: String,setExpression: String): Set<Constraint> {
        return parseConstraint(index, setExpression, tableName, Constraint.TYPE.FUNCTIONAL.name)
    }

    // table:a->>b,c
    fun parseMultivalued(index: Int, tableName: String, arrowExpressions: String): Set<Constraint> {
        return parseConstraint(index, arrowExpressions, tableName, Constraint.TYPE.MULTIVALUED.name)
    }

    // arrowWxpressions = "a,b-->c,d;x-->y"
    private fun parseConstraint(index: Int, arrowExpressions: String, tableName: String, type:String): Set<Constraint> {
        val constraintsToAdd = Constraint.set(arrowExpressions)
        var indexToAdd = index
        for (constraint in constraintsToAdd) {
            constraint.name = "$tableName.$type$indexToAdd"
            constraint.type = type
            constraint.source.table = tableName
            constraint.target.table = tableName
            indexToAdd++
        }
        return constraintsToAdd
    }

    // table1:a-->table2:b
    fun parseForeignKey(index: Int, constraintExpression: String): Constraint {
        val split = constraintExpression.split("-->")
        val tableSourceString = split[0]
        val tableTargetString = split[1]
        val tableSource = parseTable(tableSourceString)
        val tableTarget = parseTable(tableTargetString)
        val type = Constraint.TYPE.FOREIGN_KEY.name
        return buildConstraint(tableSource, tableTarget, type, index)
    }

    // table1:a-->table2:b
    fun parseInclusion(index: Int, constraintExpression: String): Constraint {
        val split = constraintExpression.split("-->")
        val tableSourceString = split[0]
        val tableTargetString = split[1]
        val tableSource = parseTable(tableSourceString)
        val tableTarget = parseTable(tableTargetString)
        val type = Constraint.TYPE.INCLUSION.name
        return buildConstraint(tableSource, tableTarget, type, index)
    }

    // table1:a<->table2:b
    fun parseDoubleInclusion(index: Int, constraintExpression: String): Constraint {
        val split = constraintExpression.split("<->")
        val tableSourceString = split[0]
        val tableTargetString = split[1]
        val tableSource = parseTable(tableSourceString)
        val tableTarget = parseTable(tableTargetString)
        val type = Constraint.TYPE.DOUBLE_INCLUSION.name
        return buildConstraint(tableSource, tableTarget, type, index)
    }

    data class ConstraintTokens(val sourceName:String,
                                val sourceAttrs:String,
                                val targetName:String,
                                val targetAttrs:String
        )

    fun parseExternalConstraintArgs(commandArgs:String):ConstraintTokens {
        val source:String = commandArgs.split("-->")[0]
        val target:String = commandArgs.split("-->")[1]

        val sourceTableName:String = source.split(":")[0]
        val sourceAttributeNames = source.split(":")[1]

        val targetTableName:String = target.split(":")[0]
        val targetAttributeNames = target.split(":")[1]
        return ConstraintTokens(sourceTableName,sourceAttributeNames,targetTableName,targetAttributeNames)
    }

    private fun buildConstraint(tableSource: Table, tableTarget: Table, type: String, index: Int): Constraint {
        var constraint = Constraint()
        constraint.name = "${tableSource.name}_${tableTarget.name}.$type$index"
        constraint.type = type
        constraint.source.table = tableSource.name
        constraint.source.columns = tableSource.columns
        constraint.target.table = tableTarget.name
        constraint.target.columns = tableTarget.columns
        return constraint
    }


}