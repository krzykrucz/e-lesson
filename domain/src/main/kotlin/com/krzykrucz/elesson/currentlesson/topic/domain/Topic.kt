package com.krzykrucz.elesson.currentlesson.topic.domain

import arrow.core.Either
import com.krzykrucz.elesson.currentlesson.shared.LessonTopic
import com.krzykrucz.elesson.currentlesson.shared.TopicTitle
import java.time.LocalDate


data class FinishedLessonsCount(val count: Int)
typealias IsAttendanceChecked = Boolean

data class InProgressLesson(val lessonTopic: LessonTopic)

sealed class ChooseTopicError {
    data class AttendanceNotChecked(val error: String = "Cannot choose topic for lesson that does not have attendance checked") : ChooseTopicError()
}

typealias ChooseTopic = (IsAttendanceChecked, TopicTitle, FinishedLessonsCount, LocalDate) -> Either<ChooseTopicError, InProgressLesson>
