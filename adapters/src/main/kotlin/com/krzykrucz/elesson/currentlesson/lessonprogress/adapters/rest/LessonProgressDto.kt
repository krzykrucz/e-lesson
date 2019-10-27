package com.krzykrucz.elesson.currentlesson.lessonprogress.adapters.rest

import com.krzykrucz.elesson.currentlesson.lessonprogress.LessonProgress
import com.krzykrucz.elesson.currentlesson.shared.LessonTopic

data class LessonProgressDto(
    val semester: Int,
    val className: String,
    val subject: String,
    val date: String,
    val topic: LessonTopic?,
    val status: String
)

fun LessonProgress.toDto() =
    LessonProgressDto(
        semester = this.semester.semesterOrdinalNumber.number,
        className = this.className.name.text,
        subject = this.subject.subject.text,
        date = this.date.toString(),
        status = this.status.status,
        topic = this.topic.orNull()
    )
