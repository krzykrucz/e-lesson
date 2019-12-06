package com.krzykrucz.elesson.currentlesson.domain

import arrow.core.NonEmptyList
import arrow.core.Option
import arrow.core.toOption
import com.virtuslab.basetypes.refined.NaturalNumber
import com.virtuslab.basetypes.refined.NonEmptyText
import java.time.LocalDateTime
import java.time.LocalTime


data class Teacher(val name: NonEmptyText) // this will be todo

data class AttemptedLessonStartTime(val dateTime: LocalDateTime)

data class LessonStartTime(val dateTime: LocalDateTime)

data class StartedLesson(
    val teacher: Teacher,
    val startTime: LessonStartTime,
    val hourNumber: LessonHourNumber,
    val classRegistry: ClassRegistry
)

typealias Time = LocalTime

sealed class LessonHourNumber(val time: Time, val number: NaturalNumber) {

    object One : LessonHourNumber(Time.parse("08:00"), NaturalNumber.ONE)
    object Two : LessonHourNumber(Time.parse("08:55"), NaturalNumber.TWO)
    object Three : LessonHourNumber(Time.parse("09:50"), NaturalNumber.THREE)

    companion object {
        fun of(number: Int): Option<LessonHourNumber> = when (number) { // this will be todo
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
    val studentList: StudentList // this will be todo
)

typealias ScheduledTime = LocalDateTime

data class ScheduledLesson(
    val className: ClassName,
    val scheduledTime: ScheduledTime,
    val hourNumber: LessonHourNumber
)

typealias StartLesson = (CheckSchedule, FetchClassRegistry, Teacher, AttemptedLessonStartTime) -> StartedLesson

val startLesson: StartLesson = { checkSchedule, fetchClassRegistry, teacher, lessonStartTime ->
    val scheduledLesson = checkSchedule(teacher, lessonStartTime)
    val checkedLessonStartTime = LessonStartTime(lessonStartTime.dateTime)
    val registry = fetchClassRegistry(scheduledLesson.className)
    StartedLesson(teacher, checkedLessonStartTime, scheduledLesson.hourNumber, registry)
}


//dependencies

typealias CheckSchedule = (Teacher, AttemptedLessonStartTime) -> ScheduledLesson
typealias FetchClassRegistry = (ClassName) -> ClassRegistry
