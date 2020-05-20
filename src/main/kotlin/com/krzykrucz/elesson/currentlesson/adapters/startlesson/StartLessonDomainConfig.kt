package com.krzykrucz.elesson.currentlesson.adapters.startlesson

import com.krzykrucz.elesson.currentlesson.adapters.startlesson.registry.eRegisterClient
import com.krzykrucz.elesson.currentlesson.adapters.startlesson.schedules.lessonSchedulesClient
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
            lessonSchedulesClient,
            validateStartTime(),
            eRegisterClient
        )

}
