package unibz.cs.semint.kprime.usecase

import org.junit.Test
import unibz.cs.semint.kprime.adapter.file.FileIOAdapter
import unibz.cs.semint.kprime.adapter.service.XMLSerializerJacksonAdapter
import unibz.cs.semint.kprime.scenario.sakila.readMeta
import unibz.cs.semint.kprime.scenario.sakila.sakilaDataSource
import unibz.cs.semint.kprime.usecase.current.TransformerXUseCase

class TransformerXUseCaseTI {

    @Test
    fun test_transformerx_decompose(){

        // given
        val db = readMeta(sakilaDataSource())
        if (db==null) {
            println("sakila meta db not open")
            return
        }
        db.schema
                //.checkBcnf()
                .addFunctionals("film","film_id --> replacement_cost, rental_duration, rental_rate")
        // val workingDir = "/home/nicola/Tmp/"
        val workingDir = "/home/nipe/Temp/kprime/"

        val transformerName="vertical"
        val params = mutableMapOf<String,Any>(
                "originTable" to "film",
                "targetTable1" to "film_catalog",
                "targetTable2" to "film_rental"
        )
        // when
        TransformerXUseCase(
                XMLSerializerJacksonAdapter(),
                FileIOAdapter(),
                workingDir + "traces/",
                workingDir + "transformers/"+transformerName+"/decompose/vertical_decompose_1_changeset.xml",
                workingDir + "transformers/"+transformerName+"/decompose/vertical_decompose_1_paths.properties",
                "",
                "",
                transformerName)
                .decompose(
                    db,params
                )
    }
}