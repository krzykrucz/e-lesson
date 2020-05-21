package com.krzykrucz.elesson.currentlesson.adapters.topic.rest


import com.krzykrucz.elesson.currentlesson.adapters.AsyncRequestHandler
import com.krzykrucz.elesson.currentlesson.adapters.toServerResponse
import com.krzykrucz.elesson.currentlesson.adapters.topic.ChooseTopicDto
import com.krzykrucz.elesson.currentlesson.adapters.topic.usecase.handleChooseTopicDto
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.awaitBody
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class TopicRouterConfig {
    @Bean
    fun topicRouter() = coRouter {
        POST("/topic", handleChooseTopicRequest)
    }

    private val handleChooseTopicRequest: AsyncRequestHandler = { request ->
        val chooseTopicDto = request.awaitBody<ChooseTopicDto>()
        handleChooseTopicDto(chooseTopicDto)
            .toServerResponse()
    }

}


