package com.krzykrucz.elesson.currentlesson.startlesson.adapters.registry

import arrow.core.Either
import com.krzykrucz.elesson.currentlesson.shared.AsyncOutput
import com.krzykrucz.elesson.currentlesson.shared.ClassName
import com.krzykrucz.elesson.currentlesson.shared.ClassRegistry
import com.krzykrucz.elesson.currentlesson.shared.FirstName
import com.krzykrucz.elesson.currentlesson.shared.NaturalNumber
import com.krzykrucz.elesson.currentlesson.shared.NonEmptyText
import com.krzykrucz.elesson.currentlesson.shared.NumberInRegister
import com.krzykrucz.elesson.currentlesson.shared.SecondName
import com.krzykrucz.elesson.currentlesson.shared.StudentRecord
import com.krzykrucz.elesson.currentlesson.startlesson.domain.FetchClassRegistry


class ERegisterClient : FetchClassRegistry {
    override fun invoke(p1: ClassName): AsyncOutput<Throwable, ClassRegistry> =
        listOf(
                StudentRecord(
                        FirstName(NonEmptyText.of("Harry")!!),
                        SecondName(NonEmptyText.of("Potter")!!),
                        NumberInRegister(NaturalNumber.ONE)
                ),
                StudentRecord(
                        FirstName(NonEmptyText.of("Hermione")!!),
                        SecondName(NonEmptyText.of("Granger")!!),
                        NumberInRegister(NaturalNumber.TWO)
                )
        )
            .let { ClassRegistry(it, p1) }
            .let { AsyncOutput.just(Either.right(it)) }

}