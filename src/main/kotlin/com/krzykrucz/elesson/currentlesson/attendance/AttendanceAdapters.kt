package com.krzykrucz.elesson.currentlesson.attendance

import org.springframework.context.support.beans

val attendanceAdapters = beans {
    bean {
        attendanceRouter(
            handleNoteAbsentDto(
                persistAttendance,
                fetchIncompleteAttendance
            ),
            handleNotePresentDto(
                persistAttendance,
                fetchIncompleteAttendance
            ),
            handleLateAttendanceDto(
                persistAttendance,
                fetchCheckedAttendance
            )
        )
    }
}
