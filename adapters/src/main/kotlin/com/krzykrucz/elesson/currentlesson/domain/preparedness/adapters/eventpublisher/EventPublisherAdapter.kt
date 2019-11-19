package com.krzykrucz.elesson.currentlesson.domain.preparedness.adapters.eventpublisher

import arrow.effects.IO
import com.krzykrucz.elesson.currentlesson.domain.preparedness.domain.api.NotifyStudentMarkedUnprepared
import com.krzykrucz.elesson.currentlesson.domain.preparedness.domain.api.StudentMarkedUnprepared
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

class EventPublisher(
    private val applicationEventPublisher: ApplicationEventPublisher
) : NotifyStudentMarkedUnprepared {
    override fun invoke(event: StudentMarkedUnprepared): IO<Unit> = IO {
        applicationEventPublisher.publishEvent(event)
    }
}

@Configuration
class EventPublisherAdapter {
    @Bean
    fun eventPublisher(
        applicationEventPublisher: ApplicationEventPublisher
    ): NotifyStudentMarkedUnprepared =
        EventPublisher(
            applicationEventPublisher
        )
}