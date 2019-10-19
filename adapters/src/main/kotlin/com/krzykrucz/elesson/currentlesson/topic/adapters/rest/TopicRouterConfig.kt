package com.krzykrucz.elesson.currentlesson.topic.adapters.rest


import arrow.core.Option
import arrow.core.extensions.option.traverse.sequence
import arrow.core.fix
import arrow.core.getOrElse
import arrow.core.toOption
import arrow.fx.IO
import arrow.fx.extensions.fx
import arrow.fx.extensions.io.applicative.applicative
import arrow.fx.fix
import arrow.fx.typeclasses.Duration
import com.krzykrucz.elesson.currentlesson.attendance.infrastructure.fetchAttendanceIO
import com.krzykrucz.elesson.currentlesson.domain.attendance.CheckedAttendance
import com.krzykrucz.elesson.currentlesson.domain.topic.domain.chooseTopic
import com.krzykrucz.elesson.currentlesson.topic.adapters.persistence.fetchFinishedLessonsCount
import com.krzykrucz.elesson.currentlesson.topic.adapters.persistence.persistInProgressLesson
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.BodyInserters
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
                .flatMap { handleChooseTopicDto(it) }
    }

    private fun handleChooseTopicDto(dto: ChooseTopicDto): Mono<ServerResponse> {
        return IO.fx {
            val (checkedAttendance) = fetchAttendanceIO()(dto.lessonIdentifier)
            val (finishedLessonsCount) = fetchFinishedLessonsCount()()
            val (inProgressLessonOpt) = checkedAttendance
                    .flatMap { (it as? CheckedAttendance).toOption() }
                    .map { attendance -> chooseTopic()(dto.topicTitle, finishedLessonsCount, attendance) }
                    .map { inProgressLesson ->
                        persistInProgressLesson()(inProgressLesson)
                                .map { inProgressLesson }
                    }
                    .sequence()
            inProgressLessonOpt.toServerResponse()
        }.run()
    }

    private fun <T> Option<IO<T>>.sequence(): IO<Option<T>> =
            this.sequence(IO.applicative()).fix()
                    .map { it.fix() }

    private fun <T> Option<T>.toServerResponse(): Mono<ServerResponse> =
            this.map { ServerResponse.ok().body(BodyInserters.fromObject(it)) }
                    .getOrElse { ServerResponse.noContent().build() }

    private fun IO<Mono<ServerResponse>>.run(): Mono<ServerResponse> =
            this.unsafeRunTimed(Duration(3, TimeUnit.SECONDS))
                    .getOrElse { ServerResponse.badRequest().build() }

}