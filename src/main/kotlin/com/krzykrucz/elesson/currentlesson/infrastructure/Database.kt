package com.krzykrucz.elesson.currentlesson.infrastructure

import arrow.core.Option
import com.krzykrucz.elesson.currentlesson.adapters.LessonIdentifier
import com.krzykrucz.elesson.currentlesson.domain.ClassRegistry
import com.krzykrucz.elesson.currentlesson.domain.LessonHourNumber
import com.krzykrucz.elesson.currentlesson.domain.LessonStartTime
import com.krzykrucz.elesson.currentlesson.domain.StartedLesson
import com.krzykrucz.elesson.currentlesson.domain.Teacher
import java.util.concurrent.ConcurrentHashMap
import arrow.core.Option.Companion as Option1


data class PersistentCurrentLesson(
    val lessonHourNumber: LessonHourNumber,
    val startTime: LessonStartTime,
    val classRegistry: ClassRegistry,
    val teacher: Teacher
) {

    fun toStartedLesson(): Option<StartedLesson> =
        StartedLesson(
            this.teacher,
            this.startTime,
            this.lessonHourNumber,
            this.classRegistry
        ).let(Option1::just)

}

class Database {

    companion object {
        val LESSON_DATABASE: ConcurrentHashMap<LessonIdentifier, PersistentCurrentLesson> = ConcurrentHashMap(mutableMapOf())
    }
}