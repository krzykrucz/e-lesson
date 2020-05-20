package com.krzykrucz.elesson.currentlesson.adapters.preparedness.readmodel

import arrow.core.Option
import arrow.core.getOrElse
import arrow.core.right
import com.krzykrucz.elesson.currentlesson.adapters.preparedness.readmodel.StudentInSemesterReadModel.READ_MODEL
import com.krzykrucz.elesson.currentlesson.domain.preparedness.domain.api.StudentMarkedUnprepared
import com.krzykrucz.elesson.currentlesson.domain.preparedness.readmodel.GetStudentSubjectUnpreparednessInASemester
import com.krzykrucz.elesson.currentlesson.domain.preparedness.readmodel.StudentInSemester
import com.krzykrucz.elesson.currentlesson.domain.preparedness.readmodel.StudentSubjectUnpreparednessInASemester
import com.krzykrucz.elesson.currentlesson.domain.preparedness.readmodel.WriteUnpreparednessInTheRegister
import kotlinx.coroutines.runBlocking
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.event.EventListener
import java.util.concurrent.ConcurrentHashMap

val writeUnpreparednessInTheRegister: WriteUnpreparednessInTheRegister = {
    StudentInSemester(
        it.lessonId.className,
        it.unpreparedStudent.firstName,
        it.unpreparedStudent.secondName
    )
        .let { READ_MODEL[it] ?: StudentSubjectUnpreparednessInASemester.createEmpty(it) }
        .let { it.copy(count = it.count.inc()) }
        .apply { READ_MODEL[this.student] = this }
}

private val getStudentSubjectUnpreparednessInASemester: GetStudentSubjectUnpreparednessInASemester = { student ->
    READ_MODEL[student]
        .let { Option.fromNullable(it) }
        .getOrElse { StudentSubjectUnpreparednessInASemester.createEmpty(student) }
        .right()
}

object StudentInSemesterReadModel {

    val READ_MODEL: MutableMap<StudentInSemester, StudentSubjectUnpreparednessInASemester> = ConcurrentHashMap()

}

@Configuration
class ReadModelBeans {
    @Bean
    fun getBean() =
        getStudentSubjectUnpreparednessInASemester

    @EventListener
    fun eventListenerStudentMarkedUnprepared(event: StudentMarkedUnprepared) {
        runBlocking {
            writeUnpreparednessInTheRegister(event)
        }
    }
}
