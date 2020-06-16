package com.krzykrucz.elesson.currentlesson.preparedness

import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

fun notifyStudentMarkedUnprepared(
    applicationEventPublisher: ApplicationEventPublisher
): NotifyStudentMarkedUnprepared = {
    applicationEventPublisher.publishEvent(it)
}

@Configuration
class EventPublisherAdapter {
    @Bean
    fun eventPublisher(
        applicationEventPublisher: ApplicationEventPublisher
    ): NotifyStudentMarkedUnprepared =
        notifyStudentMarkedUnprepared(applicationEventPublisher)
}
