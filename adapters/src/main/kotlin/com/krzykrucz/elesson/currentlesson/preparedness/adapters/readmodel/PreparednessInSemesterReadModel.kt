package com.krzykrucz.elesson.currentlesson.preparedness.adapters.readmodel

import arrow.core.Option
import arrow.core.getOrElse
import com.krzykrucz.elesson.currentlesson.preparedness.readmodel.GetStudentSubjectUnpreparednessInASemester
import com.krzykrucz.elesson.currentlesson.preparedness.readmodel.StudentInSemester
import com.krzykrucz.elesson.currentlesson.preparedness.readmodel.StudentSubjectUnpreparednessInASemester
import com.krzykrucz.elesson.currentlesson.preparedness.readmodel.WriteUnpreparednessInTheRegister
import com.krzykrucz.elesson.currentlesson.shared.AsyncFactory
import java.util.concurrent.ConcurrentHashMap

val writeUnpreparednessInTheRegister: WriteUnpreparednessInTheRegister = { event, studentSubjectUnpreparednessInASemester ->
    val student = StudentInSemester(
            event.lessonId.className,
            event.unpreparedStudent.firstName,
            event.unpreparedStudent.secondName
    )
    Option.fromNullable(studentSubjectUnpreparednessInASemester)
            .getOrElse { StudentSubjectUnpreparednessInASemester.createEmpty(student) }
            .let { it.copy(count = it.count.inc()) }
}

val getStudentSubjectUnpreparednessInASemester: GetStudentSubjectUnpreparednessInASemester = { student ->
    StudentInSemesterReadModel.READ_MODEL[student]
            .let { Option.fromNullable(it) }
            .getOrElse { StudentSubjectUnpreparednessInASemester.createEmpty(student) }
            .let(AsyncFactory.Companion::justSuccess)
}

object StudentInSemesterReadModel {

    val READ_MODEL: MutableMap<StudentInSemester, StudentSubjectUnpreparednessInASemester> = ConcurrentHashMap()

    fun save(unpreparedness: StudentSubjectUnpreparednessInASemester) {
        READ_MODEL[unpreparedness.student] = unpreparedness
    }
}