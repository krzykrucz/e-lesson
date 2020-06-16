package com.krzykrucz.elesson.currentlesson.preparedness

import arrow.core.Either
import arrow.core.Option
import arrow.core.getOrElse
import arrow.core.right
import com.krzykrucz.elesson.currentlesson.preparedness.StudentInSemesterReadModel.READ_MODEL
import com.krzykrucz.elesson.currentlesson.shared.ClassName
import com.krzykrucz.elesson.currentlesson.shared.FirstName
import com.krzykrucz.elesson.currentlesson.shared.SecondName
import com.krzykrucz.elesson.currentlesson.shared.WholeNumber
import kotlinx.coroutines.runBlocking
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.event.EventListener
import java.util.concurrent.ConcurrentHashMap


typealias WriteUnpreparednessInTheRegister = suspend (StudentMarkedUnprepared) -> StudentSubjectUnpreparednessInASemester

typealias GetStudentSubjectUnpreparednessInASemester = suspend (StudentInSemester) -> Either<StudentInSemesterReadError, StudentSubjectUnpreparednessInASemester>

data class StudentInSemester(val className: ClassName, val firstName: FirstName, val secondName: SecondName) // todo add subject

typealias StudentInSemesterReadError = String

data class StudentSubjectUnpreparednessInASemester private constructor(
    val student: StudentInSemester, val count: WholeNumber
) {
    companion object {
        fun createEmpty(student: StudentInSemester) =
            StudentSubjectUnpreparednessInASemester(
                student,
                WholeNumber.ZERO
            )

        fun create(number: Int, student: StudentInSemester): Option<StudentSubjectUnpreparednessInASemester> =
            WholeNumber.of(number)
                ?.let {
                    StudentSubjectUnpreparednessInASemester(
                        student,
                        it
                    )
                }
                .let { Option.fromNullable(it) }
    }
}


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
