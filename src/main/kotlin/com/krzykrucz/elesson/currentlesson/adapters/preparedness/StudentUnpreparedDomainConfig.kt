package com.krzykrucz.elesson.currentlesson.adapters.preparedness

import com.krzykrucz.elesson.currentlesson.domain.preparedness.FindCurrentLesson
import com.krzykrucz.elesson.currentlesson.domain.preparedness.GetStudentSubjectUnpreparednessInASemester
import com.krzykrucz.elesson.currentlesson.domain.preparedness.NotifyStudentMarkedUnprepared
import com.krzykrucz.elesson.currentlesson.domain.preparedness.PersistUnpreparedStudentToLesson
import com.krzykrucz.elesson.currentlesson.domain.preparedness.ReportUnpreparedStudentApi
import com.krzykrucz.elesson.currentlesson.domain.preparedness.reportUnpreparedStudentApi
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class StudentUnpreparedDomainConfig {

    @Bean
    fun reportUnpreparednessApiBean(
        findCurrentLesson: FindCurrentLesson,
        persistUnpreparedStudentToLesson: PersistUnpreparedStudentToLesson,
        notifyStudentMarkedUnprepared: NotifyStudentMarkedUnprepared,
        getStudentSubjectUnpreparednessInASemester: GetStudentSubjectUnpreparednessInASemester
    ): ReportUnpreparedStudentApi =
        reportUnpreparedStudentApi(
            findCurrentLesson,
            persistUnpreparedStudentToLesson,
            notifyStudentMarkedUnprepared,
            getStudentSubjectUnpreparednessInASemester
        )

}
