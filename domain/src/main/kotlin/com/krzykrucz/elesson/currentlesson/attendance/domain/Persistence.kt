package com.krzykrucz.elesson.currentlesson.attendance.domain

import arrow.data.OptionT
import arrow.effects.ForIO
import arrow.effects.IO
import com.krzykrucz.elesson.currentlesson.shared.LessonIdentifier


typealias PersistAttendance = (Attendance) -> IO<Boolean>

typealias FetchCheckedAttendance = (LessonIdentifier) -> OptionT<ForIO, CheckedAttendance>

typealias FetchNotCompletedAttendance = (LessonIdentifier) -> OptionT<ForIO, NotCompletedAttendance>

