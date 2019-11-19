package com.krzykrucz.elesson.currentlesson.domain.startlesson.adapters

import com.krzykrucz.elesson.currentlesson.domain.startlesson.adapters.registry.ERegisterClient
import com.krzykrucz.elesson.currentlesson.domain.startlesson.adapters.schedules.LessonSchedulesClient
import com.krzykrucz.elesson.currentlesson.domain.startlesson.StartLesson
import com.krzykrucz.elesson.currentlesson.domain.startlesson.startLesson
import com.krzykrucz.elesson.currentlesson.domain.startlesson.validateStartTime
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class StartLessonDomainConfig {

    @Bean
    fun createStartLesson(): StartLesson =
        startLesson(
            LessonSchedulesClient(),
            validateStartTime(),
            ERegisterClient()
        )

}
