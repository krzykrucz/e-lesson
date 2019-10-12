package com.krzykrucz.elesson.currentlesson.startlesson

import com.krzykrucz.elesson.currentlesson.domain.startlesson.CheckScheduledLesson
import com.krzykrucz.elesson.currentlesson.domain.startlesson.FetchClassRegistry
import com.krzykrucz.elesson.currentlesson.domain.startlesson.startLesson
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class DomainConfig {

    @Bean
    fun createStartLesson(checkScheduledLesson: CheckScheduledLesson, fetchClassRegistry: FetchClassRegistry) =
        startLesson(checkScheduledLesson, fetchClassRegistry)

}