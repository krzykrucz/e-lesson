package com.krzykrucz.elesson.currentlesson.topic.domain

import arrow.fx.IO
import com.krzykrucz.elesson.currentlesson.shared.InProgressLesson
import com.krzykrucz.elesson.currentlesson.shared.LessonIdentifier

typealias CountFinishedLessons = () -> IO<FinishedLessonsCount>
typealias CheckIfAttendanceIsChecked = (LessonIdentifier) -> IO<Boolean>
typealias PersistInProgressLesson = (LessonIdentifier, InProgressLesson) -> IO<Unit>
