package com.krzykrucz.elesson.currentlesson.lessonprogress.adapters.rest

import arrow.core.Option
import arrow.core.extensions.fx
import arrow.core.getOrElse
import arrow.core.none
import arrow.core.some
import com.krzykrucz.elesson.currentlesson.infrastructure.run
import com.krzykrucz.elesson.currentlesson.lessonprogress.adapters.persistence.createLessonProgressView
import com.krzykrucz.elesson.currentlesson.shared.ClassName
import com.krzykrucz.elesson.currentlesson.shared.LessonHourNumber
import com.krzykrucz.elesson.currentlesson.shared.LessonIdentifier
import com.krzykrucz.elesson.currentlesson.shared.NonEmptyText
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono
import java.time.LocalDate
import java.util.*

fun handleLessonProgressViewRequest(serverRequest: ServerRequest): Mono<ServerResponse> {
    val readDateFromParams = serverRequest.queryParam("date").toOption()
        .map { LocalDate.parse(it) }

    val readLessonHourNumberFromParams = serverRequest.queryParam("lessonHourNumber").toOption()
        .flatMap { LessonHourNumber.of(it.toInt()) }

    val readClassNameFromParams = serverRequest.queryParam("className").toOption()
        .map { ClassName(NonEmptyText.of(it)!!) }


    val lessonIdOpt = Option.fx {
        val (lessonDate) = readDateFromParams
        val (lessonHourNumber) = readLessonHourNumberFromParams
        val (className) = readClassNameFromParams
        LessonIdentifier(lessonDate, lessonHourNumber, className)
    }

    return lessonIdOpt
        .map { lessonId ->
            createLessonProgressView()(lessonId)
                .map { lessonProgressOrError ->
                    lessonProgressOrError.fold(
                        ifLeft = { ServerResponse.badRequest().body(BodyInserters.fromObject(it)) },
                        ifRight = { ServerResponse.ok().body(BodyInserters.fromObject(it.toDto())) }
                    )
                }
                .run()
        }
        .getOrElse { ServerResponse.badRequest().body(BodyInserters.fromObject("Mandatory parameters not found")) }
}


private fun <T> Optional<T>.toOption(): Option<T> =
    try {
        this.orElseThrow().some()
    } catch (ex: Throwable) {
        none()
    }

