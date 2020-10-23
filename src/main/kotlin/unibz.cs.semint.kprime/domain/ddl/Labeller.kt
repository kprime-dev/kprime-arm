package unibz.cs.semint.kprime.domain.ddl

class Labeller: Labelled {

    private lateinit var labels : MutableList<Label>

    override
    fun resetLabels(labelsAsString: String):String {
        if (labels==null) labels = mutableListOf()
        else labels.clear()
        addLabels(labelsAsString)
        return labelsAsString()
    }

    override
    fun addLabels(labelsAsString: String):String {
        if (labels==null) labels = mutableListOf()
        labels.addAll(labelsAsString.split(","))
        return labelsAsString()
    }

    override
    fun addLabels(newLabels: List<Label>): String {
        if (labels==null) labels = mutableListOf()
        labels.addAll(newLabels)
        return labelsAsString()
    }

    override
    fun labelsAsString(): String {
        return labels?.joinToString(",")?: ""
    }

    override
    fun hasLabel(label:String):Boolean {
        return labelsAsString().contains(label)
    }
}