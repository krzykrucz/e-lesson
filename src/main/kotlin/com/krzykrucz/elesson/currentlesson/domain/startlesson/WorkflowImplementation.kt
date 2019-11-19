package com.krzykrucz.elesson.currentlesson.domain.startlesson

import arrow.core.Either
import arrow.core.extensions.either.monad.flatten
import arrow.core.flatMap
import arrow.fx.IO
import arrow.fx.extensions.fx
import com.krzykrucz.elesson.currentlesson.domain.shared.ClassRegistry
import com.krzykrucz.elesson.currentlesson.domain.shared.LessonIdentifier
import com.krzykrucz.elesson.currentlesson.domain.shared.StartedLesson
import com.krzykrucz.elesson.currentlesson.domain.shared.sequence


private fun ScheduledLesson.lessonIdentifier() =
    LessonIdentifier(this.scheduledTime.toLocalDate(), this.lessonHourNumber, this.className)

private fun ScheduledLesson.toCurrentLessonWithClass(
    classRegistryOrError: Either<StartLessonError, ClassRegistry>
): Either<StartLessonError, StartedLesson> =
    classRegistryOrError
        .map { classRegistry -> StartedLesson(this.lessonIdentifier(), classRegistry, this.subject) }


fun startLesson(
    fetchScheduledLesson: FetchScheduledLesson,
    validateLessonStartTime: ValidateLessonStartTime,
    fetchClassRegistry: FetchClassRegistry
): StartLesson = { teacher, attemptedStartTime ->
    IO.fx {
        val (scheduledLessonOrError) =
            fetchScheduledLesson(teacher, attemptedStartTime)

// TODO introduce another internal error type (lesson started too early or so)

        val validatedScheduledLesson = scheduledLessonOrError
            .flatMap { validateLessonStartTime(it, attemptedStartTime) }

        val (startedLessonOrError) = validatedScheduledLesson
            .map { scheduledLesson ->
                fetchClassRegistry(scheduledLesson.className)
                    .map(scheduledLesson::toCurrentLessonWithClass)
            }.sequence()
        startedLessonOrError.flatten()

    }
}


fun validateStartTime(): ValidateLessonStartTime = { lesson, time ->
    when {
        time.isBefore(lesson.scheduledTime) -> Either.left(StartLessonError.NotScheduledLesson())
        time.isAfter(lesson.scheduledTime.plusMinutes(44)) -> Either.left(StartLessonError.NotScheduledLesson())
        else -> Either.right(lesson)
    }
}
