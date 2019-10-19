package com.krzykrucz.elesson.currentlesson.domain.topic.domain

import arrow.core.getOrElse
import com.krzykrucz.elesson.currentlesson.domain.NaturalNumber
import com.krzykrucz.elesson.currentlesson.domain.startlesson.LessonIdentifier

fun chooseTopic(): ChooseTopic = { topicTitle, finishedLessonsCount, checkedAttendance ->
    val attendance = checkedAttendance.attendance
    InProgressLesson(
            LessonIdentifier(
                    date = attendance.date,
                    lessonHourNumber = attendance.lessonHourNumber,
                    className = attendance.className
            ),
            LessonTopic(topicTitle),
            NaturalNumber.of(finishedLessonsCount.count.number + NaturalNumber.ONE.number)
                    .getOrElse { NaturalNumber.ONE }
    )
}