package com.krzykrucz.elesson.currentlesson.preparedness.adapters.rest

import arrow.core.Tuple2
import arrow.core.getOrElse
import arrow.core.getOrHandle
import arrow.core.right
import arrow.effects.typeclasses.Duration
import com.krzykrucz.elesson.currentlesson.MonoDomainError
import com.krzykrucz.elesson.currentlesson.handleErrors
import com.krzykrucz.elesson.currentlesson.preparedness.domain.FindCurrentLesson
import com.krzykrucz.elesson.currentlesson.preparedness.domain.NotifyStudentMarkedUnprepared
import com.krzykrucz.elesson.currentlesson.preparedness.domain.PersistUnpreparedStudentToLesson
import com.krzykrucz.elesson.currentlesson.preparedness.domain.ReportUnpreparedness
import com.krzykrucz.elesson.currentlesson.preparedness.domain.StudentReportingUnpreparedness
import com.krzykrucz.elesson.currentlesson.preparedness.readmodel.WriteUnpreparednessInTheRegister
import com.krzykrucz.elesson.currentlesson.shared.LessonIdentifier
import com.krzykrucz.elesson.currentlesson.shared.flatMapAsyncSuccess
import com.krzykrucz.elesson.currentlesson.shared.mapSuccess
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router
import reactor.core.publisher.Mono
import java.util.concurrent.TimeUnit

data class ReportUnpreparedRequest(
    val studentName: StudentReportingUnpreparedness,
    val lessonIdentifier: LessonIdentifier
)


@Configuration
class ReportUnpreparedRouteAdapter {

    @Bean
    fun reportUnpreparedRoute(
        reportUnpreparedness: ReportUnpreparedness,
        writeUnpreparednessInTheRegister: WriteUnpreparednessInTheRegister,
        findCurrentLesson: FindCurrentLesson,
        persistUnpreparedStudentToLesson: PersistUnpreparedStudentToLesson,
        notifyStudentMarkedUnprepared: NotifyStudentMarkedUnprepared
    ) =
        router {
            POST("/reportunprepared") { request ->
                request.bodyToMono(ReportUnpreparedRequest::class.java)
                    .flatMap { req ->
                        findCurrentLesson(req.lessonIdentifier)
                            .mapSuccess { Tuple2(it, req.studentName) }
                            .flatMapAsyncSuccess { reportUnpreparedness(it.b, it.a) }
                            .flatMapAsyncSuccess { event ->
                                persistUnpreparedStudentToLesson(event)
                                    .map { event.right() }
                            }
                            .flatMapAsyncSuccess {
                                notifyStudentMarkedUnprepared(it)
                                    .map { it.right() }
                            }
                            .map { output ->
                                output.map { Mono.just(it) }
                                    .getOrHandle { error -> Mono.error(MonoDomainError(error)) }
                            }
                            .unsafeRunTimed(Duration(3, TimeUnit.SECONDS))
                            .getOrElse { Mono.error(RuntimeException()) }
                    }
                    .flatMap { ServerResponse.ok().build() }
                    .handleErrors()
            }
        }

}

