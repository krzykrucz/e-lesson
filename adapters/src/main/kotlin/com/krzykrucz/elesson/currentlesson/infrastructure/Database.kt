package com.krzykrucz.elesson.currentlesson.infrastructure

import com.krzykrucz.elesson.currentlesson.domain.NaturalNumber
import com.krzykrucz.elesson.currentlesson.domain.NonEmptyText
import com.krzykrucz.elesson.currentlesson.domain.attendance.Attendance
import com.krzykrucz.elesson.currentlesson.domain.startlesson.*
import java.time.LocalDate
import java.util.concurrent.ConcurrentHashMap

class Database {

    companion object {
        private val lessonId1 = lessonIdOf("2019-09-09", 1, "1A")
        private val lessonId2 = lessonIdOf("2019-09-09", 2, "1A")
        private val lessonId3 = lessonIdOf("2019-09-09", 3, "1A")

        private val classRegistryOf1A = ClassRegistry(
                students = listOf(
                        createStudentRecord("Harry", "Potter", 1),
                        createStudentRecord("Tom", "Riddle", 2),
                        createStudentRecord("Hermiona", "Granger", 3),
                        createStudentRecord("Luna", "Lovegood", 4),
                        createStudentRecord("Albus", "Dumbledore", 5)

                ),
                className = classNameOf("1A")
        )

        val STARTED_LESSON_DATABASE: Map<LessonIdentifier, StartedLesson> = ConcurrentHashMap(mutableMapOf(
                lessonId1 to StartedLesson(lessonId1, classRegistryOf1A),
                lessonId2 to StartedLesson(lessonId2, classRegistryOf1A),
                lessonId3 to StartedLesson(lessonId3, classRegistryOf1A)
        ))

        val ATTENDANCE_DATABASE: Map<LessonIdentifier, Attendance> = ConcurrentHashMap()

        private fun lessonIdOf(date: String, number: Int, className: String) =
                LessonIdentifier(LocalDate.parse(date), lessonHourNumberOf(number), classNameOf(className))

        private fun lessonHourNumberOf(number: Int) =
                LessonHourNumber(NaturalNumber.of(number)!!)

        private fun classNameOf(name: String) =
                ClassName(NonEmptyText.of(name)!!)

        private fun createStudentRecord(name: String, surname: String, numberInRegister: Int): StudentRecord =
                StudentRecord(
                        firstName = FirstName(NonEmptyText.of(name)!!),
                        secondName = SecondName(NonEmptyText.of(surname)!!),
                        numberInRegister = NumberInRegister(NaturalNumber.of(numberInRegister)!!)
                )

    }

}