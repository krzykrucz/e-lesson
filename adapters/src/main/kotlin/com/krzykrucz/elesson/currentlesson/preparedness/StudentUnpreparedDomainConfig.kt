package com.krzykrucz.elesson.currentlesson.preparedness

import com.krzykrucz.elesson.currentlesson.preparedness.domain.ReportUnpreparedness
import com.krzykrucz.elesson.currentlesson.preparedness.domain.areStudentsEqual
import com.krzykrucz.elesson.currentlesson.preparedness.domain.checkNumberOfTimesStudentWasUnpreparedInSemester
import com.krzykrucz.elesson.currentlesson.preparedness.domain.checkStudentCanReportUnprepared
import com.krzykrucz.elesson.currentlesson.preparedness.domain.checkStudentIsPresent
import com.krzykrucz.elesson.currentlesson.preparedness.domain.createEvent
import com.krzykrucz.elesson.currentlesson.preparedness.domain.hasStudentAlreadyRaisedUnprepared
import com.krzykrucz.elesson.currentlesson.preparedness.domain.hasStudentUsedAllUnpreparednesses
import com.krzykrucz.elesson.currentlesson.preparedness.domain.noteStudentUnprepared
import com.krzykrucz.elesson.currentlesson.preparedness.domain.reportUnpreparedness
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
                hasStudentUsedAllUnpreparednesses
            ),
            noteStudentUnprepared(hasStudentAlreadyRaisedUnprepared),
            checkStudentIsPresent(areStudentsEqual),
            createEvent
        )

}