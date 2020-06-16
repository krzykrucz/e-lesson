package com.krzykrucz.elesson.currentlesson.topic

import arrow.core.Either
import arrow.core.getOrElse
import arrow.core.maybe
import com.krzykrucz.elesson.currentlesson.shared.InProgressLesson
import com.krzykrucz.elesson.currentlesson.shared.LessonIdentifier
import com.krzykrucz.elesson.currentlesson.shared.LessonOrdinalInSemester
import com.krzykrucz.elesson.currentlesson.shared.LessonTopic
import com.krzykrucz.elesson.currentlesson.shared.NaturalNumber
import com.krzykrucz.elesson.currentlesson.shared.TopicTitle


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
