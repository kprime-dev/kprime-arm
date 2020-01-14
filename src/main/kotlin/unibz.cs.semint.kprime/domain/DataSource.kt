package unibz.cs.semint.kprime.domain

import java.io.Serializable

class DataSource(var type: String, var name: String, var driver: String, var path: String, var user: String, var pass: String) : Serializable