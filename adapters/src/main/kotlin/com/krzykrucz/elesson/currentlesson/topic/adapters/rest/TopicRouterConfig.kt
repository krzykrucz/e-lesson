package com.krzykrucz.elesson.currentlesson.topic.adapters.rest


import arrow.core.getOrElse
import arrow.fx.IO
import arrow.fx.typeclasses.Duration
import com.krzykrucz.elesson.currentlesson.toServerResponse
import com.krzykrucz.elesson.currentlesson.topic.ChooseTopicDto
import com.krzykrucz.elesson.currentlesson.topic.usecase.handleChooseTopicDto
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router
import reactor.core.publisher.Mono
import java.util.concurrent.TimeUnit

@Configuration
class TopicRouterConfig {
    @Bean
    fun topicRouter() = router {
        (path("/topic") and accept(MediaType.APPLICATION_JSON)).nest {
            POST("", handleChooseTopicRequest())
        }
    }

    private fun handleChooseTopicRequest(): (ServerRequest) -> Mono<ServerResponse> = { request ->
        request
            .bodyToMono(ChooseTopicDto::class.java)
            .flatMap { chooseTopicDto ->
                handleChooseTopicDto()(chooseTopicDto).map {
                    it.toServerResponse()
                }.run()
            }

    }

    private fun IO<Mono<ServerResponse>>.run(): Mono<ServerResponse> =
        this.unsafeRunTimed(Duration(3, TimeUnit.SECONDS))
            .getOrElse { ServerResponse.badRequest().build() }
}


