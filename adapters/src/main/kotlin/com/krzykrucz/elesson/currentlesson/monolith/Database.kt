package com.krzykrucz.elesson.currentlesson.monolith

import arrow.core.Option
import com.krzykrucz.elesson.currentlesson.attendance.domain.Attendance
import com.krzykrucz.elesson.currentlesson.attendance.domain.CheckedAttendanceList
import com.krzykrucz.elesson.currentlesson.attendance.domain.IncompleteAttendanceList
import com.krzykrucz.elesson.currentlesson.preparedness.domain.api.StudentsUnpreparedForLesson
import com.krzykrucz.elesson.currentlesson.shared.ClassName
import com.krzykrucz.elesson.currentlesson.shared.ClassRegistry
import com.krzykrucz.elesson.currentlesson.shared.FirstName
import com.krzykrucz.elesson.currentlesson.shared.InProgressLesson
import com.krzykrucz.elesson.currentlesson.shared.LessonAfterAttendance
import com.krzykrucz.elesson.currentlesson.shared.LessonHourNumber
import com.krzykrucz.elesson.currentlesson.shared.LessonIdentifier
import com.krzykrucz.elesson.currentlesson.shared.NaturalNumber
import com.krzykrucz.elesson.currentlesson.shared.NonEmptyText
import com.krzykrucz.elesson.currentlesson.shared.NumberInRegister
import com.krzykrucz.elesson.currentlesson.shared.SecondName
import com.krzykrucz.elesson.currentlesson.shared.StartedLesson
import com.krzykrucz.elesson.currentlesson.shared.StudentRecord
import com.krzykrucz.elesson.currentlesson.topic.domain.LessonTopic
import java.time.LocalDate
import java.util.concurrent.ConcurrentHashMap
import arrow.core.Option.Companion as Option1


data class PersistentCurrentLesson(
    val lessonId: LessonIdentifier,
    val classRegistry: ClassRegistry,
    val lessonTopic: LessonTopic? = null,
    val attendance: Attendance = IncompleteAttendanceList(),
    val unpreparedStudents: StudentsUnpreparedForLesson = StudentsUnpreparedForLesson()
) {

    fun toLessonAfterAttendance(): Option<LessonAfterAttendance> {
        if (this.lessonTopic == null && this.attendance is CheckedAttendanceList) {
            return LessonAfterAttendance(
                this.lessonId,
                this.attendance,
                this.unpreparedStudents
            ).let(Option1::just)
        }
        return Option1.empty()
    }

    fun toStartedLesson(): Option<StartedLesson> {
        if (this.attendance is IncompleteAttendanceList) {
            return StartedLesson(
                this.lessonId,
                this.classRegistry
            ).let(Option1::just)
        }
        return Option1.empty()
    }

    fun toLessonInProgress(): Option<InProgressLesson> {
        if (this.lessonTopic != null) {
            return InProgressLesson(this.lessonTopic)
                .let(Option1::just)
        }
        return Option1.empty()
    }


}

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
