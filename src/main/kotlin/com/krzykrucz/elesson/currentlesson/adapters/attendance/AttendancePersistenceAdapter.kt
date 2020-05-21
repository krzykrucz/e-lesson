package com.krzykrucz.elesson.currentlesson.adapters.attendance

import arrow.core.toOption
import com.krzykrucz.elesson.currentlesson.Database
import com.krzykrucz.elesson.currentlesson.domain.attendance.CheckedAttendanceList
import com.krzykrucz.elesson.currentlesson.domain.attendance.FetchCheckedAttendance
import com.krzykrucz.elesson.currentlesson.domain.attendance.FetchIncompleteAttendance
import com.krzykrucz.elesson.currentlesson.domain.attendance.IncompleteAttendanceDto
import com.krzykrucz.elesson.currentlesson.domain.attendance.IncompleteAttendanceList
import com.krzykrucz.elesson.currentlesson.domain.attendance.PersistAttendance
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AttendancePersistenceAdapter {

    @Bean
    fun fetchCheckedAttendance(): FetchCheckedAttendance = { lessonId ->
        Database.LESSON_DATABASE[lessonId].toOption()
            .flatMap { (it.attendance as? CheckedAttendanceList).toOption() }
    }

    @Bean
    fun persistAttendance(): PersistAttendance = { lessonId, attendance ->
        Database.LESSON_DATABASE.compute(lessonId) { _, lesson ->
            lesson?.copy(attendance = attendance)
        }.let {
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
    }
}

