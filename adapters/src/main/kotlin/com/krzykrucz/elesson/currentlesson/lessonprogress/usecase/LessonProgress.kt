package com.krzykrucz.elesson.currentlesson.lessonprogress.usecase

import arrow.core.Option
import com.krzykrucz.elesson.currentlesson.shared.*
import java.time.LocalDate


typealias LessonDate = LocalDate

data class LessonProgress(
    val semester: Semester,
    val className: ClassName,
    val subject: LessonSubject,
    val date: LessonDate,
    val topic: Option<LessonTopic>,
    val status: LessonStatus
)
