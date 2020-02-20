package com.krzykrucz.elesson.currentlesson.infrastructure

import arrow.core.Option
import com.krzykrucz.elesson.currentlesson.adapters.LessonIdentifier
import com.krzykrucz.elesson.currentlesson.domain.ClassGroupName
import com.krzykrucz.elesson.currentlesson.domain.Period
import com.krzykrucz.elesson.currentlesson.domain.Register
import com.krzykrucz.elesson.currentlesson.domain.StartedLesson
import com.krzykrucz.elesson.currentlesson.domain.Subject
import java.time.LocalDate
import java.util.concurrent.ConcurrentHashMap
import arrow.core.Option.Companion as Option1


data class PersistentCurrentLesson(
    val period: Period,
    val startTime: LocalDate,
    val classRegistry:  Register,
    val classGroupName: ClassGroupName,
    val subject: Subject
) {

    fun toStartedLesson(): Option<StartedLesson> =
        StartedLesson(
            this.startTime,
            this.period,
            this.classGroupName,
            this.subject,
            this.classRegistry
        ).let(Option1::just)

}

class Database {

    companion object {
        val LESSON_DATABASE: ConcurrentHashMap<LessonIdentifier, PersistentCurrentLesson> = ConcurrentHashMap(mutableMapOf())
    }
}