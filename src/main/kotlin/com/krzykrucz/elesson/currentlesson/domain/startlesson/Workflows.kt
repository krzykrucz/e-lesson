package com.krzykrucz.elesson.currentlesson.domain.startlesson

import arrow.core.Either
import arrow.core.flatMap
import com.krzykrucz.elesson.currentlesson.adapters.asyncFlatMap
import com.krzykrucz.elesson.currentlesson.domain.shared.ClassName
import com.krzykrucz.elesson.currentlesson.domain.shared.ClassRegistry
import com.krzykrucz.elesson.currentlesson.domain.shared.LessonIdentifier
import com.krzykrucz.elesson.currentlesson.domain.shared.StartedLesson
import com.krzykrucz.elesson.currentlesson.domain.shared.Teacher
import java.time.LocalDateTime


typealias FetchScheduledLesson = suspend (Teacher, LocalDateTime) -> Either<StartLessonError, ScheduledLesson>
typealias FetchClassRegistry = suspend (ClassName) -> Either<StartLessonError, ClassRegistry>// TODO can be removed if class is an aggregate root

typealias ValidateLessonStartTime = (ScheduledLesson, LocalDateTime) -> Either<StartLessonError, ValidatedScheduledLesson>

private fun validateStartTime(): ValidateLessonStartTime = { lesson, time ->
    when {
        time.isBefore(lesson.scheduledTime) -> Either.left(StartLessonError.NotScheduledLesson())
        time.isAfter(lesson.scheduledTime.plusMinutes(44)) -> Either.left(StartLessonError.NotScheduledLesson())
        else -> Either.right(lesson)
    }
}

typealias StartLesson = suspend (Teacher, LessonStartTime) -> Either<StartLessonError, StartedLesson>

fun startLessonWorkflow(
    fetchScheduledLesson: FetchScheduledLesson,
    fetchClassRegistry: FetchClassRegistry,
    validateLessonStartTime: ValidateLessonStartTime = validateStartTime()
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

private fun ScheduledLesson.lessonIdentifier() =
    LessonIdentifier(this.scheduledTime.toLocalDate(), this.lessonHourNumber, this.className)

private fun Either<StartLessonError, ClassRegistry>.toCurrentLessonWithClass(
    scheduledLesson: ScheduledLesson
): Either<StartLessonError, StartedLesson> =
    map { classRegistry ->
        StartedLesson(scheduledLesson.lessonIdentifier(), classRegistry, scheduledLesson.subject)
    }
