package com.krzykrucz.elesson.currentlesson.domain

import arrow.effects.typeclasses.Async
import arrow.effects.typeclasses.Duration
import java.util.concurrent.TimeUnit

fun newTeacher(firstName: String, secondName: String): Teacher =
        Teacher(
                FirstName(NonEmptyText.of(firstName)!!),
                SecondName(NonEmptyText.of(secondName)!!)
        )

fun newStudent(firstName: String, secondName: String, numberInRegister: Int): Student =
        Student(
                FirstName(NonEmptyText.of(firstName)!!),
                SecondName(NonEmptyText.of(secondName)!!),
                NumberInRegister(NaturalNumber.of(numberInRegister)!!)
        )

fun newClassName(name: String): ClassName = ClassName(NonEmptyText.of(name)!!)

fun lessonHourNumberOf(number: Int) = LessonHourNumber(NaturalNumber.of(number)!!)

fun<T> AsyncOutput<T>.evaluate() = this.unsafeRunTimed(Duration(5, TimeUnit.SECONDS)).orNull()!!