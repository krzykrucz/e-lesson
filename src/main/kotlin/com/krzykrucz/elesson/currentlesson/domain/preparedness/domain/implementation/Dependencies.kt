package com.krzykrucz.elesson.currentlesson.domain.preparedness.domain.implementation

import arrow.core.extensions.list.foldable.find
import com.krzykrucz.elesson.currentlesson.domain.preparedness.domain.api.AreStudentsEqual
import com.krzykrucz.elesson.currentlesson.domain.preparedness.domain.api.CheckNumberOfTimesStudentWasUnpreparedInSemester
import com.krzykrucz.elesson.currentlesson.domain.preparedness.domain.api.CheckStudentIsPresent
import com.krzykrucz.elesson.currentlesson.domain.preparedness.domain.api.HasStudentAlreadyRaisedUnprepared
import com.krzykrucz.elesson.currentlesson.domain.preparedness.domain.api.HasStudentUsedAllUnpreparedness
import com.krzykrucz.elesson.currentlesson.domain.preparedness.domain.api.UnpreparedStudent
import com.krzykrucz.elesson.currentlesson.domain.preparedness.domain.api.UnpreparednessError
import com.krzykrucz.elesson.currentlesson.domain.preparedness.readmodel.GetStudentSubjectUnpreparednessInASemester
import com.krzykrucz.elesson.currentlesson.domain.preparedness.readmodel.StudentInSemester

fun checkNumberOfTimesStudentWasUnpreparedInSemester(
    getStudentSubjectUnpreparednessInASemester: GetStudentSubjectUnpreparednessInASemester
): CheckNumberOfTimesStudentWasUnpreparedInSemester = { student, className ->
    StudentInSemester(
        className,
        student.firstName,
        student.secondName
    )
        .let { getStudentSubjectUnpreparednessInASemester(it) }
}

val hasStudentAlreadyRaisedUnprepared: HasStudentAlreadyRaisedUnprepared = { studentsUnpreparedForLesson, presentStudent ->
    UnpreparedStudent(
        presentStudent.firstName,
        presentStudent.secondName
    )
        .let { studentsUnpreparedForLesson.students.contains(it) }
}
val hasStudentUsedAllUnpreparedness: HasStudentUsedAllUnpreparedness = {
    it.count >= 3
}
val areStudentsEqual: AreStudentsEqual = { presentStudent, studentReportingUnpreparedness ->
    (presentStudent.firstName.name.text == studentReportingUnpreparedness.firstName)
        .and(presentStudent.secondName.name.text == studentReportingUnpreparedness.secondName)
}

fun checkStudentIsPresent(
    areStudentsEqual: AreStudentsEqual
): CheckStudentIsPresent = { studentReportingUnpreparedness, checkedAttendanceList ->
    checkedAttendanceList.presentStudents
        .find { areStudentsEqual(it, studentReportingUnpreparedness) }
        .toEither { UnpreparednessError.StudentNotPresent }
}
