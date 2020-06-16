package com.krzykrucz.elesson.currentlesson.startlesson

import arrow.core.right
import com.krzykrucz.elesson.currentlesson.shared.ClassRegistry
import com.krzykrucz.elesson.currentlesson.shared.FirstName
import com.krzykrucz.elesson.currentlesson.shared.NaturalNumber
import com.krzykrucz.elesson.currentlesson.shared.NonEmptyText
import com.krzykrucz.elesson.currentlesson.shared.NumberInRegister
import com.krzykrucz.elesson.currentlesson.shared.SecondName
import com.krzykrucz.elesson.currentlesson.shared.StudentRecord


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
