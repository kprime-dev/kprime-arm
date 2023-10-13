package unibz.cs.semint.kprime.domain.db

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import unibz.cs.semint.kprime.domain.db.schemalgo.*
import unibz.cs.semint.kprime.domain.ddl.ChangeSet

enum class SchemaIsA  {EXCLUSIVE,PARTITION,COVER,GENERIC}

@JacksonXmlRootElement(localName = "schema")
class Schema () {
    @JacksonXmlProperty(isAttribute = true)
    var name: String =""
    @JacksonXmlProperty(isAttribute = true)
    var id: String=""

    @JacksonXmlElementWrapper(localName = "tables")
    @JacksonXmlProperty(localName = "table")
    var tables: ArrayList<Table>? = ArrayList<Table>()

    @JacksonXmlElementWrapper(localName = "constraints")
    @JacksonXmlProperty(localName = "constraint")
    var constraints: MutableList<Constraint>? = ArrayList<Constraint>()

    /*
     *  Reading methods
     */

    fun table(name: String): Table? {
        if (tables().isEmpty()) return null
        return tables().filter { t -> t.name==name || t.view==name }.firstOrNull()
    }

    fun tableId(id: String): Table? {
        if (tables().isEmpty()) return null
        return tables().filter { t -> t.id==id }.firstOrNull()
    }

    fun relation(relName:String):Table? {
        return tables().firstOrNull { it.name.contains("_${relName}_") || it.view == relName }
    }

    fun parents(tableName:String): List<Table> {
        println("parents:")
        val parentNames =  table(tableName)?.parent?.split(",")?: emptyList()
        val parents = mutableListOf<Table>()
        for (parentName in parentNames) {
            println(parentName)
            val parentTable = table(parentName)?:continue
            parents.add(parentTable)
        }
        return parents
    }

    fun children(tableName:String): List<Table> {
        val children = mutableListOf<Table>()
        for (table in tables()) {
            if (table.parent?.split(",")?.contains(tableName) == true)
                children.add(table)
        }
        return children
    }

    fun constraints(): MutableList<Constraint> {
        return constraints?: mutableListOf()
    }

    fun constraintsByType(type :Constraint.TYPE): List<Constraint> {
        var resultCols = mutableSetOf<Column>()
        return constraints().filter { c ->
            c.type == type.name }
    }

    fun tables():ArrayList<Table> {
        if (tables==null) tables = ArrayList()
        return tables as ArrayList<Table>
    }

    fun constraint(name: String): Constraint? {
        if (constraints().isEmpty()) return null
        return constraints().filter { c -> c.name==name }.firstOrNull()
    }

    fun constraintById(id: String): Constraint? {
        if (constraints().isEmpty()) return null
        return constraints().filter { c -> c.id==id }.firstOrNull()
    }

    fun constraintsByTable(name: String): List<Constraint> {
        if (constraints().isEmpty()) return emptyList()
        return constraints().filter { c -> c.source.table==name || c.target.table==name }
    }

    fun moveConstraintsFromTableToTable(sourceTableName: String, targetTableName:String) {
        val sourceTableConstraints = constraintsByTable(sourceTableName)
        for (constr in sourceTableConstraints) {
            if (constr.source.table==sourceTableName) {
                constr.source.table=targetTableName
            }
            if (constr.target.table==sourceTableName) {
                constr.target.table=targetTableName
            }
//            println("MOVED CONSTRAINT FROM $sourceTableName TO ${constr.name}:"+constr.toString())
        }
    }

    fun moveConstraintsFromColsToCol(originTableName: String, keyCols: String, sid: String) {
        val constraints = constraintsByTable(originTableName)
        for (constr in constraints) {
            for (col in constr.source.columns) {
                if (keyCols.contains(col.name)) col.name = sid
            }
            for (col in constr.target.columns) {
                if (keyCols.contains(col.name)) col.name = sid
            }
        }
    }

    fun removeKeyConstraint(originTableName: String) {
        val constraints = constraintsByTable(originTableName)
        var toRemove : Constraint? = null
        for (constr in constraints) {
            if (constr.type == Constraint.TYPE.PRIMARY_KEY.name)
                toRemove = constr
        }
        if (toRemove!=null) this.constraints?.remove(toRemove)
    }

    fun copyConstraintsFromTableToTable(sourceTableName: String, targetTableName:String) {
        val sourceTableConstraints = constraintsByTable(sourceTableName)
        var constrToRemove = mutableListOf<Constraint>()
        for (constr in sourceTableConstraints) {
            val copyconstr = constr.clone()
            constrToRemove.add(constr)
            copyconstr.name = copyconstr.name+"_1"
            if (constr.source.table==sourceTableName) {
                copyconstr.source.table=targetTableName
            }
            if (constr.target.table==sourceTableName) {
                copyconstr.target.table=targetTableName
            }
            if (constraints==null) constraints = ArrayList()
            constraints?.add(copyconstr)
//            println("COPIED CONSTRAINT FROM $sourceTableName TO:"+constr.toString())
        }
        this.constraints?.removeAll(constrToRemove)
    }

    fun keysPrimary(tableName: String): List<Constraint> {
        val first = constraints().filter { c ->
            c.type == Constraint.TYPE.PRIMARY_KEY.name &&
                    c.source.table == "${tableName}"
        }.toList()
        return first
    }

    fun keySurrogate(tableName: String): Constraint? {
        return constraints().filter { c ->
            c.type == Constraint.TYPE.SURROGATE_KEY.name &&
                    c.source.table == "${tableName}"
        }.firstOrNull()
    }

    fun keyPrimary(tableName: String): Constraint? {
        return constraints().filter { c ->
            c.type == Constraint.TYPE.PRIMARY_KEY.name &&
                    c.source.table == "${tableName}"
        }.firstOrNull()
    }

    fun keyCandidate(tableName: String): List<Constraint> {
        return constraints().filter { c ->
            c.type == Constraint.TYPE.CANDIDATE_KEY.name &&
                    c.source.table == "${tableName}"
        }
    }

    fun keysAll(tableName: String): List<Constraint> {
        val first = constraints().filter { c ->
            (c.type == Constraint.TYPE.PRIMARY_KEY.name ||
                    c.type == Constraint.TYPE.CANDIDATE_KEY.name||
                    c.type == Constraint.TYPE.SURROGATE_KEY.name) &&
                    c.source.table == "${tableName}"
        }.toList()
        return first
    }

    fun keyCols(tableName: String): Set<Column> {
        var resultCols = mutableSetOf<Column>()
        val keys = keysPrimary(tableName)
        if (keys.isEmpty()) return mutableSetOf()
        return keys[0].source.columns.toSet()
    }

    fun notkey(tableName: String): Set<Column> {
        val keyCols = keyCols(tableName)
        val table = table(tableName) ?: return emptySet()
        val cols = table.columns.toMutableSet()
        cols.removeAll(keyCols)
        return cols
    }

    /*
     *  Writing methods
     */

    fun addKey(tableName:String, keyCols:Set<Column>): Constraint {
        checkExistence(tableName, keyCols)
        keyPrimary(tableName).apply { this?.type = Constraint.TYPE.CANDIDATE_KEY.name }
        val primaryConstraint = buildKey(tableName, keyCols, Constraint.TYPE.PRIMARY_KEY.name)
        constraints().add(primaryConstraint)
        return primaryConstraint
    }

    fun addSubset(tableName:String, targetName:String, keyCols:Set<Column>): Constraint {
        checkExistence(tableName, keyCols)
        val subsetConstraint = Constraint()
        subsetConstraint.name = "SUBSET_${tableName}_${targetName}"
        subsetConstraint.source.table = tableName
        subsetConstraint.target.table = targetName
        subsetConstraint.source.columns.addAll(keyCols)
        subsetConstraint.target.columns.addAll(keyCols)
        subsetConstraint.type = Constraint.TYPE.SUBSET.name
        constraints().add(subsetConstraint)
        return subsetConstraint
    }

    fun addEquals(tableName:String, targetName:String, keyCols:Set<Column>): Constraint {
        checkExistence(tableName, keyCols)
        val subsetConstraint = Constraint()
        subsetConstraint.name = "EQUALS_${tableName}_${targetName}"
        subsetConstraint.source.table = tableName
        subsetConstraint.target.table = targetName
        subsetConstraint.source.columns.addAll(keyCols)
        subsetConstraint.target.columns.addAll(keyCols)
        subsetConstraint.type = Constraint.TYPE.EQUALITY.name
        constraints().add(subsetConstraint)
        return subsetConstraint
    }

    fun addDisjoint(tableName:String, targetName:String, keyCols:Set<Column>): Constraint {
        checkExistence(tableName, keyCols)
        val subsetConstraint = Constraint()
        subsetConstraint.name = "SUBSET_${tableName}_${targetName}"
        subsetConstraint.source.table = tableName
        subsetConstraint.target.table = targetName
        subsetConstraint.source.columns.addAll(keyCols)
        subsetConstraint.target.columns.addAll(keyCols)
        subsetConstraint.type = Constraint.TYPE.DISJUNCTION.name
        constraints().add(subsetConstraint)
        return subsetConstraint
    }

    fun addKey(commandArgs:String):Schema {
        val tableName:String = commandArgs.split(":")[0]
        val attributeNames = commandArgs.split(":")[1]
        addKey(tableName,Column.set(attributeNames))
        return this
    }

    // TO FIX:
    fun addId(tableName:String, idColumns:Set<Column>): Constraint {
        checkExistence(tableName, idColumns)
        val idConstraint = buildKey(tableName, idColumns, Constraint.TYPE.ID_KEY.name)
        constraints().add(idConstraint)
        return idConstraint
    }

    fun addId(commandArgs:String):Schema {
        val tableName:String = commandArgs.split(":")[0]
        val attributeNames = commandArgs.split(":")[1]
        addId(tableName,Column.set(attributeNames))
        return this
    }

    fun addSurrogateKey(commandArgs:String):Schema {
        val tableName:String = commandArgs.split(":")[0]
        val attributeNames = commandArgs.split(":")[1]
        val keyCols = Column.set(attributeNames)
        checkExistence(tableName, keyCols)
        constraints().add(buildKey(tableName, keyCols, Constraint.TYPE.SURROGATE_KEY.name))
        return this
    }

    fun addForeignKey(commandArgs:String):Schema {
        val constraintTokens = SchemaCmdParser.parseExternalConstraintArgs(commandArgs)
        val constraintPos = constraintsByType(Constraint.TYPE.FOREIGN_KEY).size+1
        val constraint = Constraint.foreignkey()
        constraint.id="cfk$constraintPos"
        constraint.name = "${constraintTokens.sourceName}_${constraintTokens.targetName}.foreignKey$constraintPos"
        constraint.source.table=constraintTokens.sourceName
        constraint.target.table=constraintTokens.targetName
        constraint.source.name=constraintTokens.sourceName
        constraint.target.name=constraintTokens.targetName
        constraint.source.columns.addAll(Column.set(constraintTokens.sourceAttrs))
        constraint.target.columns.addAll(Column.set(constraintTokens.targetAttrs))
        constraints().add(constraint)
        return this
    }

    fun buildKey(tableName: String, keyCols: Set<Column>, keyType: String): Constraint {
        val keyConstraint = Constraint()
        keyConstraint.name = "KEY_${tableName}_"+keyCols.map { it.name }.joinToString("_")
        keyConstraint.source.table = tableName
        keyConstraint.target.table = tableName
        keyConstraint.source.columns.addAll(keyCols)
        keyConstraint.target.columns.addAll(keyCols)
        keyConstraint.type = keyType
        return keyConstraint
    }

    fun keys(): List<Constraint> {
        return constraints().filter { c -> c.type.equals(Constraint.TYPE.PRIMARY_KEY.name) }
    }

    fun keysSurrogate(): List<Constraint> {
        return constraints().filter { c -> c.type.equals(Constraint.TYPE.SURROGATE_KEY.name) }
    }

    fun keysAll(): List<Constraint> {
        return constraints().filter {
            c -> c.type.equals(Constraint.TYPE.PRIMARY_KEY.name)
                || c.type.equals(Constraint.TYPE.CANDIDATE_KEY.name)
                || c.type.equals(Constraint.TYPE.SURROGATE_KEY.name) }
    }

    fun foreignKeys(): List<Constraint> {
        return constraints().filter { c -> c.type.equals(Constraint.TYPE.FOREIGN_KEY.name) }
    }

    fun notNull(tableName:String): List<Constraint> {
        return constraints().filter { c -> c.type.equals(Constraint.TYPE.NOTNULL.name) && c.source.table==tableName }
    }

    fun unique(tableName:String): List<Constraint> {
        return constraints().filter { c -> c.type.equals(Constraint.TYPE.UNIQUE.name) && c.source.table==tableName }
    }

    fun inclusions(): List<Constraint> {
        return constraints().filter { c -> c.type.equals(Constraint.TYPE.INCLUSION.name) }
    }

    fun inclusions(tableName:String): List<Constraint> {
        return inclusions().filter { f -> f.source.table.equals(tableName) || f.target.table.equals(tableName)}
    }

    fun foreignsWithSource(tableName: String): List<Constraint> {
        return foreignKeys().filter { f -> f.source.table.equals(tableName) }
    }

    fun foreignsWithTarget(tableName: String): List<Constraint> {
        return foreignKeys().filter { f -> f.target.table.equals(tableName) }
    }

    fun inclusionsWithSource(tableName: String): List<Constraint> {
        return inclusions().filter { f -> f.source.table.equals(tableName) }
    }

    fun inclusionsWithTarget(tableName: String): List<Constraint> {
        return inclusions().filter { f -> f.target.table.equals(tableName) }
    }

    fun referencedTablesOf(tableName: String): Set<Table> {
        var rTables = foreignTablesOf(tableName)
        println("rTables:[$rTables]")
        val diTables = doubleIncTablesOf(tableName)
        println("diTables:[$diTables]")
        val iTables = inclusionTablesOf(tableName)
        println("iTables:[$iTables]")
        rTables.addAll(diTables)
        rTables.addAll(iTables)
        return rTables.toSet()
    }

    private fun foreignTablesOf(tableName: String): ArrayList<Table> {
        var rTables = ArrayList<Table>()
        var foreignConstr = foreignsWithTarget(tableName)
        for (foreign in foreignConstr) {
            var t = table(foreign.source.table)
            if (t != null) rTables.add(t)
        }
        return rTables
    }

    private fun inclusionTablesOf(tableName: String): ArrayList<Table> {
        var iTables = ArrayList<Table>()
        var inclusionConstraints = inclusionsWithTarget(tableName)
        for (inclusion in inclusionConstraints) {
            var t = table(inclusion.source.table)
            if (t != null) iTables.add(t)
        }
        return iTables
    }

    private fun doubleIncTablesOf(tableName: String): ArrayList<Table> {
        var rTables = ArrayList<Table>()
        val doubleTargets = doubleIncs().filter { di -> di.source.table.equals(tableName)}
        for (double in doubleTargets) {
            val name1 = double.target.table
            var t = table(name1)
            if (t != null) rTables.add(t)
        }
        val doubleSources = doubleIncs().filter { di -> di.target.table.equals(tableName)}
        for (double in doubleSources) {
            val name1 = double.source.table
            var t = table(name1)
            if (t != null) rTables.add(t)
        }
        return rTables
    }

    fun doubleIncs(): List<Constraint> {
        return constraints().filter { c -> c.type.equals(Constraint.TYPE.DOUBLE_INCLUSION.name) }
    }

    fun doubleIncs(tableName:String): List<Constraint> {
        return doubleIncs().filter { f -> f.source.table.equals(tableName) || f.target.table.equals(tableName)}
    }

    fun multivalued(): List<Constraint> {
        return constraints().filter { c -> c.type.equals(Constraint.TYPE.MULTIVALUED.name) }
    }

    fun multivalued(tableName:String): List<Constraint> {
        return multivalued().filter { f -> f.source.table.equals(tableName) }
    }

    fun functionalLHS(tableName: String): Set<Column> {
        var resultCols = mutableSetOf<Column>()
        val first = constraints().filter { c ->
            c.type == Constraint.TYPE.FUNCTIONAL.name &&
                    c.name == "functional.${tableName}"
        }.toList()
        if (first.isEmpty()) return mutableSetOf()
        return first[0].source.columns.toSet()
    }

    fun functionals(): Set<Constraint> {
        var resultCols = mutableSetOf<Column>()
        return constraints().filter { c ->
            c.type == Constraint.TYPE.FUNCTIONAL.name }.toSet()
    }

    fun functionalsTable(tableName:String): List<Constraint> {
        return functionals().filter { f -> f.source.table.equals(tableName) }
    }

    fun functionalRHS(tableName: String): Set<Column> {
        var resultCols = mutableSetOf<Column>()
        val first = constraints().filter { c ->
            c.type == Constraint.TYPE.FUNCTIONAL.name &&
                    c.name == "functional.${tableName}"
        }.toList()
        if (first.isEmpty()) return mutableSetOf()
        return first[0].target.columns.toSet()
    }

    fun addTable(commandArgs:String) : Schema {
        val table = SchemaCmdParser.parseTable(commandArgs)
        val tablePos=tables().size+1
        table.id="t$tablePos"
        tables().add(table)
        return this
    }

    fun dropTable(commandArgs:String) : Schema {
        var tableNames = commandArgs.split(" ")
        for (tableName  in tableNames) {
            val t= table(tableName)
            if (t!=null){ tables().remove(t)}
            val toRemove = mutableListOf<Constraint>()
            for (constr in constraints()) {
                if (constr.source.table.equals(tableName))
                    toRemove.add(constr)
                if (constr.target.table.equals(tableName))
                    toRemove.add(constr)
            }
            constraints().removeAll(toRemove)
        }
        return this
    }

    fun dropConstraint(commnadArgs:String) : Schema {
        var constraintNames = commnadArgs.split(" ")
        for (constraintName in constraintNames) {
            var constraint = constraint(constraintName)
            constraints().remove(constraint)
        }
        return this
    }

    fun addFunctional(commandArgs:String): Schema {
        val tableName:String = commandArgs.split(":")[0]
        val setExpression: String= commandArgs.split(":")[1]
        return addFunctional(tableName,setExpression)
    }

    fun addFunctional(tableName:String, setExpression: String): Schema {
        val constraints = constraints()
        val newConstraints = SchemaCmdParser.parseFunctionals(constraints.size, tableName, setExpression)
        checkExistence(newConstraints)
        constraints.addAll(newConstraints)
        return this
    }

    internal fun addFunctional(tableName:String, lhs:Set<Column>, rhs:Set<Column>){
        val constraintPos = constraintsByType(Constraint.TYPE.FUNCTIONAL).size+1
        val functionalConstraint = Constraint()
        functionalConstraint.id="cf$constraintPos"
        functionalConstraint.name="functional$constraintPos.$tableName"
        functionalConstraint.source.table="$tableName"
        functionalConstraint.source.columns.addAll(lhs)
        functionalConstraint.target.table="$tableName"
        functionalConstraint.target.columns.addAll(rhs)
        functionalConstraint.type= Constraint.TYPE.FUNCTIONAL.name
        constraints().add(functionalConstraint)

    }

    fun addMultivalued(commandArgs:String): Schema {
        val tableName:String = commandArgs.split(":")[0]
        val setExpression: String= commandArgs.split(":")[1]
        return addMultivalued(tableName,setExpression)
    }

    fun addMultivalued(tableName:String, setExpression: String): Schema {
        val constraintPos = constraintsByType(Constraint.TYPE.MULTIVALUED).size+1
        val constraintsToAdd = Constraint.set(setExpression)
        for (constraint in constraintsToAdd) {
            constraint.id="cm$constraintPos"
            constraint.name=tableName+".multivalued$constraintPos"
            constraint.type=Constraint.TYPE.MULTIVALUED.name
            constraint.source.table=tableName
            constraint.target.table=tableName
        }
        checkExistence(constraintsToAdd)
        constraints().addAll(constraintsToAdd)
        return this
    }

    fun addDoubleInc(commandArgs:String):Schema {
        val source:String = commandArgs.split("<->")[0]
        val target:String = commandArgs.split("<->")[1]

        val sourceTableName:String = source.split(":")[0]
        val sourceAttributeNames = source.split(":")[1]

        val targetTableName:String = target.split(":")[0]
        val targetAttributeNames = target.split(":")[1]

        val constraint = buildDoubleInc(sourceTableName, targetTableName, sourceAttributeNames, targetAttributeNames)
        checkExistence(constraint)
        constraints().add(constraint)
        return this
    }

    internal fun buildDoubleInc(sourceTableName: String, targetTableName: String, sourceAttributeNames: String, targetAttributeNames: String): Constraint {
        val constraintPos = constraintsByType(Constraint.TYPE.DOUBLE_INCLUSION).size + 1
        val constraint = Constraint.doubleInclusion()
        constraint.id = "cdi$constraintPos"
        constraint.name = "${sourceTableName}_${targetTableName}.doubleInc$constraintPos"
        constraint.source.table = sourceTableName
        constraint.target.table = targetTableName
        constraint.source.columns.addAll(Column.set(sourceAttributeNames))
        constraint.target.columns.addAll(Column.set(targetAttributeNames))
        return constraint
    }

    fun addInclusion(commandArgs:String):Schema {
        val source:String = commandArgs.split("-->")[0]
        val target:String = commandArgs.split("-->")[1]

        val sourceTableName:String = source.split(":")[0]
        val sourceAttributeNames = source.split(":")[1]

        val targetTableName:String = target.split(":")[0]
        val targetAttributeNames = target.split(":")[1]

        val constraint = buildInclusion(sourceTableName, targetTableName, sourceAttributeNames, targetAttributeNames)
        checkExistence(constraint)
        constraints().add(constraint)
        return this
    }

    internal fun buildInclusion(sourceTableName: String, targetTableName: String, sourceAttributeNames: String, targetAttributeNames: String): Constraint {
        val constraintPos = constraintsByType(Constraint.TYPE.INCLUSION).size + 1
        val constraint = Constraint.inclusion()
        constraint.id = "ci$constraintPos"
        constraint.name = "${sourceTableName}_${targetTableName}.inclusion$constraintPos"
        constraint.source.table = sourceTableName
        constraint.target.table = targetTableName
        constraint.source.columns.addAll(Column.set(sourceAttributeNames))
        constraint.target.columns.addAll(Column.set(targetAttributeNames))
        return constraint
    }

    /** <table-name>:<col1>, .. ,<colN> with column name with joint not null constraint **/
    fun addNotNull(commandArgs: String):Schema {
        val sourceTableName:String = commandArgs.split(":")[0]
        val sourceAttributeNames = commandArgs.split(":")[1]
        val constraint = buildNotNull(sourceTableName, sourceAttributeNames)
        checkExistence(constraint)
        constraints().add(constraint)
        return this
    }

    internal fun buildNotNull(sourceTableName: String, sourceAttributeNames: String): Constraint {
        val constraintPos = constraintsByType(Constraint.TYPE.NOTNULL).size + 1
        val constraint = Constraint()
        constraint.type = Constraint.TYPE.NOTNULL.name
        constraint.id = "ci$constraintPos"
        constraint.name = "${sourceTableName}.notnull$constraintPos"
        constraint.source.table = sourceTableName
        constraint.target.table = ""
        constraint.source.columns.addAll(Column.set(sourceAttributeNames))
        constraint.target.columns.addAll(emptySet())
        return constraint
    }

    /** <table-name>:<col1>, .. ,<colN> with column name with joint unique constraint **/
    fun addUnique(commandArgs: String):Schema {
        val sourceTableName:String = commandArgs.split(":")[0]
        val sourceAttributeNames = commandArgs.split(":")[1]
        val constraint = buildUnique(sourceTableName, sourceAttributeNames)
        checkExistence(constraint)
        constraints().add(constraint)
        return this
    }

    internal fun buildUnique(sourceTableName: String, sourceAttributeNames: String): Constraint {
            val constraint = Constraint()
            val constraintPos = constraintsByType(Constraint.TYPE.UNIQUE).size + 1
            constraint.type = Constraint.TYPE.UNIQUE.name
            constraint.id = "ci$constraintPos"
            constraint.name = "${sourceTableName}.notnull$constraintPos"
            constraint.source.table = sourceTableName
            constraint.target.table = ""
            constraint.source.columns.addAll(Column.set(sourceAttributeNames))
            constraint.target.columns.addAll(emptySet())
            return constraint
    }

    fun addOrAtLeastOne(commandArgs: String):Schema {
        val constraint = buildOrAtLeastOne(SchemaCmdParser.parseExternalConstraintArgs(commandArgs))
        checkExistence(constraint)
        constraints().add(constraint)
        return this
    }

    private fun buildOrAtLeastOne(
        constraintTokens: SchemaCmdParser.ConstraintTokens ): Constraint {
        val constraint = Constraint()
        val constraintPos = constraintsByType(Constraint.TYPE.UNIQUE).size + 1
        constraint.type = Constraint.TYPE.OR_AT_LEAST_ONE.name
        constraint.id = "${constraint.type}_$constraintPos"
        constraint.name = "${constraintTokens.sourceName}.notnull$constraintPos"
        constraint.source.table = constraintTokens.sourceName
        constraint.target.table = constraintTokens.targetName
        constraint.source.columns.addAll(Column.set(constraintTokens.sourceAttrs))
        constraint.target.columns.addAll(Column.set(constraintTokens.targetAttrs))
        return constraint
    }

    fun addIsA(generic:String,specifics:List<String>,condition:String) {
        for (specific in specifics) {
            val specificTable = table(specific)?:continue
            specificTable.parent = generic
            specificTable.condition = condition
        }
    }

    /*
     *  Validation methods
     */

    fun decomposeBCNF(): Set<Relation> {
        var allDecomposed = mutableSetOf<Relation>()
        var tables = tables()
        for ( table in tables) {
            var fds = functionalsTable(table.name)
            allDecomposed.addAll(decomposeToBCNF(table.columns.toSet(), fds.toSet()))
        }
        return allDecomposed
    }

    fun decompose3NF(): Set<Relation> {
        var allDecomposed = mutableSetOf<Relation>()
        var tables = tables()
        for ( table in tables) {
            var fds = functionalsTable(table.name)
            allDecomposed.addAll(decomposeTo3NF(table.columns.toSet(), fds.toSet()))
        }
        return allDecomposed
    }

    fun violations3NF(): Set<Constraint> {
        var allViolations = mutableSetOf<Constraint>()
        var tables = tables()
        for ( table in tables) {
            var fds = functionalsTable(table.name)
            allViolations.addAll(check3NF(table.columns.toSet(), fds.toSet()))
        }
        return allViolations

    }

    fun violationsBCNF(): Set<Constraint> {
        var allViolations = mutableSetOf<Constraint>()
        var tables = tables()
        for ( table in tables) {
            var fds = functionalsTable(table.name)
            allViolations.addAll(checkBCNF(table.columns.toSet(), fds.toSet()))
        }
        return allViolations

    }

    /**
     * TODO weak if has primarykey with-a column used as foreignkey.
    fun isTableWeak(tableName:String):Boolean {
        val key = key(tableName)
        for (keycol in key) {
            foreignKeys().forEach{fkey -> fkey.target.columns. }
        }
        return false
    }
     */

    fun oidForTable(tableName:String): ChangeSet {
        return oid(this,tableName)
    }

    fun isBinaryRelation(tableName:String):Boolean {
        val table = table(tableName)?:return false
        return table.columns.size==2
                && foreignsWithTarget(tableName).size==2
    }

    private fun checkExistence(
        tableName: String,
        keyCols: Set<Column>
    ) {
        val table = table(tableName.trim())
            ?: relation(tableName.trim())
            ?: throw IllegalArgumentException("Table $tableName not found")
        for (col in keyCols) {
            if (!table.hasColumn(col.name))
                throw IllegalArgumentException("Column ${col.name} not found in table $tableName.")
        }
    }


    private fun checkExistence(
        newConstraints: Set<Constraint>
    ) {
        for (constr in newConstraints) {
            checkExistence(constr)
        }
    }

    private fun checkExistence(constraint: Constraint) {
        println("CHECK $constraint")
        val sourceTable = table(constraint.source.table)
            ?: relation(constraint.source.table)
            ?: throw IllegalArgumentException("Source table ${constraint.source.table} not found to add functional.")
        for (left in constraint.left()) {
            if (!sourceTable.hasColumn(left.name))
                throw IllegalArgumentException("Left column ${left.name} of table ${constraint.source.table} not found to add functional.")
        }
        val tableTarget = table(constraint.target.table)
            ?: relation(constraint.source.table)
            ?: throw IllegalArgumentException("Target table ${constraint.target.table}.na not found to add functional.")
        for (right in constraint.right()) {
            if (!tableTarget.hasColumn(right.name))
                throw IllegalArgumentException("Right column ${right.name} of table ${constraint.target.table} not found to add functional.")
        }
    }
}
