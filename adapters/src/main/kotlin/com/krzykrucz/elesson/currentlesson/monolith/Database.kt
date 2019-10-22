package com.krzykrucz.elesson.currentlesson.monolith

import com.krzykrucz.elesson.currentlesson.attendance.domain.Attendance
import com.krzykrucz.elesson.currentlesson.attendance.domain.IncompleteAttendanceList
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
import java.time.LocalDate
import java.util.concurrent.ConcurrentHashMap


data class PersistentCurrentLesson(
        val lessonId: LessonIdentifier,
        val classRegistry: ClassRegistry,
        val attendance: Attendance = IncompleteAttendanceList()
)

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

        val LESSON_DATABASE: ConcurrentHashMap<LessonIdentifier, PersistentCurrentLesson> = ConcurrentHashMap(mutableMapOf(
                lessonId1 to PersistentCurrentLesson(
                        lessonId1,
                        classRegistryOf1A
                )
        ))

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