package com.krzykrucz.elesson.currentlesson.domain.topic

import arrow.core.Either
import com.krzykrucz.elesson.currentlesson.domain.NonEmptyText
import com.krzykrucz.elesson.currentlesson.domain.startlesson.LessonIdentifier

data class TopicTitle(val title: NonEmptyText)

data class LessonTopic(val topicTitle: TopicTitle)
data class InProgressLesson(val lessonIdentifier: LessonIdentifier, val lessonTopic: LessonTopic)

sealed class ChooseTopicError {
    data class AttendanceIsNotChecked(val error: String = "Attendance is not checked") : ChooseTopicError()
}

typealias IsAttendanceChecked = (LessonIdentifier) -> Boolean
typealias ChooseTopic = (TopicTitle, LessonIdentifier) -> Either<ChooseTopicError, InProgressLesson>
