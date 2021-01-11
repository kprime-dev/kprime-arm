package unibz.cs.semint.kprime.domain.ddl

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import unibz.cs.semint.kprime.domain.ddl.schemalgo.*

@JacksonXmlRootElement(localName = "schema")
class Schema () {
    @JacksonXmlProperty(isAttribute = true)
    var name: String =""
    @JacksonXmlProperty(isAttribute = true)
    var id: String=""

    var tables: ArrayList<Table>? = ArrayList<Table>()

    var constraints: MutableList<Constraint>? = ArrayList<Constraint>()

    fun table(name: String): Table? {
        if (tables().isEmpty()) return null
        return tables().filter { t -> t.name==name }.firstOrNull()
    }

    fun constraints(): MutableList<Constraint> {
        if (constraints!=null) return  constraints as MutableList<Constraint>
        return ArrayList()
    }

    fun constraintsByType(type :Constraint.TYPE): List<Constraint> {
        var resultCols = mutableSetOf<Column>()
        return constraints().filter { c ->
            c.type == type.name }
    }

    fun tables():ArrayList<Table> {
        if (tables!=null) return tables as ArrayList<Table>
        return ArrayList()
    }

    fun constraint(name: String): Constraint? {
        if (constraints().isEmpty()) return null
        return constraints().filter { c -> c.name==name }.firstOrNull()
    }

    fun constraintById(id: String): Constraint? {
        if (constraints().isEmpty()) return null
        return constraints().filter { c -> c.id==id }.firstOrNull()
    }

    fun constraintByTable(name: String): Constraint? {
        if (constraints().isEmpty()) return null
        return constraints().filter { c -> c.source.table==name || c.target.table==name }.firstOrNull()
    }

    fun keys(tableName: String): List<Constraint> {
        val first = constraints().filter { c ->
            c.type == Constraint.TYPE.PRIMARY_KEY.name &&
                    c.source.table == "${tableName}"
        }.toList()
        return first
    }

    fun key(tableName: String): Set<Column> {
        var resultCols = mutableSetOf<Column>()
        val keys = keys(tableName)
        if (keys.isEmpty()) return mutableSetOf()
        return keys[0].source.columns.toSet()
    }

    fun addKey(tableName:String, k:Set<Column>): Constraint {
        val primaryConstraint = buildKey(tableName, k)
        constraints().add(primaryConstraint)
        return primaryConstraint
    }

    fun buildKey(tableName: String, k: Set<Column>): Constraint {
        val primaryConstraint = Constraint()
        primaryConstraint.name = "pkey_$tableName"
        primaryConstraint.source.table = "$tableName"
        primaryConstraint.source.columns.addAll(k)
        primaryConstraint.target.columns.addAll(k)
        primaryConstraint.type = Constraint.TYPE.PRIMARY_KEY.name
        return primaryConstraint
    }

    fun keys(): List<Constraint> {
        return constraints().filter { c -> c.type.equals(Constraint.TYPE.PRIMARY_KEY.name) }
    }

    fun foreignKeys(): List<Constraint> {
        return constraints().filter { c -> c.type.equals(Constraint.TYPE.FOREIGN_KEY.name) }
    }

    fun foreignsTable(tableName: String): List<Constraint> {
        return foreignKeys().filter { f -> f.source.name.equals(tableName) }
    }

    fun foreignsTargets(tableName: String): List<Constraint> {
        return foreignKeys().filter { f -> f.target.name.equals(tableName) }
    }

    fun doubleIncs(): List<Constraint> {
        return constraints().filter { c -> c.type.equals(Constraint.TYPE.DOUBLE_INCLUSION.name) }
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

    fun addFunctional(commandArgs:String): Schema {
        val tableName:String = commandArgs.split(":")[0]
        val setExpression: String= commandArgs.split(":")[1]
        return addFunctional(tableName,setExpression)
    }

    fun addMultivalued(commandArgs:String): Schema {
        val tableName:String = commandArgs.split(":")[0]
        val setExpression: String= commandArgs.split(":")[1]
        return addMultivalued(tableName,setExpression)
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

    fun addFunctional(tableName:String, setExpression: String): Schema {
        val constraints = constraints()
        constraints.addAll(SchemaCmdParser
                .parseFunctionals(constraints.size, tableName, setExpression))
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
        constraints().addAll(constraintsToAdd)
        return this
    }

    fun addKey(commandArgs:String):Schema {
        val tableName:String = commandArgs.split(":")[0]
        val attributeNames = commandArgs.split(":")[1]
        val constraint = Constraint.addKey {}
        constraint.id="ck_$tableName"
        constraint.name = tableName+".primaryKey"
        constraint.source.table=tableName
        constraint.target.table=tableName
        constraint.source.columns.addAll(Column.set(attributeNames))
        constraint.target.columns.addAll(Column.set(attributeNames))
        constraints().add(constraint)
        return this
    }

    // FIXME Use SchemaCmdParser
    fun addForeignKey(commandArgs:String):Schema {
        val source:String = commandArgs.split("-->")[0]
        val target:String = commandArgs.split("-->")[1]

        val sourceTableName:String = source.split(":")[0]
        val sourceAttributeNames = source.split(":")[1]

        val targetTableName:String = target.split(":")[0]
        val targetAttributeNames = target.split(":")[1]

        val constraintPos = constraintsByType(Constraint.TYPE.FOREIGN_KEY).size+1
        val constraint = Constraint.foreignkey {}
        constraint.id="cfk$constraintPos"
        constraint.name = "${sourceTableName}_${targetTableName}.foreignKey$constraintPos"
        constraint.source.table=sourceTableName
        constraint.target.table=targetTableName
        constraint.source.columns.addAll(Column.set(sourceAttributeNames))
        constraint.target.columns.addAll(Column.set(targetAttributeNames))
        constraints().add(constraint)
        return this
    }

    // FIXME Use SchemaCmdParser
    fun addDoubleInc(commandArgs:String):Schema {
        val source:String = commandArgs.split("<->")[0]
        val target:String = commandArgs.split("<->")[1]

        val sourceTableName:String = source.split(":")[0]
        val sourceAttributeNames = source.split(":")[1]

        val targetTableName:String = target.split(":")[0]
        val targetAttributeNames = target.split(":")[1]

        val constraintPos = constraintsByType(Constraint.TYPE.DOUBLE_INCLUSION).size+1
        val constraint = Constraint.doubleInclusion {}
        constraint.id="cdi$constraintPos"
        constraint.name = "${sourceTableName}_${targetTableName}.doubleInc$constraintPos"
        constraint.source.table=sourceTableName
        constraint.target.table=targetTableName
        constraint.source.columns.addAll(Column.set(sourceAttributeNames))
        constraint.target.columns.addAll(Column.set(targetAttributeNames))
        constraints().add(constraint)
        return this
    }

    fun addInclusion(commandArgs:String):Schema {
        val source:String = commandArgs.split("-->")[0]
        val target:String = commandArgs.split("-->")[1]

        val sourceTableName:String = source.split(":")[0]
        val sourceAttributeNames = source.split(":")[1]

        val targetTableName:String = target.split(":")[0]
        val targetAttributeNames = target.split(":")[1]

        val constraintPos = constraintsByType(Constraint.TYPE.INCLUSION).size+1
        val constraint = Constraint.inclusion {}
        constraint.id="ci$constraintPos"
        constraint.name = "${sourceTableName}_${targetTableName}.inclusion$constraintPos"
        constraint.source.table=sourceTableName
        constraint.target.table=targetTableName
        constraint.source.columns.addAll(Column.set(sourceAttributeNames))
        constraint.target.columns.addAll(Column.set(targetAttributeNames))
        constraints().add(constraint)
        return this
    }

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

    fun oidForTable(tableName:String):List<String> {
        return oid(this,tableName)
    }

}
