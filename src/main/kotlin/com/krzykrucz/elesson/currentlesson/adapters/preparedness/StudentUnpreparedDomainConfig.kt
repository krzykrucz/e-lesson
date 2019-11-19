package com.krzykrucz.elesson.currentlesson.adapters.preparedness

import com.krzykrucz.elesson.currentlesson.domain.preparedness.domain.ReportUnpreparedStudentApi
import com.krzykrucz.elesson.currentlesson.domain.preparedness.domain.api.FindCurrentLesson
import com.krzykrucz.elesson.currentlesson.domain.preparedness.domain.api.NotifyStudentMarkedUnprepared
import com.krzykrucz.elesson.currentlesson.domain.preparedness.domain.api.PersistUnpreparedStudentToLesson
import com.krzykrucz.elesson.currentlesson.domain.preparedness.domain.api.ReportUnpreparedness
import com.krzykrucz.elesson.currentlesson.domain.preparedness.domain.implementation.areStudentsEqual
import com.krzykrucz.elesson.currentlesson.domain.preparedness.domain.implementation.checkNumberOfTimesStudentWasUnpreparedInSemester
import com.krzykrucz.elesson.currentlesson.domain.preparedness.domain.implementation.checkStudentCanReportUnprepared
import com.krzykrucz.elesson.currentlesson.domain.preparedness.domain.implementation.checkStudentIsPresent
import com.krzykrucz.elesson.currentlesson.domain.preparedness.domain.implementation.createEvent
import com.krzykrucz.elesson.currentlesson.domain.preparedness.domain.implementation.hasStudentAlreadyRaisedUnprepared
import com.krzykrucz.elesson.currentlesson.domain.preparedness.domain.implementation.hasStudentUsedAllUnpreparedness
import com.krzykrucz.elesson.currentlesson.domain.preparedness.domain.implementation.noteStudentUnprepared
import com.krzykrucz.elesson.currentlesson.domain.preparedness.domain.implementation.reportUnpreparedness
import com.krzykrucz.elesson.currentlesson.domain.preparedness.domain.reportUnpreparedStudentApi
import com.krzykrucz.elesson.currentlesson.domain.preparedness.readmodel.GetStudentSubjectUnpreparednessInASemester
import com.krzykrucz.elesson.currentlesson.domain.preparedness.readmodel.WriteUnpreparednessInTheRegister
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class StudentUnpreparedDomainConfig {

    @Bean
    fun reportUnpreparednessBean(
        getStudentSubjectUnpreparednessInASemester: GetStudentSubjectUnpreparednessInASemester
    ): ReportUnpreparedness =
        reportUnpreparedness(
            checkStudentCanReportUnprepared(
                checkNumberOfTimesStudentWasUnpreparedInSemester(
                    getStudentSubjectUnpreparednessInASemester
                ),
                hasStudentUsedAllUnpreparedness
            ),
            noteStudentUnprepared(hasStudentAlreadyRaisedUnprepared),
            checkStudentIsPresent(areStudentsEqual),
            createEvent
        )

    @Bean
    fun reportUnpreparednessApiBean(
        reportUnpreparedness: ReportUnpreparedness,
        writeUnpreparednessInTheRegister: WriteUnpreparednessInTheRegister,
        findCurrentLesson: FindCurrentLesson,
        persistUnpreparedStudentToLesson: PersistUnpreparedStudentToLesson,
        notifyStudentMarkedUnprepared: NotifyStudentMarkedUnprepared
    ): ReportUnpreparedStudentApi =
        reportUnpreparedStudentApi(
            findCurrentLesson,
            reportUnpreparedness,
            persistUnpreparedStudentToLesson,
            notifyStudentMarkedUnprepared
        )

}