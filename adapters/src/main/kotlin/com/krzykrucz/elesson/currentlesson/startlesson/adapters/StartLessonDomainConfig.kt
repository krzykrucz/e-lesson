package com.krzykrucz.elesson.currentlesson.startlesson.adapters

import com.krzykrucz.elesson.currentlesson.startlesson.adapters.registry.ERegisterClient
import com.krzykrucz.elesson.currentlesson.startlesson.adapters.schedules.LessonSchedulesClient
import com.krzykrucz.elesson.currentlesson.startlesson.domain.StartLesson
import com.krzykrucz.elesson.currentlesson.startlesson.domain.startLesson
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class StartLessonDomainConfig {

    @Bean
    fun createStartLesson(): StartLesson =
            startLesson(LessonSchedulesClient(), ERegisterClient())

}