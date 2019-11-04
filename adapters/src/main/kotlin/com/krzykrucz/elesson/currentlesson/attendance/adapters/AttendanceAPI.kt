package com.krzykrucz.elesson.currentlesson.attendance.adapters

import com.krzykrucz.elesson.currentlesson.attendance.domain.AbsentStudent
import com.krzykrucz.elesson.currentlesson.attendance.domain.UncheckedStudent
import com.krzykrucz.elesson.currentlesson.shared.LessonIdentifier

data class AttendanceDto(
        val uncheckedStudent: UncheckedStudent,
        val lessonId: LessonIdentifier
)

data class AttendanceResponseDto(
        val checked: Boolean
)

data class LateAttendanceDto(
        val lessonId: LessonIdentifier,
        val absentStudent: AbsentStudent,
        val currentTime: String
)
