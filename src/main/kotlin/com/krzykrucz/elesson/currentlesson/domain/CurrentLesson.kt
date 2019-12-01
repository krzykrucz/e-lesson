package com.krzykrucz.elesson.currentlesson.domain

import java.time.LocalDateTime


data class Teacher(val name: String)

data class LessonStartTime(val dateTime: LocalDateTime)

data class StartedLesson(
    val teacher: Teacher,
    val startTime: LessonStartTime,
    val className: ClassName
)

data class ClassName(val name: String)

class ClassRegistry // TODO

class ScheduledLesson // TODO

typealias StartLesson = (/*TODO*/ Teacher, LessonStartTime) -> StartedLesson

val startLesson: StartLesson = { /*TODO*/ teacher, lessonStartTime ->
    TODO()
    StartedLesson(teacher, lessonStartTime, ClassName("Gryffindor"))
}


//dependencies
// TODO
typealias CheckSchedule = (Any, Any) -> ScheduledLesson

// TODO
typealias FetchClassRegistry = (Any) -> ClassRegistry

