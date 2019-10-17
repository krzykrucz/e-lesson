package com.krzykrucz.elesson.currentlesson.domain.topic

import com.krzykrucz.elesson.currentlesson.domain.NonEmptyText
import com.krzykrucz.elesson.currentlesson.domain.attendance.CheckedAttendance
import com.krzykrucz.elesson.currentlesson.domain.startlesson.LessonIdentifier

data class TopicTitle(val title: NonEmptyText)
data class LessonTopic(val topicTitle: TopicTitle)
data class InProgressLesson(val lessonIdentifier: LessonIdentifier, val lessonTopic: LessonTopic)
typealias ChooseTopic = (TopicTitle, CheckedAttendance) -> InProgressLesson
