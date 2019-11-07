package com.krzykrucz.elesson.currentlesson.topic.domain

import arrow.core.Either
import arrow.core.Option
import com.krzykrucz.elesson.currentlesson.shared.InProgressLesson
import com.krzykrucz.elesson.currentlesson.shared.LessonHourNumber
import com.krzykrucz.elesson.currentlesson.shared.LessonOrdinalNumber
import com.krzykrucz.elesson.currentlesson.shared.LessonTopic
import com.krzykrucz.elesson.currentlesson.shared.NonEmptyText
import com.krzykrucz.elesson.currentlesson.shared.TopicTitle
import java.time.LocalDate

data class FinishedLessonsCount(val count: Int)
typealias IsAttendanceChecked = Boolean

sealed class ChooseTopicError {
    data class AttendanceNotChecked(val error: String = "Cannot choose topic for lesson that does not have attendance checked") : ChooseTopicError()
}

typealias ChooseTopic = (IsAttendanceChecked, TopicTitle, FinishedLessonsCount, LocalDate) -> Either<ChooseTopicError, InProgressLesson>
