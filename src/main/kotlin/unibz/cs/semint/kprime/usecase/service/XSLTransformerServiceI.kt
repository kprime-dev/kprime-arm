package unibz.cs.semint.kprime.usecase.service

interface XSLTransformerServiceI {
    fun trasform(xsl:String,xml:String,out:String)
}