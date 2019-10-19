package com.krzykrucz.elesson.currentlesson.attendance.adapters.persistence

import arrow.effects.IO
import arrow.effects.extensions.io.functor.functor
import arrow.effects.extensions.io.monad.monad
import com.krzykrucz.elesson.currentlesson.attendance.domain.CheckedAttendance
import com.krzykrucz.elesson.currentlesson.attendance.domain.FetchCheckedAttendance
import com.krzykrucz.elesson.currentlesson.attendance.domain.FetchNotCompletedAttendance
import com.krzykrucz.elesson.currentlesson.attendance.domain.NotCompletedAttendance
import com.krzykrucz.elesson.currentlesson.attendance.domain.PersistAttendance
import com.krzykrucz.elesson.currentlesson.attendance.domain.lessonId
import com.krzykrucz.elesson.currentlesson.monolith.Database.Companion.ATTENDANCE_DATABASE
import com.krzykrucz.elesson.currentlesson.monolith.Database.Companion.fetchAttendance
import com.krzykrucz.elesson.currentlesson.monolith.Database.Companion.fetchStartedLessonAsAttendance
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

    @Bean
    fun fetchCheckedAttendance(): FetchCheckedAttendance = { lessonId ->
        fetchAttendance(lessonId)
                .map(IO.functor()) { it as CheckedAttendance }
    }

    @Bean
    fun fetchNotCompletedAttendance(): FetchNotCompletedAttendance = { lessonId ->
        fetchAttendance(lessonId)
                .map(IO.functor()) { it as NotCompletedAttendance }
                .orElse(IO.monad()) { fetchStartedLessonAsAttendance(lessonId) }
    }

}