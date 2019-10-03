package com.krzykrucz.elesson.currentlesson.attendance

import arrow.core.Option
import arrow.core.toOption
import com.krzykrucz.elesson.currentlesson.domain.attendance.Attendance
import com.krzykrucz.elesson.currentlesson.domain.attendance.AttendanceList
import com.krzykrucz.elesson.currentlesson.domain.attendance.NotCompletedAttendance
import com.krzykrucz.elesson.currentlesson.domain.startlesson.LessonIdentifier
import com.krzykrucz.elesson.currentlesson.infrastructure.Database

class AttendanceRepository {
    fun getAttendance(lessonIdentifier: LessonIdentifier): Option<Attendance> =
            Database.STARTED_LESSON_DATABASE.get(lessonIdentifier).toOption()
                    .map { NotCompletedAttendance(
                            attendance = AttendanceList(
                                    className = it.id.className,
                                    lessonHourNumber = it.id.lessonHourNumber,
                                    date = it.id.date
                            )
                    )}
}