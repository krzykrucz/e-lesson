package com.krzykrucz.elesson.currentlesson.adapters

import arrow.core.getOrElse
import com.krzykrucz.elesson.currentlesson.domain.AttemptedLessonStartTime
import com.krzykrucz.elesson.currentlesson.domain.CheckSchedule
import com.krzykrucz.elesson.currentlesson.domain.ClassGroupName
import com.krzykrucz.elesson.currentlesson.domain.Maths
import com.krzykrucz.elesson.currentlesson.domain.Period
import com.krzykrucz.elesson.currentlesson.domain.StartingLessonFailure
import com.krzykrucz.elesson.currentlesson.domain.Teacher
import com.krzykrucz.elesson.currentlesson.domain.TimetabledLesson
import com.krzykrucz.elesson.currentlesson.domain.Year
import com.virtuslab.basetypes.refined.NonEmptyText
import com.virtuslab.basetypes.result.Result
import com.virtuslab.basetypes.result.arrow.toAsync
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

class CheckScheduleAdapter : CheckSchedule {
    override fun invoke(teacher: Teacher, time: AttemptedLessonStartTime) =
        NonEmptyText.of("A")
            .map {
                ClassGroupName(Year.`7`, it)
            }
            .map {
                TimetabledLesson(
                    Period.First,
                    teacher,
                    it,
                    Maths
                )
            }
            .map { Result.success(it) }
            .getOrElse { Result.error(StartingLessonFailure.ExternalError) }
            .toAsync()
}

@Configuration
class CheckScheduleAdapterConfig {
    @Bean
    fun checkScheduleAdapter(): CheckSchedule = CheckScheduleAdapter()
}