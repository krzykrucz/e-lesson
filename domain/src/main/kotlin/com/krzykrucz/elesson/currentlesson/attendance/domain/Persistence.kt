package com.krzykrucz.elesson.currentlesson.attendance.domain

import arrow.core.Option
import arrow.fx.IO
import com.krzykrucz.elesson.currentlesson.shared.ClassRegistry
import com.krzykrucz.elesson.currentlesson.shared.LessonIdentifier


typealias PersistAttendance = (LessonIdentifier, Attendance) -> IO<Boolean>
typealias FetchCheckedAttendance = (LessonIdentifier) -> IO<Option<CheckedAttendanceList>>
typealias FetchIncompleteAttendance = (LessonIdentifier) -> IO<Option<IncompleteAttendanceDto>>

class IncompleteAttendanceDto(
    val incompleteAttendanceList: IncompleteAttendanceList,
    val classRegistry: ClassRegistry
)
