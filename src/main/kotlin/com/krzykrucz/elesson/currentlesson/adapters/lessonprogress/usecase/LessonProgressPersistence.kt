package com.krzykrucz.elesson.currentlesson.adapters.lessonprogress.usecase

import arrow.core.Either
import com.krzykrucz.elesson.currentlesson.domain.shared.LessonIdentifier

sealed class LessonProgressError {
    data class LessonNotFound(val error: String = "Could not find lesson") : LessonProgressError()
}

typealias LoadLessonProgress = suspend (LessonIdentifier) -> Either<LessonProgressError, LessonProgress>
