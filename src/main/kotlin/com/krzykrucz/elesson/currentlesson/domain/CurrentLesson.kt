package com.krzykrucz.elesson.currentlesson.domain

import java.time.LocalDateTime


data class Teacher(val name: String)

data class LessonStartTime(val dateTime: LocalDateTime)

data class StartedLesson(
    val teacher: Teacher,
    val startTime: LessonStartTime
)

typealias StartLesson = (Teacher, LessonStartTime) -> StartedLesson

val startLesson: StartLesson = { teacher, lessonStartTime ->
    StartedLesson(teacher, lessonStartTime)
}