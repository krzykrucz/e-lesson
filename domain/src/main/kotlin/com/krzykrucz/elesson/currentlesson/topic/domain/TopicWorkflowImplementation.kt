package com.krzykrucz.elesson.currentlesson.topic.domain


import arrow.core.getOrElse
import arrow.core.maybe
import com.krzykrucz.elesson.currentlesson.shared.InProgressLesson
import com.krzykrucz.elesson.currentlesson.shared.LessonOrdinalInSemester
import com.krzykrucz.elesson.currentlesson.shared.LessonTopic
import com.krzykrucz.elesson.currentlesson.shared.NaturalNumber

fun chooseTopic(): ChooseTopic = { isAttendanceChecked, topicTitle, finishedLessonsCount, lessonId ->
    isAttendanceChecked.maybe {
        (finishedLessonsCount.count + 1)
            .let { NaturalNumber.of(it).getOrElse { NaturalNumber.ONE } }
            .let { LessonOrdinalInSemester(it) }
            .let { lessonOrdinalNumber -> LessonTopic(lessonOrdinalNumber, topicTitle, lessonId.date) }
            .let { topic -> InProgressLesson(lessonId, topic) }
    }.toEither { ChooseTopicError.AttendanceNotChecked() }
}
