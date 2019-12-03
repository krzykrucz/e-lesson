package com.krzykrucz.elesson.currentlesson.domain

import arrow.core.NonEmptyList
import arrow.core.Option
import arrow.core.toOption
import com.virtuslab.basetypes.refined.NonEmptyText
import com.virtuslab.basetypes.refined.RawText
import java.time.LocalDateTime
import java.time.LocalTime


data class Teacher(val name: NonEmptyText)

data class AttemptedLessonStartTime(val dateTime: RawText)
data class LessonStartTime(val dateTime: LocalDateTime) {
    companion object {
        fun of(stringTime: RawText) =
            LessonStartTime(
                LocalDateTime.parse(stringTime.text)
            )
    }
}

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

typealias StartLesson = (ValidateStartLessonTime, CheckSchedule, FetchClassRegistry, Teacher, AttemptedLessonStartTime) -> StartedLesson

val startLesson: StartLesson = { validateStartLessonTime, checkSchedule, fetchClassRegistry, teacher, lessonStartTime ->
    val validLessonStartTime = validateStartLessonTime(lessonStartTime)
    val scheduledLesson = checkSchedule(teacher, validLessonStartTime)
    val registry = fetchClassRegistry(scheduledLesson.className)
    StartedLesson(teacher, validLessonStartTime, scheduledLesson.hourNumber, registry)
}


//dependencies

typealias CheckSchedule = (Teacher, LessonStartTime) -> ScheduledLesson
typealias FetchClassRegistry = (ClassName) -> ClassRegistry

typealias ValidateStartLessonTime = (AttemptedLessonStartTime) -> LessonStartTime

