package com.krzykrucz.elesson.currentlesson.attendance.adapters.persistence

import arrow.core.Tuple2
import arrow.core.toOption
import arrow.data.OptionT
import arrow.effects.IO
import arrow.effects.extensions.io.applicative.applicative
import arrow.effects.extensions.io.functor.functor
import com.krzykrucz.elesson.currentlesson.attendance.domain.CheckedAttendanceList
import com.krzykrucz.elesson.currentlesson.attendance.domain.FetchCheckedAttendance
import com.krzykrucz.elesson.currentlesson.attendance.domain.FetchNotCompletedAttendanceAndRegistry
import com.krzykrucz.elesson.currentlesson.attendance.domain.IncompleteAttendanceList
import com.krzykrucz.elesson.currentlesson.attendance.domain.PersistAttendance
import com.krzykrucz.elesson.currentlesson.monolith.Database
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class DbConnectorFactory {

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
    fun fetchCheckedAttendance(): FetchCheckedAttendance = { lessonIdentifier ->
        OptionT.fromOption(IO.applicative(), Database.LESSON_DATABASE[lessonIdentifier].toOption())
                .filter(IO.functor()) { it.attendance != null }
                .filter(IO.functor()) { it.attendance is CheckedAttendanceList }
                .map(IO.functor()) { it.attendance as CheckedAttendanceList }
    }

    @Bean
    fun fetchNotCompletedAttendance(): FetchNotCompletedAttendanceAndRegistry = { lessonIdentifier ->
        OptionT.fromOption(IO.applicative(), Database.LESSON_DATABASE[lessonIdentifier].toOption())
                .filter(IO.functor()) { it.attendance != null }
                .filter(IO.functor()) { it.attendance is IncompleteAttendanceList }
                .map(IO.functor()) { Tuple2(it.attendance as IncompleteAttendanceList, it.classRegistry) }
    }

}