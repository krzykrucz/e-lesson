package com.krzykrucz.elesson.currentlesson.finishlesson

import com.krzykrucz.elesson.currentlesson.shared.LessonIdentifier
import com.krzykrucz.elesson.currentlesson.shared.asyncMap
import com.krzykrucz.elesson.currentlesson.shared.toServerResponse
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
