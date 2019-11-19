package com.krzykrucz.elesson.currentlesson

import arrow.core.Either
import arrow.core.orNull
import arrow.effects.typeclasses.Duration
import arrow.fx.IO
import com.krzykrucz.elesson.currentlesson.domain.shared.AsyncOutput
import com.krzykrucz.elesson.currentlesson.domain.shared.ClassName
import com.krzykrucz.elesson.currentlesson.domain.shared.FirstName
import com.krzykrucz.elesson.currentlesson.domain.shared.LessonHourNumber
import com.krzykrucz.elesson.currentlesson.domain.shared.NaturalNumber
import com.krzykrucz.elesson.currentlesson.domain.shared.NonEmptyText
import com.krzykrucz.elesson.currentlesson.domain.shared.NumberInRegister
import com.krzykrucz.elesson.currentlesson.domain.shared.Output
import com.krzykrucz.elesson.currentlesson.domain.shared.SecondName
import com.krzykrucz.elesson.currentlesson.domain.shared.StudentRecord
import com.krzykrucz.elesson.currentlesson.domain.shared.Teacher

import java.util.concurrent.TimeUnit

fun newTeacher(firstName: String, secondName: String): Teacher =
    Teacher(
        FirstName(
            NonEmptyText.of(
                firstName
            )!!
        ),
        SecondName(
            NonEmptyText.of(
                secondName
            )!!
        )
    )

fun newStudent(firstName: String, secondName: String, numberInRegister: Int): StudentRecord =
    StudentRecord(
        FirstName(
            NonEmptyText.of(
                firstName
            )!!
        ),
        SecondName(
            NonEmptyText.of(
                secondName
            )!!
        ),
        NumberInRegister(
            NaturalNumber.of(
                numberInRegister
            ).orNull()!!
        )
    )

fun newClassName(name: String): ClassName =
    ClassName(
        NonEmptyText.of(name)!!
    )

fun lessonHourNumberOf(number: Int) = LessonHourNumber.of(number).orNull()!!

fun <T, E> AsyncOutput<E, T>.evaluate() = this.unsafeRunTimed(Duration(5, TimeUnit.SECONDS)).orNull()!!
fun <T, E> IO<Either<E, T>>.evaluate() = this.unsafeRunSync()
fun <T, E> Output<E, T>.getError() = this.swap().orNull()!!
fun <T, E> Output<E, T>.getSuccess() = this.orNull()!!
