package com.krzykrucz.elesson.currentlesson.infrastructure

import com.krzykrucz.elesson.currentlesson.adapters.lessonprogress.rest.handleLessonProgressViewRequest
import com.krzykrucz.elesson.currentlesson.adapters.lessonprogress.usecase.LoadLessonProgress
import org.springframework.web.reactive.function.server.coRouter

fun routes(
    loadLessonProgress: LoadLessonProgress
) = coRouter {
    GET("/progress", handleLessonProgressViewRequest(loadLessonProgress))
}
