package com.krzykrucz.elesson.currentlesson.adapters.startlesson.rest

import com.krzykrucz.elesson.currentlesson.adapters.startlesson.startLessonApi
import com.krzykrucz.elesson.currentlesson.domain.startlesson.FetchClassRegistry
import com.krzykrucz.elesson.currentlesson.domain.startlesson.FetchScheduledLesson
import com.krzykrucz.elesson.currentlesson.domain.startlesson.PersistStartedLesson
import org.springframework.web.reactive.function.server.coRouter


internal fun startLessonRestAdapter(
    fetchScheduledLesson: FetchScheduledLesson,
    fetchClassRegistry: FetchClassRegistry,
    persistStartedLesson: PersistStartedLesson
) =
    coRouter {
        POST("/startlesson", startLessonApi(fetchScheduledLesson, fetchClassRegistry, persistStartedLesson))
    }

