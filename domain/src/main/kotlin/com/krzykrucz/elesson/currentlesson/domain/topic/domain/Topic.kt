package com.krzykrucz.elesson.currentlesson.domain.topic.domain

import com.krzykrucz.elesson.currentlesson.domain.NaturalNumber
import com.krzykrucz.elesson.currentlesson.domain.NonEmptyText
import com.krzykrucz.elesson.currentlesson.domain.attendance.CheckedAttendance
import com.krzykrucz.elesson.currentlesson.domain.startlesson.LessonIdentifier
import java.time.LocalDate

data class LessonOrdinalNumber(val number: NaturalNumber)
data class FinishedLessonsCount(val count: NaturalNumber)
data class TopicTitle(val title: NonEmptyText)
data class LessonTopic(val lessonOrdinalNumber: LessonOrdinalNumber, val topicTitle: TopicTitle, val date: LocalDate)
data class InProgressLesson(val lessonIdentifier: LessonIdentifier, val lessonTopic: LessonTopic, val attendance: CheckedAttendance)
typealias ChooseTopic = (TopicTitle, FinishedLessonsCount, CheckedAttendance) -> InProgressLesson
