package com.krzykrucz.elesson.currentlesson.attendance

import arrow.core.Option
import com.krzykrucz.elesson.currentlesson.shared.ClassRegistry
import com.krzykrucz.elesson.currentlesson.shared.LessonIdentifier


typealias PersistAttendance = suspend (LessonIdentifier, Attendance) -> Boolean
typealias FetchCheckedAttendance = suspend (LessonIdentifier) -> Option<CheckedAttendanceList>
typealias FetchIncompleteAttendance = suspend (LessonIdentifier) -> Option<IncompleteAttendanceDto>

class IncompleteAttendanceDto(
    val incompleteAttendanceList: IncompleteAttendanceList,
    val classRegistry: ClassRegistry
)
