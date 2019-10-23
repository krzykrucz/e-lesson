package com.krzykrucz.elesson.currentlesson.attendance.domain

import arrow.core.Tuple2
import arrow.data.OptionT
import arrow.effects.ForIO
import arrow.effects.IO
import com.krzykrucz.elesson.currentlesson.shared.ClassRegistry
import com.krzykrucz.elesson.currentlesson.shared.LessonIdentifier


typealias PersistAttendance = (LessonIdentifier, Attendance) -> IO<Boolean>

typealias FetchCheckedAttendance = (LessonIdentifier) -> OptionT<ForIO, CheckedAttendanceList>

typealias FetchNotCompletedAttendanceAndRegistry = (LessonIdentifier) -> OptionT<ForIO, Tuple2<IncompleteAttendanceList, ClassRegistry>>

