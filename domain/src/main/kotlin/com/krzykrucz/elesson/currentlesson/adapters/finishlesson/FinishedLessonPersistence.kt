package com.krzykrucz.elesson.currentlesson.adapters.finishlesson

import arrow.fx.IO
import com.krzykrucz.elesson.currentlesson.adapters.monolith.Database
import com.krzykrucz.elesson.currentlesson.domain.shared.Finished
import com.krzykrucz.elesson.currentlesson.domain.shared.LessonIdentifier

fun storeLessonAsFinished(lessonId: LessonIdentifier): IO<Unit> =
    Database.LESSON_DATABASE.compute(lessonId) { _, lesson ->
        lesson?.copy(
            status = Finished
        )
    }.let { IO.lazy }