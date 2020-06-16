package com.krzykrucz.elesson.currentlesson.lessonprogress

import arrow.core.toOption
import com.krzykrucz.elesson.currentlesson.Database

internal fun loadLessonProgressAdapter(): LoadLessonProgress = { lessonIdentifier ->
    Database.LESSON_DATABASE[lessonIdentifier].toOption()
        .map { currentLesson ->
            LessonProgress(
                semester = currentLesson.semester,
                className = currentLesson.classRegistry.className,
                date = currentLesson.lessonId.date,
                subject = currentLesson.subject,
                topic = currentLesson.lessonTopic,
                status = currentLesson.status
            )
        }
        .toEither(ifEmpty = { LessonProgressError.LessonNotFound() })
}
