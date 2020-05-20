package com.krzykrucz.elesson.currentlesson.domain.startlesson

import arrow.core.Either
import arrow.core.flatMap
import com.krzykrucz.elesson.currentlesson.adapters.asyncFlatMap
import com.krzykrucz.elesson.currentlesson.domain.shared.ClassRegistry
import com.krzykrucz.elesson.currentlesson.domain.shared.LessonIdentifier
import com.krzykrucz.elesson.currentlesson.domain.shared.StartedLesson


private fun ScheduledLesson.lessonIdentifier() =
    LessonIdentifier(this.scheduledTime.toLocalDate(), this.lessonHourNumber, this.className)

private fun Either<StartLessonError, ClassRegistry>.toCurrentLessonWithClass(
    scheduledLesson: ScheduledLesson
): Either<StartLessonError, StartedLesson> =
    map { classRegistry ->
        StartedLesson(scheduledLesson.lessonIdentifier(), classRegistry, scheduledLesson.subject)
    }


fun startLesson(
    fetchScheduledLesson: FetchScheduledLesson,
    validateLessonStartTime: ValidateLessonStartTime,
    fetchClassRegistry: FetchClassRegistry
): StartLesson = { teacher, attemptedStartTime ->
    val scheduledLessonOrError = fetchScheduledLesson(teacher, attemptedStartTime)

// TODO introduce another internal error type (lesson started too early or so)

    val validatedScheduledLesson = scheduledLessonOrError
        .flatMap { validateLessonStartTime(it, attemptedStartTime) }

    validatedScheduledLesson
        .asyncFlatMap { scheduledLesson ->
            fetchClassRegistry(scheduledLesson.className)
                .toCurrentLessonWithClass(scheduledLesson)
        }
}

fun validateStartTime(): ValidateLessonStartTime = { lesson, time ->
    when {
        time.isBefore(lesson.scheduledTime) -> Either.left(StartLessonError.NotScheduledLesson())
        time.isAfter(lesson.scheduledTime.plusMinutes(44)) -> Either.left(StartLessonError.NotScheduledLesson())
        else -> Either.right(lesson)
    }
}
