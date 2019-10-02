package com.krzykrucz.elesson.currentlesson.domain

import arrow.core.orNull
import arrow.effects.typeclasses.Duration
import com.krzykrucz.elesson.currentlesson.domain.startlesson.*
import java.util.concurrent.TimeUnit

fun newTeacher(firstName: String, secondName: String): Teacher =
        Teacher(
                FirstName(NonEmptyText.of(firstName)!!),
                SecondName(NonEmptyText.of(secondName)!!)
        )

fun newStudent(firstName: String, secondName: String, numberInRegister: Int): StudentRecord =
        StudentRecord(
                FirstName(NonEmptyText.of(firstName)!!),
                SecondName(NonEmptyText.of(secondName)!!),
                NumberInRegister(NaturalNumber.of(numberInRegister)!!)
        )

fun newClassName(name: String): ClassName = ClassName(NonEmptyText.of(name)!!)

fun lessonHourNumberOf(number: Int) = LessonHourNumber(NaturalNumber.of(number)!!)

fun <T, E> AsyncOutput<T, E>.evaluate() = this.unsafeRunTimed(Duration(5, TimeUnit.SECONDS)).orNull()!!
fun <T, E> Output<T, E>.getError() = this.swap().orNull()!!
fun <T, E> Output<T, E>.getSuccess() = this.orNull()!!