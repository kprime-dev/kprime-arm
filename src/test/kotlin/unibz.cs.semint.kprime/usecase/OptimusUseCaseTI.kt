package unibz.cs.semint.kprime.usecase

import org.junit.Assert
import org.junit.Assert.assertNotNull
import org.junit.Test
import unibz.cs.semint.kprime.adapter.file.FileIOAdapter
import unibz.cs.semint.kprime.adapter.service.XMLSerializerJacksonAdapter
import unibz.cs.semint.kprime.adapter.strategy.TransformationStrategyYesAdapter
import unibz.cs.semint.kprime.domain.DataSource
import unibz.cs.semint.kprime.domain.Transformation
import unibz.cs.semint.kprime.scenario.sakila.readMeta
import unibz.cs.semint.kprime.scenario.sakila.sakilaDataSource
import unibz.cs.semint.kprime.usecase.current.TransformerHUseCase
import unibz.cs.semint.kprime.usecase.current.TransformerVUseCase
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Applies Optimus to local Sakila Postgres Example.
 */
class OptimusUseCaseTI {

    @Test
    /*
        readSakila
        addFunctionalDeps
        compute trasformationPath
            as decompose vertical(film)
        transformation(newdb,changeset)
            where changeset has: create / drop tables, constraints, mappings
            where newdb has changed: tables, constraints, mappings
        doPhysicalTransformation(sakilaDataSource, trasformationPath)
            where for every transformation in trasformationPath starting from step 0
                create view from transformation.newdb[step].mappings

     */
    fun test_starter() {
        // given
        val database = readMeta(sakilaDataSource())
        if (database==null) {
            Assert.fail("no database to test")
            return
        };
        database.schema
                .addFunctionals("film","film_id --> replacement_cost, rental_duration, rental_rate")

        database.schema
                .addFunctionals("address", "address_id --> address2")

        val rootWorkingDir = System.getenv()["tmp_dir"] ?: "" // "/home/nipe/Temp/"
        val timestampSuffix =  LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_nnnnnnnnnn"))
        val workingDir = rootWorkingDir + timestampSuffix + "/"
        File(workingDir).mkdirs()

        val params = mapOf(
                "workingDir" to workingDir
        )

        val serializerService = XMLSerializerJacksonAdapter()
        val fileIOService = FileIOAdapter()


        fileIOService.writeOnWorkingFilePath(serializerService.prettyDatabase(database), workingDir + "db_original.xml")

        // when
        val transformationPath = OptimusUseCase(
                TransformationStrategyYesAdapter()
        ).addTrasnsformers(listOf(
                TransformerHUseCase(),
                TransformerVUseCase(serializerService,fileIOService)
        )).transfom(database, params)
        // then
        assertNotNull(transformationPath)
        println(transformationPath)
        doPhysicalTransformation(sakilaDataSource(), transformationPath)

    }

    private fun doPhysicalTransformation(sakilaDataSource: DataSource, transformationPath: List<Transformation>) {
        // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}

