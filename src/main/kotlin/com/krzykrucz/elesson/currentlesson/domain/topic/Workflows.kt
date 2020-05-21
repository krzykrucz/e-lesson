package com.krzykrucz.elesson.currentlesson.domain.topic

import arrow.core.Either
import arrow.core.getOrElse
import arrow.core.maybe
import com.krzykrucz.elesson.currentlesson.domain.shared.InProgressLesson
import com.krzykrucz.elesson.currentlesson.domain.shared.LessonIdentifier
import com.krzykrucz.elesson.currentlesson.domain.shared.LessonOrdinalInSemester
import com.krzykrucz.elesson.currentlesson.domain.shared.LessonTopic
import com.krzykrucz.elesson.currentlesson.domain.shared.NaturalNumber
import com.krzykrucz.elesson.currentlesson.domain.shared.TopicTitle


typealias CountFinishedLessons = suspend () -> FinishedLessonsCount
typealias CheckIfAttendanceIsChecked = suspend (LessonIdentifier) -> Boolean

typealias ChooseTopic = suspend (TopicTitle, LessonIdentifier) -> Either<ChooseTopicError, InProgressLesson>

fun chooseTopicWorkflow(
    checkIfAttendanceIsChecked: CheckIfAttendanceIsChecked,
    countFinishedLessons: CountFinishedLessons
): ChooseTopic = { topicTitle, lessonId ->
    val isAttendanceChecked = checkIfAttendanceIsChecked(lessonId)
    val finishedLessonsCount = countFinishedLessons()
    isAttendanceChecked.maybe {
        (finishedLessonsCount.count + 1)
            .let { NaturalNumber.of(it).getOrElse { NaturalNumber.ONE } }
            .let { LessonOrdinalInSemester(it) }
            .let { lessonOrdinalNumber ->
                LessonTopic(
                    lessonOrdinalNumber,
                    topicTitle,
                    lessonId.date
                )
            }
            .let { topic -> InProgressLesson(lessonId, topic) }
    }.toEither { ChooseTopicError.AttendanceNotChecked() }
}
