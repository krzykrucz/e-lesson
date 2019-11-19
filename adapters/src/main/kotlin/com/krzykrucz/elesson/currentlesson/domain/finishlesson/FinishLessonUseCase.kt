package com.krzykrucz.elesson.currentlesson.domain.finishlesson

import arrow.core.Either
import arrow.fx.IO
import arrow.fx.extensions.fx
import com.krzykrucz.elesson.currentlesson.domain.finishlesson.FinishLessonError
import com.krzykrucz.elesson.currentlesson.domain.finishlesson.FinishLessonTime
import com.krzykrucz.elesson.currentlesson.domain.finishlesson.bellRang
import com.krzykrucz.elesson.currentlesson.domain.finishlesson.finishLesson
import com.krzykrucz.elesson.currentlesson.sequence
import com.krzykrucz.elesson.currentlesson.domain.shared.LessonIdentifier


fun finishInProgressLesson(lessonIdentifier: LessonIdentifier): IO<Either<FinishLessonError, Unit>> = IO.fx {
    val (inProgressLesson) = readInProgressLesson(
        lessonIdentifier
    )
    val finishLesson =
        finishLesson(bellRang())
    val (finishedLesson) =
        inProgressLesson
            .map { finishLesson(it, FinishLessonTime.now()) }
            .map { storeLessonAsFinished(lessonIdentifier) }
            .sequence()
    finishedLesson
}