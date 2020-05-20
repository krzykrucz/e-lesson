package com.krzykrucz.elesson.currentlesson.adapters.finishlesson

import arrow.core.Either
import com.krzykrucz.elesson.currentlesson.adapters.asyncMap
import com.krzykrucz.elesson.currentlesson.domain.finishlesson.FinishLessonError
import com.krzykrucz.elesson.currentlesson.domain.finishlesson.FinishLessonTime
import com.krzykrucz.elesson.currentlesson.domain.finishlesson.bellRang
import com.krzykrucz.elesson.currentlesson.domain.finishlesson.finishLesson
import com.krzykrucz.elesson.currentlesson.domain.shared.LessonIdentifier


suspend fun finishInProgressLesson(lessonIdentifier: LessonIdentifier): Either<FinishLessonError, Unit> {
    val finishLesson = finishLesson(bellRang())
    return readInProgressLesson(lessonIdentifier)
        .map { inProgressLesson -> finishLesson(inProgressLesson, FinishLessonTime.now()) }
        .asyncMap { storeLessonAsFinished(lessonIdentifier) }
}
