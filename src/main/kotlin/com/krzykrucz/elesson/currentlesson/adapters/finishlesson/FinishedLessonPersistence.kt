package com.krzykrucz.elesson.currentlesson.adapters.finishlesson

import com.krzykrucz.elesson.currentlesson.domain.shared.Finished
import com.krzykrucz.elesson.currentlesson.domain.shared.LessonIdentifier
import com.krzykrucz.elesson.currentlesson.infrastructure.Database

suspend fun storeLessonAsFinished(lessonId: LessonIdentifier) {
    Database.LESSON_DATABASE.compute(lessonId) { _, lesson ->
        lesson?.copy(status = Finished)
    }
}
