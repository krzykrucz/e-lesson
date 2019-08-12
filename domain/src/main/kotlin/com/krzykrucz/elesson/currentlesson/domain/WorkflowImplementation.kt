package com.krzykrucz.elesson.currentlesson.domain

import arrow.core.*
import arrow.effects.IO


private fun ScheduledLesson.lessonIdentifier() =
        LessonIdentifier(this.scheduledTime.toLocalDate(), this.lessonHourNumber, this.className)

private fun ScheduledLesson.toCurrentLessonWithClass(classRegistry: ClassRegistry) =
        LessonBeforeAttendance(this.lessonIdentifier(), classRegistry)

val startLesson: StartLesson = { fetchClassRegistry, checkScheduledLesson, teacher, localDateTime ->
    checkScheduledLesson(teacher, localDateTime)
            .failIf({ scheduledLesson -> localDateTime.isBefore(scheduledLesson.scheduledTime) }, LessonError.NotScheduledLesson())
            .flatMapIfSuccess { scheduledLesson ->
                fetchClassRegistry(scheduledLesson.className)
                        .mapIfSuccess(scheduledLesson::toCurrentLessonWithClass)
            }
}

fun <A> AsyncOutput<A>.failIf(predicate: Predicate<A>, error: LessonError): AsyncOutput<A> {
    return this.map { either -> either.flatMap { a: A -> if (predicate(a)) Either.Left(error) else Either.Right(a) } }
}

fun <A, B> AsyncOutput<A>.mapIfSuccess(transformer: (A) -> B): AsyncOutput<B> {
    return this.map { either -> either.map(transformer) }
}

fun <A, B> AsyncOutput<A>.flatMapIfSuccess(transformer: (A) -> AsyncOutput<B>): AsyncOutput<B> {
    return this.flatMap { either ->
        either.map { transformer(it) }
                .getOrHandle { IO.just(Either.left(it)) }
    }
}
