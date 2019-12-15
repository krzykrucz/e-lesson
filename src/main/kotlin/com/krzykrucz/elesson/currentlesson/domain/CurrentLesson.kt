package com.krzykrucz.elesson.currentlesson.domain

import arrow.core.NonEmptyList
import arrow.core.Option
import arrow.core.toOption
import com.virtuslab.basetypes.refined.NonEmptyText
import com.virtuslab.basetypes.result.Result
import com.virtuslab.basetypes.result.flatMap
import com.virtuslab.basetypes.result.map
import com.virtuslab.basetypes.result.pairWith
import com.virtuslab.basetypes.result.arrow.AsyncResult
import com.virtuslab.basetypes.result.arrow.flatMapResult
import com.virtuslab.basetypes.result.arrow.flatMapSuccess
import com.virtuslab.basetypes.result.arrow.mapSuccess
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

typealias StartLesson = (Teacher, AttemptedLessonStartTime) -> Result<StartedLesson, StartLessonError> // TODO change to AsyncResult

typealias StartLessonWithDependencies = (CheckSchedule, FetchClassRegistry, CheckLessonStartTime) -> StartLesson

val startLesson: StartLessonWithDependencies = { checkSchedule, fetchClassRegistry, checkLessonStartTime ->
    { teacher, lessonStartTime ->
        checkSchedule(teacher, lessonStartTime)
            .flatMap { checkLessonStartTime(it, lessonStartTime) }
            .flatMap { fetchClassRegistry(it.className).pairWith(it) }
            .map { (registry, lesson) ->
                StartedLesson(teacher, lesson.startTime, lesson.hourNumber, registry)
            }
    }
}

//dependencies

class ExternalError(val msg: String) : Exception()
typealias CheckSchedule = (Teacher, AttemptedLessonStartTime) -> Result<ScheduledLesson, StartLessonError> // TODO change to AsyncResult
typealias FetchClassRegistry = (ClassName) -> Result<ClassRegistry, StartLessonError> // TODO change to AsyncResult

data class LessonAboutToStart(
    val className: ClassName,
    val startTime: LessonStartTime,
    val hourNumber: LessonHourNumber
)
typealias CheckLessonStartTime = (ScheduledLesson, AttemptedLessonStartTime) -> Result<LessonAboutToStart, StartLessonError>

val checkTime: CheckLessonStartTime = { lesson, startTime ->
    when {
        startTime.dateTime.isBefore(lesson.scheduledTime) -> Result.error(StartLessonError.StartingTooEarlyOrTooLate)
        startTime.dateTime.isAfter(lesson.scheduledTime.plusMinutes(44)) -> Result.error(StartLessonError.StartingTooEarlyOrTooLate)
        else -> Result.success(lesson)
    }.map {
        LessonAboutToStart(it.className, LessonStartTime(startTime.dateTime), it.hourNumber)
    }
}

//errors

sealed class StartLessonError : RuntimeException() {
    object ClassRegistryUnavailable : StartLessonError()
    object StartingTooEarlyOrTooLate : StartLessonError()
    object ExternalError : StartLessonError()
}