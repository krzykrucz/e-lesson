package com.krzykrucz.elesson.currentlesson.domain.topic.domain

import com.krzykrucz.elesson.currentlesson.domain.NaturalNumber
import com.krzykrucz.elesson.currentlesson.domain.NonEmptyText
import com.krzykrucz.elesson.currentlesson.domain.attendance.CheckedAttendance
import com.krzykrucz.elesson.currentlesson.domain.startlesson.LessonIdentifier

data class FinishedLessonsCount(val count: NaturalNumber)
data class TopicTitle(val title: NonEmptyText)
data class LessonTopic(val topicTitle: TopicTitle)
data class InProgressLesson(val lessonIdentifier: LessonIdentifier, val lessonTopic: LessonTopic, val lessonNumber: NaturalNumber)
typealias ChooseTopic = (TopicTitle, FinishedLessonsCount, CheckedAttendance) -> InProgressLesson
