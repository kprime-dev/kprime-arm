package unibz.cs.semint.kprime.usecase

import org.junit.Assert
import org.junit.Assert.assertNotNull
import org.junit.Test
import unibz.cs.semint.kprime.adapter.file.FileIOAdapter
import unibz.cs.semint.kprime.adapter.service.XMLSerializerJacksonAdapter
import unibz.cs.semint.kprime.scenario.sakila.readMeta
import unibz.cs.semint.kprime.scenario.sakila.sakilaDataSource

/**
 * Applies Starter to local Sakila Postgres Example.
 */
class OptimusUseCaseTI {

    @Test
    fun test_starter() {
        // given
        val database = readMeta(sakilaDataSource())
        if (database==null) {
            Assert.fail("no database to test")
            return
        };
        // when
        val newdb = OptimusUseCase(
                XMLSerializerJacksonAdapter(),
                FileIOAdapter()
        ).transfom(database)
        // then
        assertNotNull(newdb)
    }

}

