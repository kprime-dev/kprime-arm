package unibz.cs.semint.kprime.domain

class DataSourceConnection(
        val id: String,
        val username: String,
        val pass: String,
        var autocommit: Boolean,
        val commited: Boolean,
        var closed:Boolean)