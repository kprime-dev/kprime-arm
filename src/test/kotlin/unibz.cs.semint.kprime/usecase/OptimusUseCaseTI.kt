package unibz.cs.semint.kprime.usecase

import org.junit.Assert
import org.junit.Assert.assertNotNull
import org.junit.Test
import unibz.cs.semint.kprime.adapter.file.FileIOAdapter
import unibz.cs.semint.kprime.adapter.service.XMLSerializerJacksonAdapter
import unibz.cs.semint.kprime.adapter.strategy.TransformationStrategyYesAdapter
import unibz.cs.semint.kprime.scenario.sakila.readMeta
import unibz.cs.semint.kprime.scenario.sakila.sakilaDataSource

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

        // when
        val transformationPath = OptimusUseCase(
                XMLSerializerJacksonAdapter(),
                FileIOAdapter(),
                TransformationStrategyYesAdapter()
        ).transfom(database)
        // then
        assertNotNull(transformationPath)
        //doPhysicalTransformation(sakilaDataSource(), trasformationPath)
    }

}

