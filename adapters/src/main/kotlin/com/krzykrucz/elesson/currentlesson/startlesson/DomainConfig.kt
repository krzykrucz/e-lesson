package com.krzykrucz.elesson.currentlesson.startlesson

import com.krzykrucz.elesson.currentlesson.domain.startlesson.StartLesson
import com.krzykrucz.elesson.currentlesson.domain.startlesson.startLesson
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class DomainConfig {

    @Bean
    fun createStartLesson(): StartLesson =
        startLesson(LessonSchedulesClient(), ERegisterClient())

}