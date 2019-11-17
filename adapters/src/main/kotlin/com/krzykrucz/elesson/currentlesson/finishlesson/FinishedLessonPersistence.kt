package com.krzykrucz.elesson.currentlesson.finishlesson

import arrow.fx.IO
import com.krzykrucz.elesson.currentlesson.monolith.Database
import com.krzykrucz.elesson.currentlesson.shared.Finished
import com.krzykrucz.elesson.currentlesson.shared.LessonIdentifier

fun storeLessonAsFinished(lessonId: LessonIdentifier): IO<Unit> =
    Database.LESSON_DATABASE.compute(lessonId) { _, lesson ->
        lesson?.copy(
            status = Finished
        )
    }.let { IO.lazy }