package com.krzykrucz.elesson.currentlesson.attendance.adapters.persistence

import arrow.effects.IO
import com.krzykrucz.elesson.currentlesson.attendance.domain.CheckedAttendance
import com.krzykrucz.elesson.currentlesson.attendance.domain.NotCompletedAttendance
import com.krzykrucz.elesson.currentlesson.attendance.domain.PersistAttendance
import com.krzykrucz.elesson.currentlesson.attendance.domain.lessonId
import com.krzykrucz.elesson.currentlesson.monolith.Database.Companion.ATTENDANCE_DATABASE
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class DbConnectorFactory {

    @Bean
    fun persistAttendance(): PersistAttendance = { attendance ->
        IO.just(
                ATTENDANCE_DATABASE.put(attendance.lessonId(), attendance)
        ).map {
            when (attendance) {
                is NotCompletedAttendance -> false
                is CheckedAttendance -> true
            }
        }
    }

}