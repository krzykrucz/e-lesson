package com.krzykrucz.elesson.currentlesson.domain

import arrow.core.NonEmptyList
import arrow.core.Option
import arrow.core.toOption
import com.virtuslab.basetypes.refined.NonEmptyText
import com.virtuslab.basetypes.result.Result
import com.virtuslab.basetypes.result.map
import com.virtuslab.basetypes.result.flatMap
import java.time.LocalDateTime
import java.time.LocalTime


data class Teacher(val name: NonEmptyText)

data class AttemptedLessonStartTime(val dateTime: LocalDateTime)

data class LessonStartTime(val dateTime: LocalDateTime)

data class StartedLesson(
    val teacher: Teacher,
    val startTime: LessonStartTime,
    val hourNumber: LessonHourNumber,
    val classRegistry: ClassRegistry
)

typealias Time = LocalTime

sealed class LessonHourNumber(val time: Time) {

    object One : LessonHourNumber(Time.parse("08:00"))
    object Two : LessonHourNumber(Time.parse("08:55"))
    object Three : LessonHourNumber(Time.parse("09:50"))

    companion object {
        fun of(number: Int): Option<LessonHourNumber> = when (number) {
            1 -> One.toOption()
            2 -> Two.toOption()
            3 -> Three.toOption()
            else -> Option.empty()
        }
    }
}

data class ClassName(val name: NonEmptyText)

data class StudentRecord(val firstName: NonEmptyText, val secondName: NonEmptyText)
typealias StudentList = NonEmptyList<StudentRecord>

data class ClassRegistry(
    val className: ClassName,
    val studentList: StudentList
)

typealias ScheduledTime = LocalDateTime

data class ScheduledLesson(
    val className: ClassName,
    val scheduledTime: ScheduledTime,
    val hourNumber: LessonHourNumber
)

data class LessonAboutToStart(
    val className: ClassName,
    val startTime: LessonStartTime,
    val hourNumber: LessonHourNumber
)

typealias StartLesson = (Teacher, AttemptedLessonStartTime) -> StartedLesson // TODO change to return Result<Success, Failure>

typealias StartLessonWithDependencies = (CheckSchedule, FetchClassRegistry, CheckLessonStartTime) -> StartLesson

val startLesson: StartLessonWithDependencies = { checkSchedule, fetchClassRegistry, checkLessonStartTime ->
    { teacher, lessonStartTime ->
        val scheduledLesson = checkSchedule(teacher, lessonStartTime)
        val lessonBeforeStart = checkLessonStartTime(scheduledLesson, lessonStartTime)
        val registry = fetchClassRegistry(lessonBeforeStart.className)
        StartedLesson(teacher, lessonBeforeStart.startTime, lessonBeforeStart.hourNumber, registry)
    }
}

//dependencies

typealias CheckSchedule = (Teacher, AttemptedLessonStartTime) -> ScheduledLesson // TODO change to return Result<Success, Failure>
typealias FetchClassRegistry = (ClassName) -> ClassRegistry // TODO change to return Result<Success, Failure>

typealias CheckLessonStartTime = (ScheduledLesson, AttemptedLessonStartTime) -> LessonAboutToStart // TODO change to return Result<Success, Failure>

val checkTime: CheckLessonStartTime = { lesson, startTime ->
    when {
        startTime.dateTime.isBefore(lesson.scheduledTime) -> throw StartLessonError.StartingTooEarlyOrTooLate
        startTime.dateTime.isAfter(lesson.scheduledTime.plusMinutes(44)) -> throw StartLessonError.StartingTooEarlyOrTooLate
        else -> lesson
    }.let {
        LessonAboutToStart(it.className, LessonStartTime(startTime.dateTime), it.hourNumber)
    }
}

//errors

sealed class StartLessonError : RuntimeException() {
    object ClassRegistryUnavailable : StartLessonError()
    object StartingTooEarlyOrTooLate : StartLessonError()
    object ExternalError : StartLessonError()
}