package com.krzykrucz.elesson.currentlesson.attendance.adapters.persistence

import arrow.core.toOption
import arrow.fx.IO
import com.krzykrucz.elesson.currentlesson.attendance.domain.*
import com.krzykrucz.elesson.currentlesson.monolith.Database


fun fetchCheckedAttendance(): FetchCheckedAttendance = { lessonId ->
    Database.LESSON_DATABASE[lessonId].toOption()
        .flatMap { (it.attendance as? CheckedAttendanceList).toOption() }
        .let { IO.just(it) }
}

fun persistAttendance(): PersistAttendance = { lessonId, attendance ->
    IO.just(
        Database.LESSON_DATABASE.compute(lessonId) { _, lesson ->
            lesson?.copy(attendance = attendance)
        }
    ).map {
        when (attendance) {
            is IncompleteAttendanceList -> false
            is CheckedAttendanceList -> true
        }
    }
}

fun fetchIncompleteAttendance(): FetchIncompleteAttendance = { lessonId ->
    Database.LESSON_DATABASE[lessonId].toOption()
        .flatMap {
            (it.attendance as? IncompleteAttendanceList).toOption()
                .map { incompleteAttendance -> IncompleteAttendanceDto(incompleteAttendance, it.classRegistry) }
        }
        .let { IO.just(it) }
}

