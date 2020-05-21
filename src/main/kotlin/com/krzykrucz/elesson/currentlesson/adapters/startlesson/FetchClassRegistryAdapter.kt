package com.krzykrucz.elesson.currentlesson.adapters.startlesson

import arrow.core.right
import com.krzykrucz.elesson.currentlesson.domain.shared.ClassRegistry
import com.krzykrucz.elesson.currentlesson.domain.shared.FirstName
import com.krzykrucz.elesson.currentlesson.domain.shared.NaturalNumber
import com.krzykrucz.elesson.currentlesson.domain.shared.NonEmptyText
import com.krzykrucz.elesson.currentlesson.domain.shared.NumberInRegister
import com.krzykrucz.elesson.currentlesson.domain.shared.SecondName
import com.krzykrucz.elesson.currentlesson.domain.shared.StudentRecord
import com.krzykrucz.elesson.currentlesson.domain.startlesson.FetchClassRegistry


internal val fetchClassRegistryAdapter: FetchClassRegistry = { className ->
    listOf(
        StudentRecord(
            FirstName(
                NonEmptyText.of(
                    "Harry"
                )!!
            ),
            SecondName(
                NonEmptyText.of(
                    "Potter"
                )!!
            ),
            NumberInRegister(NaturalNumber.ONE)
        ),
        StudentRecord(
            FirstName(
                NonEmptyText.of(
                    "Hermione"
                )!!
            ),
            SecondName(
                NonEmptyText.of(
                    "Granger"
                )!!
            ),
            NumberInRegister(NaturalNumber.TWO)
        )
    )
        .let { ClassRegistry(it, className) }
        .right()

}
