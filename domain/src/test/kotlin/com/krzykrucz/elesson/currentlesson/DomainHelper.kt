package com.krzykrucz.elesson.currentlesson

import arrow.core.orNull
import arrow.effects.typeclasses.Duration
import com.krzykrucz.elesson.currentlesson.shared.AsyncOutput
import com.krzykrucz.elesson.currentlesson.shared.ClassName
import com.krzykrucz.elesson.currentlesson.shared.FirstName
import com.krzykrucz.elesson.currentlesson.shared.LessonHourNumber
import com.krzykrucz.elesson.currentlesson.shared.NaturalNumber
import com.krzykrucz.elesson.currentlesson.shared.NonEmptyText
import com.krzykrucz.elesson.currentlesson.shared.NumberInRegister
import com.krzykrucz.elesson.currentlesson.shared.Output
import com.krzykrucz.elesson.currentlesson.shared.SecondName
import com.krzykrucz.elesson.currentlesson.shared.StudentRecord
import com.krzykrucz.elesson.currentlesson.shared.Teacher
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