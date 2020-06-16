package com.krzykrucz.elesson.currentlesson.startlesson.rest

import com.krzykrucz.elesson.currentlesson.startlesson.FetchClassRegistry
import com.krzykrucz.elesson.currentlesson.startlesson.FetchScheduledLesson
import com.krzykrucz.elesson.currentlesson.startlesson.PersistStartedLesson
import com.krzykrucz.elesson.currentlesson.startlesson.startLessonApi
import org.springframework.web.reactive.function.server.coRouter


internal fun startLessonRestAdapter(
    fetchScheduledLesson: FetchScheduledLesson,
    fetchClassRegistry: FetchClassRegistry,
    persistStartedLesson: PersistStartedLesson
) =
    coRouter {
        POST("/startlesson", startLessonApi(fetchScheduledLesson, fetchClassRegistry, persistStartedLesson))
    }

