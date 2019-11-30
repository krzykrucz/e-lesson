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

class LessonHourNumber {
    //TODO
}

data class ClassName(val name: String)

data class ClassRegistry(
    val className: ClassName
)

typealias ScheduledTime = LocalDateTime

data class ScheduledLesson( //TODO
    val className: ClassName
)

typealias StartLesson = (CheckSchedule, FetchClassRegistry, Teacher, LessonStartTime) -> StartedLesson

val startLesson: StartLesson = { checkSchedule, fetchClassRegistry, teacher, lessonStartTime ->
    val scheduledLesson = checkSchedule(teacher, lessonStartTime)
    val registry = fetchClassRegistry(scheduledLesson.className)
    val hourNumber = TODO()
    StartedLesson(teacher, lessonStartTime, hourNumber, registry.className)
}


//dependencies

typealias CheckSchedule = (Teacher, LessonStartTime) -> ScheduledLesson
typealias FetchClassRegistry = (ClassName) -> ClassRegistry

