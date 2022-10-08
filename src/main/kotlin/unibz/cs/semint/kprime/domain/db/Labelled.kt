package unibz.cs.semint.kprime.domain.db

interface Labelled {

    fun resetLabels(labelsAsString:String): String

    fun addLabels(labelsAsString: String): String

    fun addLabels(newLabels:List<Label>): String

    fun remLabels(newLabels:String): String

    fun remLabels(newLabels:List<Label>): String

    fun labelsAsString():String

    fun hasLabel(label:String) :Boolean

    fun listLabels():List<Label>

    fun prefixedLabels(prefix:String):List<Label>
}