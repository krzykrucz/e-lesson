package com.krzykrucz.elesson.currentlesson.preparedness.readmodel

import arrow.core.Option
import arrow.core.getOrElse
import com.krzykrucz.elesson.currentlesson.shared.AsyncFactory
import com.krzykrucz.elesson.currentlesson.shared.AsyncOutput
import com.krzykrucz.elesson.currentlesson.shared.ClassName
import com.krzykrucz.elesson.currentlesson.shared.FirstName
import com.krzykrucz.elesson.currentlesson.shared.SecondName
import com.krzykrucz.elesson.currentlesson.shared.WholeNumber
import java.util.concurrent.ConcurrentHashMap

data class StudentInSemester(val className: ClassName, val firstName: FirstName, val secondName: SecondName)

object StudentInSemesterReadModel {

    private val READ_MODEL: MutableMap<StudentInSemester, StudentSubjectUnpreparednessInASemester> = ConcurrentHashMap()

    fun getStudentSubjectUnpreparednessInASemester(student: StudentInSemester): AsyncOutput<StudentSubjectUnpreparednessInASemester, StudentInSemesterReadError> =
            READ_MODEL[student]
                    .let { Option.fromNullable(it) }
                    .getOrElse { StudentSubjectUnpreparednessInASemester.createEmpty(student) }
                    .let(AsyncFactory.Companion::justSuccess)

    fun apply(unpreparednessInASemester: StudentSubjectUnpreparednessInASemester) {
        READ_MODEL[unpreparednessInASemester.student] = unpreparednessInASemester
    }
}

typealias StudentInSemesterReadError = String

data class StudentSubjectUnpreparednessInASemester private constructor(
        val student: StudentInSemester, val count: WholeNumber
) {
    companion object {
        fun createEmpty(student: StudentInSemester) = StudentSubjectUnpreparednessInASemester(student, WholeNumber.ZERO)
        fun create(number: Int, student: StudentInSemester): Option<StudentSubjectUnpreparednessInASemester> =
                WholeNumber.of(number)
                        ?.let { StudentSubjectUnpreparednessInASemester(student, it) }
                        .let { Option.fromNullable(it) }
    }
}