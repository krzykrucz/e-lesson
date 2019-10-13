package com.krzykrucz.elesson.currentlesson.startlesson

import arrow.core.Either
import com.krzykrucz.elesson.currentlesson.domain.AsyncOutput
import com.krzykrucz.elesson.currentlesson.domain.NaturalNumber
import com.krzykrucz.elesson.currentlesson.domain.NonEmptyText
import com.krzykrucz.elesson.currentlesson.domain.startlesson.ClassName
import com.krzykrucz.elesson.currentlesson.domain.startlesson.ClassRegistry
import com.krzykrucz.elesson.currentlesson.domain.startlesson.FetchClassRegistry
import com.krzykrucz.elesson.currentlesson.domain.startlesson.FirstName
import com.krzykrucz.elesson.currentlesson.domain.startlesson.NumberInRegister
import com.krzykrucz.elesson.currentlesson.domain.startlesson.SecondName
import com.krzykrucz.elesson.currentlesson.domain.startlesson.StudentRecord


class ERegisterClient : FetchClassRegistry {
    override fun invoke(p1: ClassName): AsyncOutput<ClassRegistry, Throwable> =
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