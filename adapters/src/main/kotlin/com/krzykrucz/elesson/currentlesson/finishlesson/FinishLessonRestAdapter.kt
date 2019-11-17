package com.krzykrucz.elesson.currentlesson.finishlesson

import com.krzykrucz.elesson.currentlesson.infrastructure.run
import com.krzykrucz.elesson.currentlesson.shared.LessonIdentifier
import com.krzykrucz.elesson.currentlesson.toServerResponse
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router
import reactor.core.publisher.Mono

@Configuration
class FinishLessonRestAdapter {

    @Bean
    fun finishLessonRoute() = router {
        (path("/finished-lessons") and accept(MediaType.APPLICATION_JSON)).nest {
            PUT("", handleFinishLessonRequest())
        }
    }

    private fun handleFinishLessonRequest(): (ServerRequest) -> Mono<out ServerResponse> = { request ->
        request
            .bodyToMono(FinishLessonDto::class.java)
            .flatMap { dto -> finishLesson(dto) }
    }

    private fun finishLesson(dto: FinishLessonDto) =
        finishInProgressLesson(dto.lessonIdentifier)
            .map { it.toServerResponse() }
            .run()

}

data class FinishLessonDto(val lessonIdentifier: LessonIdentifier)