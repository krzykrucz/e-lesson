package com.krzykrucz.elesson.currentlesson.domain.topic

import arrow.core.left
import arrow.core.right

fun chooseTopic(isAttendanceChecked: IsAttendanceChecked): ChooseTopic = { topicTitle, lessonIdentifier ->
    if (isAttendanceChecked(lessonIdentifier)) {
        InProgressLesson(lessonIdentifier, LessonTopic(topicTitle)).right()
    } else {
        ChooseTopicError.AttendanceIsNotChecked().left()
    }
}