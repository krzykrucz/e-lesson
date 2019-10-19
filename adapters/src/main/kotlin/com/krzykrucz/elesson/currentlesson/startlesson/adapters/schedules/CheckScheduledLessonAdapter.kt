package com.krzykrucz.elesson.currentlesson.startlesson.adapters.schedules

import arrow.core.Either
import arrow.core.getOrElse
import arrow.effects.IO
import com.krzykrucz.elesson.currentlesson.shared.AsyncOutput
import com.krzykrucz.elesson.currentlesson.shared.ClassName
import com.krzykrucz.elesson.currentlesson.shared.LessonHourNumber
import com.krzykrucz.elesson.currentlesson.shared.NaturalNumber
import com.krzykrucz.elesson.currentlesson.shared.NonEmptyText
import com.krzykrucz.elesson.currentlesson.shared.Teacher
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
                        ClassName(NonEmptyText.of("Gryffindor")!!)
                )
            }
            .map { IO { Either.right(it) } }
            .getOrElse { IO { Either.left(NoSuchLessonError()) } }
}