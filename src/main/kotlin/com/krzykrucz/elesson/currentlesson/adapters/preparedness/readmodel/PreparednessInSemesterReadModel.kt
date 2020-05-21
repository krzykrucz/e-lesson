package com.krzykrucz.elesson.currentlesson.adapters.preparedness.readmodel

import arrow.core.Option
import arrow.core.getOrElse
import arrow.core.right
import com.krzykrucz.elesson.currentlesson.adapters.preparedness.readmodel.StudentInSemesterReadModel.READ_MODEL
import com.krzykrucz.elesson.currentlesson.domain.preparedness.GetStudentSubjectUnpreparednessInASemester
import com.krzykrucz.elesson.currentlesson.domain.preparedness.StudentInSemester
import com.krzykrucz.elesson.currentlesson.domain.preparedness.StudentMarkedUnprepared
import com.krzykrucz.elesson.currentlesson.domain.preparedness.StudentSubjectUnpreparednessInASemester
import com.krzykrucz.elesson.currentlesson.domain.preparedness.WriteUnpreparednessInTheRegister
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
