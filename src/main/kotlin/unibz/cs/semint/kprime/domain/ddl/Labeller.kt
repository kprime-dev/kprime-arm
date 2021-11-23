package unibz.cs.semint.kprime.domain.ddl

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty

class Labeller(): Labelled {

    @JacksonXmlProperty(isAttribute = true)
    var labels: String? = null

    override fun resetLabels(labelsAsString: String): String {
        labels = labelsAsString
        return labels!!
    }

    override fun addLabels(labelsAsString: String): String {
        if (labels==null) labels = labelsAsString
        else labels += labelsAsString
        return labels!!
    }

    override fun addLabels(newLabels: List<Label>): String {
        return addLabels(newLabels.joinToString(","))
    }

    override fun hasLabel(label: String): Boolean {
        return labels?.contains(label)?:false
    }

    override fun labelsAsString(): String {
        return labels?: ""
    }

    override fun remLabels(newLabels: List<Label>): String {
        val labels2 = labels ?: return ""
        return resetLabels(labels2.split(",")
                .filter { !newLabels.contains(it) }
                .joinToString(","))
    }

}