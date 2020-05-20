package com.krzykrucz.elesson.currentlesson.adapters.startlesson.schedules

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


val lessonSchedulesClient: FetchScheduledLesson = { teacher: Teacher, time: LocalDateTime ->
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
        .toEither { StartLessonError.NotScheduledLesson() }
}

