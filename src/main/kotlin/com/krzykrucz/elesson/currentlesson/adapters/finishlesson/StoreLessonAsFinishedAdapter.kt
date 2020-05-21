package com.krzykrucz.elesson.currentlesson.adapters.finishlesson

import com.krzykrucz.elesson.currentlesson.Database
import com.krzykrucz.elesson.currentlesson.domain.finishlesson.StoreLessonAsFinished
import com.krzykrucz.elesson.currentlesson.domain.shared.Finished
import com.krzykrucz.elesson.currentlesson.domain.shared.LessonIdentifier

internal val storeLessonAsFinishedAdapter: StoreLessonAsFinished = { lessonId: LessonIdentifier ->
    Database.LESSON_DATABASE.compute(lessonId) { _, lesson ->
        lesson?.copy(status = Finished)
    }
}