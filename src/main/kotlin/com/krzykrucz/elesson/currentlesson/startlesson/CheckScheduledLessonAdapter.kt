package com.krzykrucz.elesson.currentlesson.startlesson

import com.krzykrucz.elesson.currentlesson.shared.ClassName
import com.krzykrucz.elesson.currentlesson.shared.LessonHourNumber
import com.krzykrucz.elesson.currentlesson.shared.LessonSubject
import com.krzykrucz.elesson.currentlesson.shared.NaturalNumber
import com.krzykrucz.elesson.currentlesson.shared.NonEmptyText
import com.krzykrucz.elesson.currentlesson.shared.Teacher
import java.time.LocalDateTime


internal val checkScheduledLessonAdapter: FetchScheduledLesson = { teacher: Teacher, time: LocalDateTime ->
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

