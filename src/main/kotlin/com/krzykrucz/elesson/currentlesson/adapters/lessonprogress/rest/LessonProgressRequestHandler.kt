package com.krzykrucz.elesson.currentlesson.adapters.lessonprogress.rest

import arrow.core.Option
import arrow.core.extensions.fx
import com.krzykrucz.elesson.currentlesson.adapters.AsyncRequestHandler
import com.krzykrucz.elesson.currentlesson.adapters.asyncFlatMap
import com.krzykrucz.elesson.currentlesson.adapters.lessonprogress.usecase.LoadLessonProgress
import com.krzykrucz.elesson.currentlesson.adapters.toServerResponse
import com.krzykrucz.elesson.currentlesson.domain.shared.ClassName
import com.krzykrucz.elesson.currentlesson.domain.shared.LessonHourNumber
import com.krzykrucz.elesson.currentlesson.domain.shared.LessonIdentifier
import com.krzykrucz.elesson.currentlesson.domain.shared.NonEmptyText
import java.time.LocalDate
import java.util.Optional

fun handleLessonProgressViewRequest(loadLessonProgress: LoadLessonProgress): AsyncRequestHandler = { serverRequest ->
    val readDateFromParams = serverRequest.queryParam("date").toOption()
        .map { LocalDate.parse(it) }
    val readLessonHourNumberFromParams = serverRequest.queryParam("lessonHourNumber").toOption()
        .flatMap { LessonHourNumber.of(it.toInt()) }
    val readClassNameFromParams = serverRequest.queryParam("className").toOption()
        .map { ClassName(NonEmptyText(it)) }

    val lessonIdOpt = Option.fx {
        val (lessonDate) = readDateFromParams
        val (lessonHourNumber) = readLessonHourNumberFromParams
        val (className) = readClassNameFromParams
        LessonIdentifier(lessonDate, lessonHourNumber, className)
    }

    lessonIdOpt.toEither { MandatoryParametersNotFound }
        .asyncFlatMap { loadLessonProgress(it) }
        .map(LessonProgressDto.Companion::fromLessonProgress)
        .toServerResponse()
}

private object MandatoryParametersNotFound

private fun <T> Optional<T>.toOption(): Option<T> =
    Option.fromNullable(this.orElse(null))

