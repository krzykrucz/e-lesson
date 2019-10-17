package com.krzykrucz.elesson.currentlesson.domain.topic

import com.krzykrucz.elesson.currentlesson.domain.startlesson.LessonIdentifier

fun chooseTopic(): ChooseTopic = { topicTitle, checkedAttendance ->
    val attendance = checkedAttendance.attendance
    InProgressLesson(LessonIdentifier(
            date = attendance.date,
            lessonHourNumber = attendance.lessonHourNumber,
            className = attendance.className
    ), LessonTopic(topicTitle))
}