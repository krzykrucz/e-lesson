package com.krzykrucz.elesson.currentlesson.lessonprogress

import arrow.core.Either
import com.krzykrucz.elesson.currentlesson.shared.LessonIdentifier

sealed class LessonProgressError {
    data class LessonNotFound(val error: String = "Could not find lesson") : LessonProgressError()
}

typealias LoadLessonProgress = suspend (LessonIdentifier) -> Either<LessonProgressError, LessonProgress>
