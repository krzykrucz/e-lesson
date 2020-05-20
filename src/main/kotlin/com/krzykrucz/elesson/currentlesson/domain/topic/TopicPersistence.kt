package com.krzykrucz.elesson.currentlesson.domain.topic

import com.krzykrucz.elesson.currentlesson.domain.shared.InProgressLesson
import com.krzykrucz.elesson.currentlesson.domain.shared.LessonIdentifier

typealias CountFinishedLessons = suspend () -> FinishedLessonsCount
typealias CheckIfAttendanceIsChecked = suspend (LessonIdentifier) -> Boolean
typealias PersistInProgressLesson = suspend (LessonIdentifier, InProgressLesson) -> Unit
