package unibz.cs.semint.kprime.service

interface IXSLTransformerService {
    fun trasform(xsl:String,xml:String,out:String)
}