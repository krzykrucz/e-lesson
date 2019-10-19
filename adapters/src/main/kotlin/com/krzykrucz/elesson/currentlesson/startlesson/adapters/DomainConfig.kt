package com.krzykrucz.elesson.currentlesson.startlesson.adapters

import com.krzykrucz.elesson.currentlesson.startlesson.domain.StartLesson
import com.krzykrucz.elesson.currentlesson.startlesson.domain.startLesson
import com.krzykrucz.elesson.currentlesson.startlesson.adapters.registry.ERegisterClient
import com.krzykrucz.elesson.currentlesson.startlesson.adapters.schedules.LessonSchedulesClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class DomainConfig {

    @Bean
    fun createStartLesson(): StartLesson =
            startLesson(LessonSchedulesClient(), ERegisterClient())

}