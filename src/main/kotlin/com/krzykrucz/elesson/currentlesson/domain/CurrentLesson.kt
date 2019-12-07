package com.krzykrucz.elesson.currentlesson.domain

import arrow.core.NonEmptyList
import arrow.core.Option
import arrow.core.toOption
import com.virtuslab.basetypes.refined.NonEmptyText
import java.time.Duration
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

typealias StartLesson = (Teacher, AttemptedLessonStartTime) -> StartedLesson

// TODO use this
typealias StartLessonWithDependencies = (Any, Any) -> Unit

val startLesson: StartLesson = // TODO
    { checkSchedule, fetchClassRegistry, teacher, lessonStartTime ->
        val scheduledLesson = checkSchedule(teacher, lessonStartTime)
        val scheduledTime = scheduledLesson.scheduledTime
        val startTime = lessonStartTime.dateTime
        val checkedLessonStartTime =
            if (startTime.isBefore(scheduledTime)
                or startTime.isAfter(scheduledTime + Duration.ofMinutes(45))) {
                throw StartLessonError.StartingTooEarlyOrTooLate
            } else {
                LessonStartTime(lessonStartTime.dateTime)
            }
        val registry = fetchClassRegistry(scheduledLesson.className)
        StartedLesson(teacher, checkedLessonStartTime, scheduledLesson.hourNumber, registry)
    }


//dependencies

typealias CheckSchedule = (Teacher, AttemptedLessonStartTime) -> ScheduledLesson
typealias FetchClassRegistry = (ClassName) -> ClassRegistry

//errors

sealed class StartLessonError : RuntimeException() {
    object ClassRegistryUnavailable : StartLessonError()
    object StartingTooEarlyOrTooLate : StartLessonError()
}