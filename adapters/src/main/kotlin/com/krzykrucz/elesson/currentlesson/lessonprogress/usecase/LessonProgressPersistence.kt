package com.krzykrucz.elesson.currentlesson.lessonprogress.usecase

import arrow.core.Either
import arrow.fx.IO
import com.krzykrucz.elesson.currentlesson.domain.shared.LessonIdentifier

sealed class LessonProgressError {
    data class LessonNotFound(val error: String = "Could not find lesson") : LessonProgressError()
}

typealias LoadLessonProgress = (LessonIdentifier) -> IO<Either<LessonProgressError, LessonProgress>>
