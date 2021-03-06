package com.krzykrucz.elesson.currentlesson.adapters.lessonprogress.rest

import com.krzykrucz.elesson.currentlesson.adapters.lessonprogress.usecase.LessonProgress
import com.krzykrucz.elesson.currentlesson.domain.shared.LessonTopic

data class LessonProgressDto(
    val semester: Int,
    val className: String,
    val subject: String,
    val date: String,
    val topic: LessonTopic?,
    val status: String
) {
    companion object {
        fun fromLessonProgress(lessonProgress: LessonProgress): LessonProgressDto =
            LessonProgressDto(
                semester = lessonProgress.semester.semesterOrdinalNumber.number,
                className = lessonProgress.className.name.text,
                subject = lessonProgress.subject.subject.text,
                date = lessonProgress.date.toString(),
                status = lessonProgress.status.status,
                topic = lessonProgress.topic.orNull()
            )
    }
}

