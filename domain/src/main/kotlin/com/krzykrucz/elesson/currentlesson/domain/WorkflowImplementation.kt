package com.krzykrucz.elesson.currentlesson.domain

import arrow.core.Either
import arrow.core.EitherOf
import arrow.core.Predicate
import arrow.effects.IO

private fun ScheduledLesson.lessonIdentifier() =
        LessonIdentifier(this.scheduledTime.toLocalDate(), this.lessonHourNumber, this.className)

private fun ScheduledLesson.toCurrentLessonWithClass(classRegistry: ClassRegistry) =
        LessonBeforeAttendance(this.lessonIdentifier(), classRegistry)

val startLesson: StartLesson = { fetchClassRegistry, checkScheduledLesson, teacher, localDateTime ->
    checkScheduledLesson(teacher, localDateTime)
            .failIf ({scheduledLesson -> localDateTime.isBefore(scheduledLesson.scheduledTime) }, "Cannot start a lesson outside of a lesson hour for which it's scheduled")
            .zipWith({ fetchClassRegistry(it.className) },
                    ScheduledLesson::toCurrentLessonWithClass)
}

fun <A, B, R> IO<A>.zipWith(another: (A) -> IO<B>, combiner: (A, B) -> R): IO<R> {
    return this.flatMap { a: A -> another(a).map { b: B -> combiner(a, b) } }
}
fun <A> IO<A>.failIf(predicate: Predicate<A>, error: String): IO<Either<String, A>> {
    return this.map { if (predicate(it)) Either.Left(error) else Either.Right(it) }
}