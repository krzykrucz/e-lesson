package com.krzykrucz.elesson.currentlesson.startlesson.adapters.registry

import arrow.core.Either
import arrow.core.right
import arrow.fx.IO
import com.krzykrucz.elesson.currentlesson.shared.*
import com.krzykrucz.elesson.currentlesson.startlesson.domain.FetchClassRegistry
import com.krzykrucz.elesson.currentlesson.startlesson.domain.StartLessonError


class ERegisterClient : FetchClassRegistry {
    override fun invoke(p1: ClassName): IO<Either<StartLessonError, ClassRegistry>> =
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
            .let { IO.just(it.right()) }

}
