package com.krzykrucz.elesson.currentlesson.lessonprogress.adapters.rest

import arrow.core.Option
import arrow.core.extensions.fx
import arrow.core.getOrElse
import com.krzykrucz.elesson.currentlesson.infrastructure.run
import com.krzykrucz.elesson.currentlesson.lessonprogress.usecase.LoadLessonProgress
import com.krzykrucz.elesson.currentlesson.domain.shared.ClassName
import com.krzykrucz.elesson.currentlesson.domain.shared.LessonHourNumber
import com.krzykrucz.elesson.currentlesson.domain.shared.LessonIdentifier
import com.krzykrucz.elesson.currentlesson.domain.shared.NonEmptyText
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono
import java.time.LocalDate
import java.util.*

fun handleLessonProgressViewRequest(loadLessonProgress: LoadLessonProgress): (ServerRequest) -> Mono<ServerResponse> = { serverRequest ->
    val readDateFromParams = serverRequest.queryParam("date").toOption()
        .map { LocalDate.parse(it) }

    val readLessonHourNumberFromParams = serverRequest.queryParam("lessonHourNumber").toOption()
        .flatMap { LessonHourNumber.of(it.toInt()) }

    val readClassNameFromParams = serverRequest.queryParam("className").toOption()
        .map {
            ClassName(
                NonEmptyText(
                    it
                )
            )
        }


    val lessonIdOpt = Option.fx {
        val (lessonDate) = readDateFromParams
        val (lessonHourNumber) = readLessonHourNumberFromParams
        val (className) = readClassNameFromParams
        LessonIdentifier(lessonDate, lessonHourNumber, className)
    }

    lessonIdOpt
        .map { lessonId ->
            loadLessonProgress(lessonId)
                .map { lessonProgressOrError ->
                    lessonProgressOrError.fold(
                        ifLeft = { ServerResponse.badRequest().body(BodyInserters.fromObject(it)) },
                        ifRight = { ServerResponse.ok().body(BodyInserters.fromObject(LessonProgressDto.fromLessonProgress(it))) }
                    )
                }
                .run()
        }
        .getOrElse { ServerResponse.badRequest().body(BodyInserters.fromObject("Mandatory parameters not found")) }
}


private fun <T> Optional<T>.toOption(): Option<T> =
    Option.fromNullable(this.orElse(null))

