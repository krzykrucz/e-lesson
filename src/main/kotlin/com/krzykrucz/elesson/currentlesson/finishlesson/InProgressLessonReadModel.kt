package com.krzykrucz.elesson.currentlesson.finishlesson

import arrow.core.Either
import arrow.core.toOption
import com.krzykrucz.elesson.currentlesson.Database
import com.krzykrucz.elesson.currentlesson.finishlesson.FinishLessonError.LessonNotFound
import com.krzykrucz.elesson.currentlesson.shared.InProgress
import com.krzykrucz.elesson.currentlesson.shared.InProgressLesson
import com.krzykrucz.elesson.currentlesson.shared.LessonIdentifier

// TODO extract port
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
