package com.krzykrucz.elesson.currentlesson.lessonprogress.adapters.persistence

import arrow.core.toOption
import arrow.fx.IO
import com.krzykrucz.elesson.currentlesson.lessonprogress.usecase.LessonProgress
import com.krzykrucz.elesson.currentlesson.lessonprogress.usecase.LessonProgressError
import com.krzykrucz.elesson.currentlesson.lessonprogress.usecase.LoadLessonProgress
import com.krzykrucz.elesson.currentlesson.monolith.Database

fun loadLessonProgress(): LoadLessonProgress = { lessonIdentifier ->
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
        .let { IO.just(it) }
}
