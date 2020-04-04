package unibz.cs.semint.kprime.usecase.common

import unibz.cs.semint.kprime.usecase.UseCaseResult
import unibz.cs.semint.kprime.usecase.service.XSLTransformerServiceI

class XSLTrasformUseCase (val transformer:XSLTransformerServiceI){

    fun transform(xsl: String, xml: String, out: String): UseCaseResult<Unit> {
        return UseCaseResult("done", transformer.trasform(xsl, xml, out))
    }
}