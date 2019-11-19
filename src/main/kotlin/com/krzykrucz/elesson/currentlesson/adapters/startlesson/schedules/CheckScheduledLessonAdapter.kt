package com.krzykrucz.elesson.currentlesson.adapters.startlesson.schedules

import arrow.core.Either
import arrow.core.getOrElse
import arrow.core.right
import arrow.fx.IO
import com.krzykrucz.elesson.currentlesson.domain.shared.ClassName
import com.krzykrucz.elesson.currentlesson.domain.shared.LessonHourNumber
import com.krzykrucz.elesson.currentlesson.domain.shared.LessonSubject
import com.krzykrucz.elesson.currentlesson.domain.shared.NaturalNumber
import com.krzykrucz.elesson.currentlesson.domain.shared.NonEmptyText
import com.krzykrucz.elesson.currentlesson.domain.shared.Teacher
import com.krzykrucz.elesson.currentlesson.domain.startlesson.FetchScheduledLesson
import com.krzykrucz.elesson.currentlesson.domain.startlesson.ScheduledLesson
import com.krzykrucz.elesson.currentlesson.domain.startlesson.StartLessonError
import java.time.LocalDateTime


class LessonSchedulesClient : FetchScheduledLesson {
    override fun invoke(teacher: Teacher, time: LocalDateTime): IO<Either<StartLessonError.NotScheduledLesson, ScheduledLesson>> =
        LessonHourNumber.of(NaturalNumber.THREE)
            .map {
                ScheduledLesson(
                    time,
                    it,
                    teacher,
                    ClassName(
                        NonEmptyText.of(
                            "Gryffindor"
                        )!!
                    ),
                    LessonSubject(
                        NonEmptyText(
                            "Elixirs"
                        )
                    )
                )
            }
            .map { IO { it.right() } }
            .getOrElse { IO { Either.left(StartLessonError.NotScheduledLesson()) } }
}
