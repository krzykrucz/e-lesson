package com.krzykrucz.elesson.currentlesson.topic


import arrow.core.Option
import arrow.core.getOrElse
import arrow.core.toOption
import arrow.fx.IO
import arrow.fx.extensions.fx
import arrow.fx.typeclasses.Duration
import com.krzykrucz.elesson.currentlesson.attendance.infrastructure.fetchAttendanceIO
import com.krzykrucz.elesson.currentlesson.domain.attendance.CheckedAttendance
import com.krzykrucz.elesson.currentlesson.domain.topic.chooseTopic
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.context.annotation.Bean
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router
import reactor.core.publisher.Mono
import java.util.concurrent.TimeUnit

class TopicRouterConfig {
    @Bean
    fun topicRouter() = router {
        (path("/topic") and accept(MediaType.APPLICATION_JSON)).nest {
            POST("", handleChooseTopicRequest())
            GET("", handleGetLessonTopicRequest())
        }
    }

    private fun handleGetLessonTopicRequest(): (ServerRequest) -> Mono<ServerResponse> = { request ->

    }

    private fun handleChooseTopicRequest(): (ServerRequest) -> Mono<ServerResponse> = { request ->
        IO.fx {
            val (dto) = effect { request.bodyToMono(ChooseTopicDto::class.java).awaitSingle() }
            val checkedAttendance = fetchAttendanceIO()(dto.lessonIdentifier).bind()
            val inProgressLessonOpt = checkedAttendance
                    .flatMap { (it as? CheckedAttendance).toOption() }
                    .map { chooseTopic()(dto.topicTitle, it) }
            inProgressLessonOpt.toServerResponse()
        }.run()
    }

    private fun <T> Option<T>.toServerResponse(): Mono<ServerResponse> =
            this.map { ServerResponse.ok().body(BodyInserters.fromObject(it)) }
                    .getOrElse { ServerResponse.noContent().build() }

    private fun IO<Mono<ServerResponse>>.run(): Mono<ServerResponse> =
            this.unsafeRunTimed(Duration(3, TimeUnit.SECONDS))
                    .getOrElse { ServerResponse.badRequest().build() }

}