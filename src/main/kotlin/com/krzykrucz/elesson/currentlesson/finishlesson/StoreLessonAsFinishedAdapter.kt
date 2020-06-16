package com.krzykrucz.elesson.currentlesson.finishlesson

import com.krzykrucz.elesson.currentlesson.Database
import com.krzykrucz.elesson.currentlesson.shared.Finished
import com.krzykrucz.elesson.currentlesson.shared.LessonIdentifier

internal val storeLessonAsFinishedAdapter: StoreLessonAsFinished = { lessonId: LessonIdentifier ->
    Database.LESSON_DATABASE.compute(lessonId) { _, lesson ->
        lesson?.copy(status = Finished)
    }
}
