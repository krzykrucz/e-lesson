package com.krzykrucz.elesson.currentlesson.adapters.finishlesson

import com.krzykrucz.elesson.currentlesson.adapters.asyncMap
import com.krzykrucz.elesson.currentlesson.adapters.toServerResponse
import com.krzykrucz.elesson.currentlesson.domain.finishlesson.FinishLesson
import com.krzykrucz.elesson.currentlesson.domain.finishlesson.FinishLessonTime
import com.krzykrucz.elesson.currentlesson.domain.finishlesson.StoreLessonAsFinished
import com.krzykrucz.elesson.currentlesson.domain.shared.LessonIdentifier
import org.springframework.web.reactive.function.server.awaitBody
import org.springframework.web.reactive.function.server.coRouter


fun finishLessonRestAdapter(
    finishLesson: FinishLesson,
    storeLessonAsFinished: StoreLessonAsFinished
) = coRouter {
    PUT("/finished-lessons") { request ->
        val dto = request.awaitBody<FinishLessonDto>()
        readInProgressLesson(dto.lessonIdentifier)
            .map { inProgressLesson -> finishLesson(inProgressLesson, FinishLessonTime.now()) }
            .asyncMap { storeLessonAsFinished(dto.lessonIdentifier) }
            .toServerResponse()
    }
}

private data class FinishLessonDto(val lessonIdentifier: LessonIdentifier)
