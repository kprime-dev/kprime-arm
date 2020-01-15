package unibz.cs.semint.kprime.usecase.service

interface IXSLTransformerService {
    fun trasform(xsl:String,xml:String,out:String)
}