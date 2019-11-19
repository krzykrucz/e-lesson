package com.krzykrucz.elesson.currentlesson.domain.topic

import arrow.fx.IO
import com.krzykrucz.elesson.currentlesson.domain.shared.InProgressLesson
import com.krzykrucz.elesson.currentlesson.domain.shared.LessonIdentifier

typealias CountFinishedLessons = () -> IO<FinishedLessonsCount>
typealias CheckIfAttendanceIsChecked = (LessonIdentifier) -> IO<Boolean>
typealias PersistInProgressLesson = (LessonIdentifier, InProgressLesson) -> IO<Unit>
