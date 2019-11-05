package com.krzykrucz.elesson.currentlesson.topic.domain

import arrow.core.Either
import arrow.core.Option
import com.krzykrucz.elesson.currentlesson.shared.LessonHourNumber
import com.krzykrucz.elesson.currentlesson.shared.LessonTopic
import com.krzykrucz.elesson.currentlesson.shared.NaturalNumber
import com.krzykrucz.elesson.currentlesson.shared.TopicTitle
import java.time.LocalDate


data class LessonOrdinalNumber(val number: NaturalNumber)
data class FinishedLessonsCount(val count: NaturalNumber)
typealias IsAttendanceChecked = Boolean

data class InProgressLesson(val lessonTopic: LessonTopic) {
    fun lessonHourNumber(): Option<LessonHourNumber> = lessonTopic.run {
        LessonHourNumber.of(lessonOrdinalNumber.number)
    }
}

sealed class ChooseTopicError {
    data class AttendanceNotChecked(val error: String = "Cannot choose topic for lesson that does not have attendance checked") : ChooseTopicError()
}

typealias ChooseTopic = (IsAttendanceChecked, TopicTitle, FinishedLessonsCount, LocalDate) -> Either<ChooseTopicError, InProgressLesson>
