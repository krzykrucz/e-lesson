package com.krzykrucz.elesson.currentlesson.finishlesson

import arrow.core.Either
import arrow.core.toOption
import arrow.fx.IO
import com.krzykrucz.elesson.currentlesson.finishlesson.domain.FinishLessonError
import com.krzykrucz.elesson.currentlesson.finishlesson.domain.FinishLessonError.LessonNotFound
import com.krzykrucz.elesson.currentlesson.monolith.Database
import com.krzykrucz.elesson.currentlesson.shared.InProgress
import com.krzykrucz.elesson.currentlesson.shared.InProgressLesson
import com.krzykrucz.elesson.currentlesson.shared.LessonIdentifier

fun readInProgressLesson(id: LessonIdentifier): IO<Either<FinishLessonError, InProgressLesson>> =
    Database.LESSON_DATABASE[id].toOption()
        .filter { lesson -> lesson.status.status == InProgress.status }
        .flatMap { lesson -> lesson.lessonTopic }
        .map { topic -> InProgressLesson(lessonTopic = topic) }
        .toEither(ifEmpty = { LessonNotFound() })
        .let { IO.just(it) }
