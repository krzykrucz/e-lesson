package com.krzykrucz.elesson.currentlesson.domain

import arrow.core.NonEmptyList
import com.krzykrucz.elesson.currentlesson.domain.base.Time
import com.krzykrucz.elesson.currentlesson.domain.base.timeOf
import com.virtuslab.basetypes.refined.NaturalNumber
import com.virtuslab.basetypes.refined.NonEmptyText
import com.virtuslab.basetypes.result.Result
import com.virtuslab.basetypes.result.arrow.AsyncResult
import com.virtuslab.basetypes.result.arrow.flatMapResult
import com.virtuslab.basetypes.result.arrow.flatMapSuccess
import com.virtuslab.basetypes.result.arrow.mapSuccess
import com.virtuslab.basetypes.result.map
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

// types

data class Teacher(
    val firstName: FirstName,
    val secondName: SecondName
)

inline class FirstName(val name: NonEmptyText)

inline class SecondName(val name: NonEmptyText)

inline class LessonStartTime(val time: LocalDateTime)

data class TimetabledLesson(
    val period: Period,
    val teacher: Teacher,
    val classGroupName: ClassGroupName,
    val subject: Subject
)


sealed class Period(val start: Time) {
    object First : Period(timeOf("08:55am"))
    object Second : Period(timeOf("09:50am"))
    object Third : Period(timeOf("10:55am"))

}

data class ClassGroupName(
    val year: Year,
    val groupName: NonEmptyText
)

enum class Year {
    `7`,
    `8`,
    `9`,
    `10`,
    `11`

}

sealed class Subject
object English : Subject()
object Maths : Subject()

data class LessonAboutToStart(
    val period: Period,
    val classGroupName: ClassGroupName,
    val subject: Subject
)


inline class Register(val pupils: NonEmptyList<PupilEntry>)

data class PupilEntry(
    val ordinal: RegisterOrdinal,
    val firstName: FirstName,
    val secondName: SecondName
)

data class RegisterOrdinal(val number: NaturalNumber)

data class StartedLesson(
    val date: LocalDate,
    val period: Period,
    val classGroupName: ClassGroupName,
    val subject: Subject,
    val pupilRegister: Register
)

// workflows

typealias AttemptedLessonStartTime = LessonStartTime

typealias StartLesson = (Teacher, AttemptedLessonStartTime) -> AsyncResult<StartedLesson, StartingLessonFailure>

sealed class StartingLessonFailure {
    object RegisterUnavailable : StartingLessonFailure()
    object StartingTooEarlyOrTooLate : StartingLessonFailure()
    object WrongTeacher : StartingLessonFailure()
    object ExternalError : StartingLessonFailure()
}

typealias CheckLessonStartTime = (TimetabledLesson, AttemptedLessonStartTime) -> Result<LessonAboutToStart, StartingLessonFailure>
typealias CheckSchedule = (Teacher, AttemptedLessonStartTime) -> AsyncResult<TimetabledLesson, StartingLessonFailure>

typealias FetchClassRegistry = (ClassGroupName) -> AsyncResult<Register, StartingLessonFailure>

typealias StartLessonWithDependencies = (CheckSchedule, FetchClassRegistry, CheckLessonStartTime) -> StartLesson

val startLesson: StartLessonWithDependencies = { checkSchedule, fetchClassRegistry, checkLessonStartTime ->
    { teacher, lessonStartTime ->
        checkSchedule(teacher, lessonStartTime)
            .flatMapResult { checkLessonStartTime(it, lessonStartTime) }
            .flatMapSuccess { fetchClassRegistry(it.classGroupName).pairWith(it) }
            .mapSuccess { (register, lesson) ->
                StartedLesson(LocalDate.now(), lesson.period, lesson.classGroupName, lesson.subject, register)
            }
    }
}

val checkTime: CheckLessonStartTime = { lesson, startTime ->
    val timetabledTime = LocalDateTime.of(LocalDate.now(), lesson.period.start)
    when {
        startTime.time.isBefore(timetabledTime) -> Result.error(StartingLessonFailure.StartingTooEarlyOrTooLate)
        startTime.time.isAfter(timetabledTime.plusMinutes(44)) -> Result.error(StartingLessonFailure.StartingTooEarlyOrTooLate)
        else -> Result.success(lesson)
    }.map {
        LessonAboutToStart(it.period, it.classGroupName, it.subject)
    }
}
