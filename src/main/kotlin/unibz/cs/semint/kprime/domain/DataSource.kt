package unibz.cs.semint.kprime.domain

import java.io.Serializable

class DataSource(
        var type: String,
        var name: String,
        var driver: String,
        var path: String,
        var user: String,
        var pass: String,
        var driverUrl: String = "") : Serializable {

    var connection : DataSourceConnection? = null
    var currentResources: HashMap<String,Any>? = null

    fun setResource(name :String,resource:Any) {
        if (currentResources==null)
            currentResources = HashMap()
        currentResources!![name]=resource
    }

    fun getResource(name:String):Any? {
        if (currentResources!=null) return currentResources!![name]
        return null
    }

    fun remResource(name:String) {
        if (currentResources!=null) currentResources!!.remove(name)
    }
}