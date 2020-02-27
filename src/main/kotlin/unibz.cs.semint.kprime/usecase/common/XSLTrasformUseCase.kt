package unibz.cs.semint.kprime.usecase.common

import unibz.cs.semint.kprime.domain.UseCaseResult
import unibz.cs.semint.kprime.usecase.service.IXSLTransformerService

class XSLTrasformUseCase (val transformer:IXSLTransformerService){

    fun transform(xsl: String, xml: String, out: String):UseCaseResult<Unit> {
        return UseCaseResult("done",transformer.trasform(xsl,xml,out))
    }
}