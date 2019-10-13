package com.krzykrucz.elesson.currentlesson.startlesson

import arrow.core.Either
import arrow.core.getOrElse
import arrow.effects.IO
import com.krzykrucz.elesson.currentlesson.domain.AsyncOutput
import com.krzykrucz.elesson.currentlesson.domain.NaturalNumber
import com.krzykrucz.elesson.currentlesson.domain.NonEmptyText
import com.krzykrucz.elesson.currentlesson.domain.startlesson.CheckScheduledLesson
import com.krzykrucz.elesson.currentlesson.domain.startlesson.ClassName
import com.krzykrucz.elesson.currentlesson.domain.startlesson.LessonHourNumber
import com.krzykrucz.elesson.currentlesson.domain.startlesson.ScheduledLesson
import com.krzykrucz.elesson.currentlesson.domain.startlesson.Teacher
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