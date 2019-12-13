package com.krzykrucz.elesson.currentlesson.adapters

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class StartedLessonPersistenceAdapterConfig {

    @Bean
    fun create(): PersistStartedLesson = TODO("use com.krzykrucz.elesson.currentlesson.infrastructure.Database")
}