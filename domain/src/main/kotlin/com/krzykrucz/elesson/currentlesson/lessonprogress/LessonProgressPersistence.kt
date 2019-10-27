package com.krzykrucz.elesson.currentlesson.lessonprogress

import arrow.core.Either
import arrow.fx.IO
import com.krzykrucz.elesson.currentlesson.shared.LessonIdentifier

sealed class LessonProgressError {
    data class LessonNotFound(val error: String = "Could not find lesson") : LessonProgressError()
}

typealias CreateLessonProgressView = (LessonIdentifier) -> IO<Either<LessonProgressError, LessonProgress>>
typealias FetchLessonProgress = (LessonIdentifier) -> IO<Either<LessonProgressError, LessonProgress>>
