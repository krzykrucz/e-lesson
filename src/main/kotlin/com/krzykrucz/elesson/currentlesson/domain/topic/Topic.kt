package com.krzykrucz.elesson.currentlesson.domain.topic

import arrow.core.Either
import com.krzykrucz.elesson.currentlesson.domain.shared.InProgressLesson
import com.krzykrucz.elesson.currentlesson.domain.shared.LessonIdentifier
import com.krzykrucz.elesson.currentlesson.domain.shared.TopicTitle

data class FinishedLessonsCount(val count: Int)
typealias IsAttendanceChecked = Boolean

sealed class ChooseTopicError {
    data class AttendanceNotChecked(val error: String = "Cannot choose topic for lesson that does not have attendance checked") : ChooseTopicError()
}

typealias ChooseTopic =
    (IsAttendanceChecked, TopicTitle, FinishedLessonsCount, LessonIdentifier) -> Either<ChooseTopicError, InProgressLesson>
