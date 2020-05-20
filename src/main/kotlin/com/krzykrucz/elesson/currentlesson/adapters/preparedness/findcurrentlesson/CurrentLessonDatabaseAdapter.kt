package com.krzykrucz.elesson.currentlesson.adapters.preparedness.findcurrentlesson

import arrow.core.orElse
import arrow.core.toOption
import com.krzykrucz.elesson.currentlesson.domain.preparedness.domain.api.FindCurrentLesson
import com.krzykrucz.elesson.currentlesson.domain.preparedness.domain.api.UnpreparednessError
import com.krzykrucz.elesson.currentlesson.infrastructure.Database
import com.krzykrucz.elesson.currentlesson.infrastructure.PersistentCurrentLesson
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
    }
}
