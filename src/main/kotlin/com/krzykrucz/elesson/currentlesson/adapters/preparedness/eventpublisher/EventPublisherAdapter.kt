package com.krzykrucz.elesson.currentlesson.adapters.preparedness.eventpublisher

import com.krzykrucz.elesson.currentlesson.domain.preparedness.NotifyStudentMarkedUnprepared
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
