package com.krzykrucz.elesson.currentlesson.adapters

import com.krzykrucz.elesson.currentlesson.infrastructure.Database
import com.krzykrucz.elesson.currentlesson.infrastructure.PersistentCurrentLesson
import com.virtuslab.basetypes.result.arrow.toAsync
import com.virtuslab.basetypes.result.toResult
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.UUID


@Configuration
class StartedLessonPersistenceAdapterConfig {

    @Bean
    fun create(): PersistStartedLesson = {
        val id = "${UUID.randomUUID()}"
            .let(::LessonIdentifier)
        Database.LESSON_DATABASE[id] = PersistentCurrentLesson(
            it.hourNumber,
            it.startTime,
            it.classRegistry,
            it.teacher
        )
        id.toResult()
            .toAsync()
    }
}