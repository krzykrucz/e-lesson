package com.krzykrucz.elesson.currentlesson.adapters.finishlesson

import com.krzykrucz.elesson.currentlesson.adapters.toServerResponse
import com.krzykrucz.elesson.currentlesson.domain.shared.LessonIdentifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.awaitBody
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class FinishLessonRestAdapter {

    @Bean
    fun finishLessonRoute() = coRouter {
        (path("/finished-lessons") and accept(MediaType.APPLICATION_JSON)).nest {
            PUT("", handleFinishLessonRequest)
        }
    }

    private val handleFinishLessonRequest: suspend (ServerRequest) -> ServerResponse = { request ->
        request.awaitBody<FinishLessonDto>()
            .let { finishLesson(it) }
    }

    private suspend fun finishLesson(dto: FinishLessonDto) =
        finishInProgressLesson(dto.lessonIdentifier)
            .toServerResponse()

}

data class FinishLessonDto(val lessonIdentifier: LessonIdentifier)
