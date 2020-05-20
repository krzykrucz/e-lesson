package com.krzykrucz.elesson.currentlesson.adapters.topic.rest


import com.krzykrucz.elesson.currentlesson.adapters.toServerResponseAsync
import com.krzykrucz.elesson.currentlesson.adapters.topic.ChooseTopicDto
import com.krzykrucz.elesson.currentlesson.adapters.topic.usecase.handleChooseTopicDto
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.awaitBody
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class TopicRouterConfig {
    @Bean
    fun topicRouter() = coRouter {
        (path("/topic") and accept(MediaType.APPLICATION_JSON)).nest {
            POST("", handleChooseTopicRequest)
        }
    }

    private val handleChooseTopicRequest: suspend (ServerRequest) -> ServerResponse = { request ->
        val chooseTopicDto = request.awaitBody<ChooseTopicDto>()
        handleChooseTopicDto(chooseTopicDto)
            .toServerResponseAsync()
    }

}


