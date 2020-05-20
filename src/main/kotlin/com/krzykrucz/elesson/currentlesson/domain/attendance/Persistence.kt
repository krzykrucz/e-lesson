package com.krzykrucz.elesson.currentlesson.domain.attendance

import arrow.core.Option
import com.krzykrucz.elesson.currentlesson.domain.shared.ClassRegistry
import com.krzykrucz.elesson.currentlesson.domain.shared.LessonIdentifier


typealias PersistAttendance = suspend (LessonIdentifier, Attendance) -> Boolean
typealias FetchCheckedAttendance = suspend (LessonIdentifier) -> Option<CheckedAttendanceList>
typealias FetchIncompleteAttendance = suspend (LessonIdentifier) -> Option<IncompleteAttendanceDto>

class IncompleteAttendanceDto(
    val incompleteAttendanceList: IncompleteAttendanceList,
    val classRegistry: ClassRegistry
)
