package com.krzykrucz.elesson.currentlesson.adapters.attendance.persistence

import arrow.core.toOption
import arrow.fx.IO
import com.krzykrucz.elesson.currentlesson.infrastructure.Database
import com.krzykrucz.elesson.currentlesson.domain.attendance.CheckedAttendanceList
import com.krzykrucz.elesson.currentlesson.domain.attendance.FetchCheckedAttendance
import com.krzykrucz.elesson.currentlesson.domain.attendance.FetchIncompleteAttendance
import com.krzykrucz.elesson.currentlesson.domain.attendance.IncompleteAttendanceDto
import com.krzykrucz.elesson.currentlesson.domain.attendance.IncompleteAttendanceList
import com.krzykrucz.elesson.currentlesson.domain.attendance.PersistAttendance
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AttendanceDatabaseConnector {

    @Bean
    fun fetchCheckedAttendance(): FetchCheckedAttendance = { lessonId ->
        Database.LESSON_DATABASE[lessonId].toOption()
            .flatMap { (it.attendance as? CheckedAttendanceList).toOption() }
            .let { IO.just(it) }
    }

    @Bean
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

    @Bean
    fun fetchIncompleteAttendance(): FetchIncompleteAttendance = { lessonId ->
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
            .let { IO.just(it) }
    }
}

