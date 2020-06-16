package com.krzykrucz.elesson.currentlesson.lessonprogress

import arrow.core.Option
import arrow.core.extensions.fx
import com.krzykrucz.elesson.currentlesson.shared.ClassName
import com.krzykrucz.elesson.currentlesson.shared.LessonHourNumber
import com.krzykrucz.elesson.currentlesson.shared.LessonIdentifier
import com.krzykrucz.elesson.currentlesson.shared.NonEmptyText
import com.krzykrucz.elesson.currentlesson.shared.RestApi
import com.krzykrucz.elesson.currentlesson.shared.asyncFlatMap
import com.krzykrucz.elesson.currentlesson.shared.toServerResponse
import java.time.LocalDate
import java.util.Optional

fun handleLessonProgressViewRequest(loadLessonProgress: LoadLessonProgress): RestApi = { serverRequest ->
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

