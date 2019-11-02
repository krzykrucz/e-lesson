package com.krzykrucz.elesson.currentlesson.preparedness.adapters.findcurrentlesson

import arrow.core.toOption
import arrow.effects.IO
import com.krzykrucz.elesson.currentlesson.attendance.domain.CheckedAttendanceList
import com.krzykrucz.elesson.currentlesson.monolith.Database
import com.krzykrucz.elesson.currentlesson.preparedness.domain.FindCurrentLesson
import com.krzykrucz.elesson.currentlesson.preparedness.domain.UnpreparednessError
import com.krzykrucz.elesson.currentlesson.shared.CurrentLesson
import com.krzykrucz.elesson.currentlesson.shared.LessonAfterAttendance
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AdapterConfig {
    @Bean
    fun getLessonBean(): FindCurrentLesson = {
        Database.LESSON_DATABASE[it].toOption()
            .toEither { UnpreparednessError.LessonNotStarted }
            .map { persistentCurrentLesson ->

                (if (persistentCurrentLesson.lessonTopic == null &&
                    persistentCurrentLesson.attendance is CheckedAttendanceList) {
                    LessonAfterAttendance(
                        persistentCurrentLesson.lessonId,
                        persistentCurrentLesson.attendance as CheckedAttendanceList,
                        persistentCurrentLesson.unpreparedStudents
                    )
                } else {
                    null//TODO
                })
                    .let { it as CurrentLesson }
            }
            .let { IO.just(it) }
    }
}