package com.krzykrucz.elesson.currentlesson.preparedness

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
