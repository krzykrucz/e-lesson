package com.krzykrucz.elesson.currentlesson.topic.domain


import arrow.core.getOrElse
import arrow.core.maybe
import com.krzykrucz.elesson.currentlesson.shared.LessonOrdinalNumber
import com.krzykrucz.elesson.currentlesson.shared.LessonTopic
import com.krzykrucz.elesson.currentlesson.shared.NaturalNumber

fun chooseTopic(): ChooseTopic = { isAttendanceChecked, topicTitle, finishedLessonsCount, date ->
    isAttendanceChecked.maybe {
        (finishedLessonsCount.count + 1)
            .let { NaturalNumber.of(it).getOrElse { NaturalNumber.ONE } }
            .let { LessonOrdinalNumber(it) }
            .let { lessonOrdinalNumber ->
                InProgressLesson(
                    LessonTopic(lessonOrdinalNumber, topicTitle, date)
                )
            }
    }.toEither { ChooseTopicError.AttendanceNotChecked() }
}
