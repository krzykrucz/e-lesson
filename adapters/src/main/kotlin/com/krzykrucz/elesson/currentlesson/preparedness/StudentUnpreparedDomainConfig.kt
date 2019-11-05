package com.krzykrucz.elesson.currentlesson.preparedness

import com.krzykrucz.elesson.currentlesson.preparedness.domain.api.ReportUnpreparedness
import com.krzykrucz.elesson.currentlesson.preparedness.domain.implementation.areStudentsEqual
import com.krzykrucz.elesson.currentlesson.preparedness.domain.implementation.checkNumberOfTimesStudentWasUnpreparedInSemester
import com.krzykrucz.elesson.currentlesson.preparedness.domain.implementation.checkStudentCanReportUnprepared
import com.krzykrucz.elesson.currentlesson.preparedness.domain.implementation.checkStudentIsPresent
import com.krzykrucz.elesson.currentlesson.preparedness.domain.implementation.createEvent
import com.krzykrucz.elesson.currentlesson.preparedness.domain.implementation.hasStudentAlreadyRaisedUnprepared
import com.krzykrucz.elesson.currentlesson.preparedness.domain.implementation.hasStudentUsedAllUnpreparedness
import com.krzykrucz.elesson.currentlesson.preparedness.domain.implementation.noteStudentUnprepared
import com.krzykrucz.elesson.currentlesson.preparedness.domain.implementation.reportUnpreparedness
import com.krzykrucz.elesson.currentlesson.preparedness.readmodel.GetStudentSubjectUnpreparednessInASemester
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
                checkNumberOfTimesStudentWasUnpreparedInSemester(getStudentSubjectUnpreparednessInASemester),
                hasStudentUsedAllUnpreparedness
            ),
            noteStudentUnprepared(hasStudentAlreadyRaisedUnprepared),
            checkStudentIsPresent(areStudentsEqual),
            createEvent
        )

}