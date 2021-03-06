package com.krzykrucz.elesson.currentlesson.infrastructure

import arrow.core.Option
import com.krzykrucz.elesson.currentlesson.domain.attendance.Attendance
import com.krzykrucz.elesson.currentlesson.domain.attendance.CheckedAttendanceList
import com.krzykrucz.elesson.currentlesson.domain.attendance.IncompleteAttendanceList
import com.krzykrucz.elesson.currentlesson.domain.preparedness.domain.api.StudentsUnpreparedForLesson
import com.krzykrucz.elesson.currentlesson.domain.shared.ClassName
import com.krzykrucz.elesson.currentlesson.domain.shared.ClassRegistry
import com.krzykrucz.elesson.currentlesson.domain.shared.FirstName
import com.krzykrucz.elesson.currentlesson.domain.shared.InProgressLesson
import com.krzykrucz.elesson.currentlesson.domain.shared.LessonAfterAttendance
import com.krzykrucz.elesson.currentlesson.domain.shared.LessonHourNumber
import com.krzykrucz.elesson.currentlesson.domain.shared.LessonIdentifier
import com.krzykrucz.elesson.currentlesson.domain.shared.LessonStatus
import com.krzykrucz.elesson.currentlesson.domain.shared.LessonSubject
import com.krzykrucz.elesson.currentlesson.domain.shared.LessonTopic
import com.krzykrucz.elesson.currentlesson.domain.shared.NaturalNumber
import com.krzykrucz.elesson.currentlesson.domain.shared.NonEmptyText
import com.krzykrucz.elesson.currentlesson.domain.shared.NumberInRegister
import com.krzykrucz.elesson.currentlesson.domain.shared.Scheduled
import com.krzykrucz.elesson.currentlesson.domain.shared.SecondName
import com.krzykrucz.elesson.currentlesson.domain.shared.Semester
import com.krzykrucz.elesson.currentlesson.domain.shared.StartedLesson
import com.krzykrucz.elesson.currentlesson.domain.shared.StudentRecord
import com.krzykrucz.elesson.currentlesson.domain.shared.WinterSemester
import java.time.LocalDate
import java.util.concurrent.ConcurrentHashMap
import arrow.core.Option.Companion as Option1


data class PersistentCurrentLesson(
    val lessonId: LessonIdentifier,
    val classRegistry: ClassRegistry,
    val lessonTopic: Option<LessonTopic> = Option.empty(),
    val attendance: Attendance = IncompleteAttendanceList(),
    val semester: Semester = WinterSemester,
    val subject: LessonSubject,
    val status: LessonStatus,
    val unpreparedStudents: StudentsUnpreparedForLesson = StudentsUnpreparedForLesson()
) {

    fun toLessonAfterAttendance(): Option<LessonAfterAttendance> {
        if (this.lessonTopic.isEmpty() && this.attendance is CheckedAttendanceList) {
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
                this.classRegistry,
                this.subject
            ).let(Option1::just)
        }
        return Option1.empty()
    }

    fun toLessonInProgress(): Option<InProgressLesson> =
        this.lessonTopic
            .map { lessonTopic ->
                InProgressLesson(
                    lessonId,
                    lessonTopic
                )
            }

}

class Database {

    companion object {

        private val lessonId1 =
            lessonIdOf("2019-09-09", 1, "1A")

        private val classRegistryOf1A = ClassRegistry(
            students = listOf(
                createStudentRecord(
                    "Harry",
                    "Potter",
                    1
                ),
                createStudentRecord(
                    "Tom",
                    "Riddle",
                    2
                )

            ),
            className = classNameOf("1A")
        )

        val LESSON_DATABASE: ConcurrentHashMap<LessonIdentifier, PersistentCurrentLesson> = ConcurrentHashMap(mutableMapOf(
            lessonId1 to PersistentCurrentLesson(
                lessonId1,
                classRegistryOf1A,
                semester = WinterSemester,
                subject = LessonSubject(
                    NonEmptyText(
                        "Defense from dark arts"
                    )
                ),
                status = Scheduled
            )
        ))

        fun lessonIdOf(date: String, number: Int, className: String) =
            LessonIdentifier(
                LocalDate.parse(date),
                lessonHourNumberOf(number),
                classNameOf(className)
            )

        private fun lessonHourNumberOf(number: Int) =
            LessonHourNumber.of(number).orNull()!!

        private fun classNameOf(name: String) =
            ClassName(
                NonEmptyText.of(
                    name
                )!!
            )

        private fun createStudentRecord(name: String, surname: String, numberInRegister: Int): StudentRecord =
            StudentRecord(
                firstName = FirstName(
                    NonEmptyText.of(
                        name
                    )!!
                ),
                secondName = SecondName(
                    NonEmptyText.of(
                        surname
                    )!!
                ),
                numberInRegister = NaturalNumber.of(numberInRegister)
                    .map(::NumberInRegister)
                    .orNull()!!
            )

    }

}
