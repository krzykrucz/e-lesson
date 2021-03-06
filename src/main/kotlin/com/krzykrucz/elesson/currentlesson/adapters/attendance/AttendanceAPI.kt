package com.krzykrucz.elesson.currentlesson.adapters.attendance

import com.krzykrucz.elesson.currentlesson.domain.attendance.AbsentStudent
import com.krzykrucz.elesson.currentlesson.domain.attendance.UncheckedStudent
import com.krzykrucz.elesson.currentlesson.domain.shared.LessonIdentifier

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
