package com.krzykrucz.elesson.currentlesson.infrastructure

import com.krzykrucz.elesson.currentlesson.domain.NaturalNumber
import com.krzykrucz.elesson.currentlesson.domain.NonEmptyText
import com.krzykrucz.elesson.currentlesson.domain.attendance.Attendance
import com.krzykrucz.elesson.currentlesson.domain.attendance.AttendanceList
import com.krzykrucz.elesson.currentlesson.domain.attendance.LessonTime
import com.krzykrucz.elesson.currentlesson.domain.attendance.NotCompletedAttendance
import com.krzykrucz.elesson.currentlesson.domain.startlesson.*
import java.time.LocalDate
import java.time.LocalTime
import java.util.concurrent.ConcurrentHashMap

class Database {

    companion object {
        private val lessonId1 = lessonIdOf("2019-09-09", 1, "1A")
        private val lessonId2 = lessonIdOf("2019-09-09", 2, "1A")
        private val lessonId3 = lessonIdOf("2019-09-09", 3, "1A")

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

        private fun lessonTimeOf(time: String): LessonTime =
                LocalTime.parse(time)

        private fun startedLessonOf(lessonIdentifier: LessonIdentifier): StartedLesson =
                StartedLesson(
                        lessonIdentifier,
                        classRegistryOf1A
                )

        private fun notCompletedAttendanceOf(date: String, number: Int, className: String): NotCompletedAttendance =
                NotCompletedAttendance(AttendanceList(
                        className = classNameOf(className),
                        date = LocalDate.parse(date),
                        lessonHourNumber = lessonHourNumberOf(number)
                ))

        fun lessonIdOf(date: String, number: Int, className: String) =
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