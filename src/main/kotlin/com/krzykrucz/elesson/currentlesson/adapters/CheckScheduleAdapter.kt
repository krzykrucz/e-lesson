package com.krzykrucz.elesson.currentlesson.adapters

import arrow.core.getOrElse
import com.krzykrucz.elesson.currentlesson.domain.AttemptedLessonStartTime
import com.krzykrucz.elesson.currentlesson.domain.CheckSchedule
import com.krzykrucz.elesson.currentlesson.domain.ClassName
import com.krzykrucz.elesson.currentlesson.domain.LessonHourNumber
import com.krzykrucz.elesson.currentlesson.domain.ScheduledLesson
import com.krzykrucz.elesson.currentlesson.domain.ScheduledTime
import com.krzykrucz.elesson.currentlesson.domain.StartLessonError
import com.krzykrucz.elesson.currentlesson.domain.Teacher
import com.virtuslab.basetypes.refined.NonEmptyText
import com.virtuslab.basetypes.result.Result
import com.virtuslab.basetypes.result.arrow.toAsync
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

class CheckScheduleAdapter : CheckSchedule {
    override fun invoke(teacher: Teacher, time: AttemptedLessonStartTime) =
        NonEmptyText.of("Gryffindor")
            .map(::ClassName)
            .map {
                ScheduledLesson(
                    it,
                    time.dateTime,
                    LessonHourNumber.One
                )
            }
            .map { Result.success(it) }
            .getOrElse { Result.error(StartLessonError.ExternalError) }
            .toAsync()
}

@Configuration
class CheckScheduleAdapterConfig {
    @Bean
    fun checkScheduleAdapter(): CheckSchedule = CheckScheduleAdapter()
}