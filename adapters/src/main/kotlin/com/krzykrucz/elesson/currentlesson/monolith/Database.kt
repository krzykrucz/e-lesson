package com.krzykrucz.elesson.currentlesson.monolith

import arrow.core.toOption
import arrow.data.OptionT
import arrow.effects.IO
import arrow.effects.extensions.io.applicative.applicative
import arrow.effects.extensions.io.functor.functor
import com.krzykrucz.elesson.currentlesson.attendance.domain.Attendance
import com.krzykrucz.elesson.currentlesson.attendance.domain.AttendanceList
import com.krzykrucz.elesson.currentlesson.attendance.domain.NotCompletedAttendance
import com.krzykrucz.elesson.currentlesson.shared.ClassName
import com.krzykrucz.elesson.currentlesson.shared.ClassRegistry
import com.krzykrucz.elesson.currentlesson.shared.FirstName
import com.krzykrucz.elesson.currentlesson.shared.LessonHourNumber
import com.krzykrucz.elesson.currentlesson.shared.LessonIdentifier
import com.krzykrucz.elesson.currentlesson.shared.NaturalNumber
import com.krzykrucz.elesson.currentlesson.shared.NonEmptyText
import com.krzykrucz.elesson.currentlesson.shared.NumberInRegister
import com.krzykrucz.elesson.currentlesson.shared.SecondName
import com.krzykrucz.elesson.currentlesson.shared.StudentRecord
import com.krzykrucz.elesson.currentlesson.startlesson.domain.StartedLesson
import java.time.LocalDate
import java.util.concurrent.ConcurrentHashMap

fun StartedLesson.toNotCompletedAttendance() =
        NotCompletedAttendance(
                attendance = AttendanceList(
                        className = this.id.className,
                        date = this.id.date,
                        lessonHourNumber = this.id.lessonHourNumber
                ),
                classRegistry = clazz
        )

class Database {

    companion object {

        fun fetchAttendance(lessonIdentifier: LessonIdentifier) =
                OptionT.fromOption(IO.applicative(), Database.ATTENDANCE_DATABASE[lessonIdentifier].toOption())


        fun fetchStartedLesson(lessonIdentifier: LessonIdentifier) =
                OptionT.fromOption(IO.applicative(), Database.STARTED_LESSON_DATABASE[lessonIdentifier].toOption())

        fun fetchStartedLessonAsAttendance(lessonIdentifier: LessonIdentifier) =
                fetchStartedLesson(lessonIdentifier)
                        .map(IO.functor()) { it.toNotCompletedAttendance() }

        private val lessonId1 = lessonIdOf("2019-09-09", 1, "1A")

        private val classRegistryOf1A = ClassRegistry(
                students = listOf(
                        createStudentRecord("Harry", "Potter", 1),
                        createStudentRecord("Tom", "Riddle", 2)

                ),
                className = classNameOf("1A")
        )

        val STARTED_LESSON_DATABASE: ConcurrentHashMap<LessonIdentifier, StartedLesson> = ConcurrentHashMap(mutableMapOf(
                lessonId1 to startedLessonOf(lessonId1)
        ))

        val ATTENDANCE_DATABASE: ConcurrentHashMap<LessonIdentifier, Attendance> = ConcurrentHashMap(mutableMapOf(
        ))

        private fun startedLessonOf(lessonIdentifier: LessonIdentifier): StartedLesson =
                StartedLesson(
                        lessonIdentifier,
                        classRegistryOf1A
                )

        fun lessonIdOf(date: String, number: Int, className: String) =
                LessonIdentifier(LocalDate.parse(date), lessonHourNumberOf(number), classNameOf(className))

        private fun lessonHourNumberOf(number: Int) =
                LessonHourNumber.of(number).orNull()!!

        private fun classNameOf(name: String) =
                ClassName(NonEmptyText.of(name)!!)

        private fun createStudentRecord(name: String, surname: String, numberInRegister: Int): StudentRecord =
                StudentRecord(
                        firstName = FirstName(NonEmptyText.of(name)!!),
                        secondName = SecondName(NonEmptyText.of(surname)!!),
                        numberInRegister = NaturalNumber.of(numberInRegister)
                                .map(::NumberInRegister)
                                .orNull()!!
                )

    }

}