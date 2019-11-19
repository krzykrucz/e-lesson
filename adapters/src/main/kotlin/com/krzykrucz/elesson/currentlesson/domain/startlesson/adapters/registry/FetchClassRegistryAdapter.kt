package com.krzykrucz.elesson.currentlesson.domain.startlesson.adapters.registry

import arrow.core.Either
import arrow.core.right
import arrow.fx.IO
import com.krzykrucz.elesson.currentlesson.domain.shared.ClassName
import com.krzykrucz.elesson.currentlesson.domain.shared.ClassRegistry
import com.krzykrucz.elesson.currentlesson.domain.shared.FirstName
import com.krzykrucz.elesson.currentlesson.domain.shared.NaturalNumber
import com.krzykrucz.elesson.currentlesson.domain.shared.NonEmptyText
import com.krzykrucz.elesson.currentlesson.domain.shared.NumberInRegister
import com.krzykrucz.elesson.currentlesson.domain.shared.SecondName
import com.krzykrucz.elesson.currentlesson.domain.shared.StudentRecord
import com.krzykrucz.elesson.currentlesson.shared.*
import com.krzykrucz.elesson.currentlesson.domain.startlesson.FetchClassRegistry
import com.krzykrucz.elesson.currentlesson.domain.startlesson.StartLessonError


class ERegisterClient : FetchClassRegistry {
    override fun invoke(p1: ClassName): IO<Either<StartLessonError, ClassRegistry>> =
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
            .let { ClassRegistry(it, p1) }
            .let { IO.just(it.right()) }

}
