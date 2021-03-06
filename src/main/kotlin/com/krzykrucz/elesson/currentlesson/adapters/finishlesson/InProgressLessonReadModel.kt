package com.krzykrucz.elesson.currentlesson.adapters.finishlesson

import arrow.core.Either
import arrow.core.toOption
import com.krzykrucz.elesson.currentlesson.domain.finishlesson.FinishLessonError
import com.krzykrucz.elesson.currentlesson.domain.finishlesson.FinishLessonError.LessonNotFound
import com.krzykrucz.elesson.currentlesson.domain.shared.InProgress
import com.krzykrucz.elesson.currentlesson.domain.shared.InProgressLesson
import com.krzykrucz.elesson.currentlesson.domain.shared.LessonIdentifier
import com.krzykrucz.elesson.currentlesson.infrastructure.Database

suspend fun readInProgressLesson(id: LessonIdentifier): Either<FinishLessonError, InProgressLesson> =
    Database.LESSON_DATABASE[id].toOption()
        .filter { lesson -> lesson.status.status == InProgress.status }
        .flatMap { lesson -> lesson.lessonTopic }
        .map { topic ->
            InProgressLesson(
                lessonIdentifier = id,
                lessonTopic = topic
            )
        }
        .toEither(ifEmpty = { LessonNotFound() })
