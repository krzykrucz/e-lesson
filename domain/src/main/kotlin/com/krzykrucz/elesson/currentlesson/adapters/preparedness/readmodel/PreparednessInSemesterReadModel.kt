package com.krzykrucz.elesson.currentlesson.adapters.preparedness.readmodel

import arrow.core.Option
import arrow.core.getOrElse
import arrow.effects.IO
import com.krzykrucz.elesson.currentlesson.adapters.preparedness.readmodel.StudentInSemesterReadModel.READ_MODEL
import com.krzykrucz.elesson.currentlesson.domain.preparedness.domain.api.StudentMarkedUnprepared
import com.krzykrucz.elesson.currentlesson.domain.preparedness.readmodel.GetStudentSubjectUnpreparednessInASemester
import com.krzykrucz.elesson.currentlesson.domain.preparedness.readmodel.StudentInSemester
import com.krzykrucz.elesson.currentlesson.domain.preparedness.readmodel.StudentSubjectUnpreparednessInASemester
import com.krzykrucz.elesson.currentlesson.domain.preparedness.readmodel.WriteUnpreparednessInTheRegister
import com.krzykrucz.elesson.currentlesson.domain.shared.AsyncFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.event.EventListener
import java.util.concurrent.ConcurrentHashMap

private class UnpreparednessReadModel :
    WriteUnpreparednessInTheRegister {

    @EventListener
    override fun invoke(event: StudentMarkedUnprepared) =
        StudentInSemester(
            event.lessonId.className,
            event.unpreparedStudent.firstName,
            event.unpreparedStudent.secondName
        )
            .let { READ_MODEL[it] ?: StudentSubjectUnpreparednessInASemester.createEmpty(it) }
            .let { it.copy(count = it.count.inc()) }
            .apply { READ_MODEL[this.student] = this }
            .let { IO.just(it) }
}

private val getStudentSubjectUnpreparednessInASemester: GetStudentSubjectUnpreparednessInASemester = { student ->
    READ_MODEL[student]
        .let { Option.fromNullable(it) }
        .getOrElse { StudentSubjectUnpreparednessInASemester.createEmpty(student) }
        .let(AsyncFactory.Companion::justSuccess)
}

object StudentInSemesterReadModel {

    val READ_MODEL: MutableMap<StudentInSemester, StudentSubjectUnpreparednessInASemester> = ConcurrentHashMap()

}

@Configuration
class ReadModelBeans {
    @Bean
    fun getBean() =
        getStudentSubjectUnpreparednessInASemester

    @Bean
    fun writeBean(): WriteUnpreparednessInTheRegister =
        UnpreparednessReadModel()
}