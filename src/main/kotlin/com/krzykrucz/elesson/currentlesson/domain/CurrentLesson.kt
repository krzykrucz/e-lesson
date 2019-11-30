package com.krzykrucz.elesson.currentlesson.domain

import java.time.LocalDateTime
import java.time.LocalTime


data class Teacher(val name: String)

data class LessonStartTime(val dateTime: LocalDateTime)

data class StartedLesson(
    val teacher: Teacher,
    val startTime: LessonStartTime,
    val hourNumber: LessonHourNumber,
    val className: ClassName
)

typealias Time = LocalTime

sealed class LessonHourNumber(val number: Int, val time: LocalTime) {

    object One : LessonHourNumber(1, Time.parse("08:00"))
    object Two : LessonHourNumber(2, Time.parse("08:50"))
    object Three : LessonHourNumber(3, Time.parse("09:40"))

    companion object {
        fun of(number: Int): LessonHourNumber = when (number) {
            1 -> One
            2 -> Two
            3 -> Three
            else -> throw NumberFormatException()
        }
    }
}

data class ClassName(val name: String)

data class ClassRegistry(
    val className: ClassName
)

typealias ScheduledTime = LocalDateTime

data class ScheduledLesson(
    val className: ClassName,
    val scheduledTime: ScheduledTime,
    val hourNumber: LessonHourNumber
)

typealias StartLesson = (CheckSchedule, FetchClassRegistry, Teacher, LessonStartTime) -> StartedLesson

val startLesson: StartLesson = { checkSchedule, fetchClassRegistry, teacher, lessonStartTime ->
    val scheduledLesson = checkSchedule(teacher, lessonStartTime)
    val registry = fetchClassRegistry(scheduledLesson.className)
    StartedLesson(teacher, lessonStartTime, scheduledLesson.hourNumber, registry.className)
}


//dependencies

typealias CheckSchedule = (Teacher, LessonStartTime) -> ScheduledLesson
typealias FetchClassRegistry = (ClassName) -> ClassRegistry

