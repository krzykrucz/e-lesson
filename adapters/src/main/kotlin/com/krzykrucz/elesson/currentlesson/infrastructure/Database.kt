package com.krzykrucz.elesson.currentlesson.infrastructure

import com.krzykrucz.elesson.currentlesson.domain.NaturalNumber
import com.krzykrucz.elesson.currentlesson.domain.NonEmptyText
import com.krzykrucz.elesson.currentlesson.domain.attendance.Attendance
import com.krzykrucz.elesson.currentlesson.domain.startlesson.*
import com.krzykrucz.elesson.currentlesson.domain.topic.domain.InProgressLesson
import java.time.LocalDate
import java.util.concurrent.ConcurrentHashMap

class Database {

    companion object {
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

        val IN_PROGRESS_LESSON_DATABASE: ConcurrentHashMap<LessonIdentifier, InProgressLesson> = ConcurrentHashMap(mutableMapOf())

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