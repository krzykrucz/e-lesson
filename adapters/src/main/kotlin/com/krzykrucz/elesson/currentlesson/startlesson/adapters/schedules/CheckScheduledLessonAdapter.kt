package com.krzykrucz.elesson.currentlesson.startlesson.adapters.schedules

import arrow.core.Either
import arrow.core.getOrElse
import arrow.effects.IO
import com.krzykrucz.elesson.currentlesson.shared.*
import com.krzykrucz.elesson.currentlesson.startlesson.domain.CheckScheduledLesson
import com.krzykrucz.elesson.currentlesson.startlesson.domain.ScheduledLesson
import java.time.LocalDateTime

class NoSuchLessonError : RuntimeException("NoSuchLessonError")

class LessonSchedulesClient : CheckScheduledLesson {
    override fun invoke(teacher: Teacher, time: LocalDateTime): AsyncOutput<ScheduledLesson, Throwable> =
        LessonHourNumber.of(NaturalNumber.THREE)
            .map {
                ScheduledLesson(
                        time,
                        it,
                        teacher,
                        ClassName(NonEmptyText.of("Gryffindor")!!),
                        LessonSubject(NonEmptyText("Elixirs"))
                )
            }
            .map { IO { Either.right(it) } }
            .getOrElse { IO { Either.left(NoSuchLessonError()) } }
}
