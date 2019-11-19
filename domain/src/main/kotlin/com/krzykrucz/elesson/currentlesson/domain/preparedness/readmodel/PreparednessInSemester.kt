package com.krzykrucz.elesson.currentlesson.domain.preparedness.readmodel

import arrow.core.Option
import arrow.effects.IO
import com.krzykrucz.elesson.currentlesson.domain.preparedness.domain.api.StudentMarkedUnprepared
import com.krzykrucz.elesson.currentlesson.domain.shared.AsyncOutput
import com.krzykrucz.elesson.currentlesson.domain.shared.ClassName
import com.krzykrucz.elesson.currentlesson.domain.shared.FirstName
import com.krzykrucz.elesson.currentlesson.domain.shared.SecondName
import com.krzykrucz.elesson.currentlesson.domain.shared.WholeNumber


typealias WriteUnpreparednessInTheRegister = (StudentMarkedUnprepared) -> IO<StudentSubjectUnpreparednessInASemester>

typealias GetStudentSubjectUnpreparednessInASemester = (StudentInSemester) -> AsyncOutput<StudentInSemesterReadError, StudentSubjectUnpreparednessInASemester>

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