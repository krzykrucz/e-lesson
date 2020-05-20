package com.krzykrucz.elesson.currentlesson.domain.topic


import arrow.core.getOrElse
import arrow.core.maybe
import com.krzykrucz.elesson.currentlesson.domain.shared.InProgressLesson
import com.krzykrucz.elesson.currentlesson.domain.shared.LessonOrdinalInSemester
import com.krzykrucz.elesson.currentlesson.domain.shared.LessonTopic
import com.krzykrucz.elesson.currentlesson.domain.shared.NaturalNumber

val chooseTopic: ChooseTopic = { isAttendanceChecked, topicTitle, finishedLessonsCount, lessonId ->
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
