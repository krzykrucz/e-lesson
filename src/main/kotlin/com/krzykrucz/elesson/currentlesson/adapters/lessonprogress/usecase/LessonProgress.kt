package com.krzykrucz.elesson.currentlesson.adapters.lessonprogress.usecase

import arrow.core.Option
import com.krzykrucz.elesson.currentlesson.domain.shared.ClassName
import com.krzykrucz.elesson.currentlesson.domain.shared.LessonStatus
import com.krzykrucz.elesson.currentlesson.domain.shared.LessonSubject
import com.krzykrucz.elesson.currentlesson.domain.shared.LessonTopic
import com.krzykrucz.elesson.currentlesson.domain.shared.Semester
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
