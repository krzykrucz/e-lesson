package com.krzykrucz.elesson.currentlesson.preparedness.adapters.findcurrentlesson

import arrow.core.orElse
import arrow.core.toOption
import arrow.effects.IO
import com.krzykrucz.elesson.currentlesson.monolith.Database
import com.krzykrucz.elesson.currentlesson.monolith.PersistentCurrentLesson
import com.krzykrucz.elesson.currentlesson.preparedness.domain.api.FindCurrentLesson
import com.krzykrucz.elesson.currentlesson.preparedness.domain.api.UnpreparednessError
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AdapterConfig {
    @Bean
    fun getLessonBean(): FindCurrentLesson = { id ->
        val lesson = Database.LESSON_DATABASE[id].toOption()
        lesson.flatMap(PersistentCurrentLesson::toLessonAfterAttendance) // TODO try the rest of stages
            .orElse { lesson.flatMap(PersistentCurrentLesson::toLessonInProgress) }
            .orElse { lesson.flatMap(PersistentCurrentLesson::toStartedLesson) }
            .toEither { UnpreparednessError.LessonNotStarted }
            .let { IO.just(it) }
    }
}