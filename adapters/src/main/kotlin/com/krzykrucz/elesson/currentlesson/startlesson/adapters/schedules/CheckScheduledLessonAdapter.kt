package com.krzykrucz.elesson.currentlesson.startlesson.adapters.schedules

import arrow.core.Either
import arrow.core.getOrElse
import arrow.core.right
import arrow.fx.IO
import com.krzykrucz.elesson.currentlesson.shared.*
import com.krzykrucz.elesson.currentlesson.startlesson.domain.FetchScheduledLesson
import com.krzykrucz.elesson.currentlesson.startlesson.domain.ScheduledLesson
import com.krzykrucz.elesson.currentlesson.startlesson.domain.StartLessonError
import java.time.LocalDateTime


class LessonSchedulesClient : FetchScheduledLesson {
    override fun invoke(teacher: Teacher, time: LocalDateTime): IO<Either<StartLessonError.NotScheduledLesson, ScheduledLesson>> =
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
            .map { IO { it.right() } }
            .getOrElse { IO { Either.left(StartLessonError.NotScheduledLesson()) } }
}
