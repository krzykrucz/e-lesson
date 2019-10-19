package com.krzykrucz.elesson.currentlesson.attendance.domain

import arrow.data.OptionT
import arrow.effects.ForIO
import arrow.effects.IO
import com.krzykrucz.elesson.currentlesson.shared.ClassRegistry
import com.krzykrucz.elesson.currentlesson.shared.LessonIdentifier
import com.krzykrucz.elesson.currentlesson.startlesson.domain.StartedLesson


typealias PersistAttendance = (Attendance) -> IO<Boolean>
typealias FetchAttendance = (LessonIdentifier) -> OptionT<ForIO, Attendance>
typealias FetchStartedLesson = (LessonIdentifier) -> OptionT<ForIO, StartedLesson>
typealias FetchClassRegistry = (Attendance) -> OptionT<ForIO, ClassRegistry>
typealias FetchStartedLessonAsAttendance = (LessonIdentifier) -> OptionT<ForIO, NotCompletedAttendance>
