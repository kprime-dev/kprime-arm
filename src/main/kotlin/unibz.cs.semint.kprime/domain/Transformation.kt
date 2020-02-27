package unibz.cs.semint.kprime.domain

import unibz.cs.semint.kprime.domain.ddl.Database
import unibz.cs.semint.kprime.domain.dml.ChangeSet

data class Transformation(val changeset : ChangeSet, val newdb : Database)