package com.krzykrucz.elesson.currentlesson.adapters.finishlesson

import com.krzykrucz.elesson.currentlesson.adapters.AsyncRequestHandler
import com.krzykrucz.elesson.currentlesson.adapters.toServerResponse
import com.krzykrucz.elesson.currentlesson.domain.shared.LessonIdentifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.awaitBody
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class FinishLessonRestAdapter {

    @Bean
    fun finishLessonRoute() = coRouter {
        PUT("/finished-lessons", handleFinishLessonRequest)
    }

    private val handleFinishLessonRequest: AsyncRequestHandler = { request ->
        request.awaitBody<FinishLessonDto>()
            .let { finishLesson(it) }
    }

    private suspend fun finishLesson(dto: FinishLessonDto) =
        finishInProgressLesson(dto.lessonIdentifier)
            .toServerResponse()

}

data class FinishLessonDto(val lessonIdentifier: LessonIdentifier)
