package com.krzykrucz.elesson.currentlesson.domain

import arrow.core.orNull
import arrow.effects.typeclasses.Duration
import com.krzykrucz.elesson.currentlesson.domain.startlesson.ClassName
import com.krzykrucz.elesson.currentlesson.domain.startlesson.FirstName
import com.krzykrucz.elesson.currentlesson.domain.startlesson.LessonHourNumber
import com.krzykrucz.elesson.currentlesson.domain.startlesson.NumberInRegister
import com.krzykrucz.elesson.currentlesson.domain.startlesson.SecondName
import com.krzykrucz.elesson.currentlesson.domain.startlesson.StudentRecord
import com.krzykrucz.elesson.currentlesson.domain.startlesson.Teacher
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
            NumberInRegister(NaturalNumber.of(numberInRegister).orNull()!!)
        )

fun newClassName(name: String): ClassName = ClassName(NonEmptyText.of(name)!!)

fun lessonHourNumberOf(number: Int) = LessonHourNumber.of(number).orNull()!!

fun <T, E> AsyncOutput<T, E>.evaluate() = this.unsafeRunTimed(Duration(5, TimeUnit.SECONDS)).orNull()!!
fun <T, E> Output<T, E>.getError() = this.swap().orNull()!!
fun <T, E> Output<T, E>.getSuccess() = this.orNull()!!