package com.krzykrucz.elesson.currentlesson.attendance

import com.krzykrucz.elesson.currentlesson.domain.attendance.AbsentStudent
import com.krzykrucz.elesson.currentlesson.domain.attendance.CheckedAttendance
import com.krzykrucz.elesson.currentlesson.domain.attendance.NotCompletedAttendance
import com.krzykrucz.elesson.currentlesson.domain.attendance.UncheckedStudent
import com.krzykrucz.elesson.currentlesson.domain.startlesson.ClassName
import com.krzykrucz.elesson.currentlesson.domain.startlesson.LessonIdentifier

data class AttendanceDto(
        val uncheckedStudent: UncheckedStudent,
        val notCompletedAttendance: NotCompletedAttendance,
        val className: ClassName
)

data class LateAttendanceDto(
        val lessonId: LessonIdentifier,
        val absentStudent: AbsentStudent,
        val checkedAttendance: CheckedAttendance,
        val currentTime: String
)

data class FinishAttendanceDto(
        val lessonId: LessonIdentifier,
        val checkedAttendance: CheckedAttendance
)