package com.krzykrucz.elesson.currentlesson.attendance

import arrow.core.toOption
import com.krzykrucz.elesson.currentlesson.Database

val fetchCheckedAttendance: FetchCheckedAttendance = { lessonId ->
    Database.LESSON_DATABASE[lessonId].toOption()
        .flatMap { (it.attendance as? CheckedAttendanceList).toOption() }
}

val persistAttendance: PersistAttendance = { lessonId, attendance ->
    Database.LESSON_DATABASE.compute(lessonId) { _, lesson ->
        lesson?.copy(attendance = attendance)
    }.let {
        when (attendance) {
            is IncompleteAttendanceList -> false
            is CheckedAttendanceList -> true
        }
    }
}

val fetchIncompleteAttendance: FetchIncompleteAttendance = { lessonId ->
    Database.LESSON_DATABASE[lessonId].toOption()
        .flatMap {
            (it.attendance as? IncompleteAttendanceList).toOption()
                .map { incompleteAttendance ->
                    IncompleteAttendanceDto(
                        incompleteAttendance,
                        it.classRegistry
                    )
                }
        }
}
