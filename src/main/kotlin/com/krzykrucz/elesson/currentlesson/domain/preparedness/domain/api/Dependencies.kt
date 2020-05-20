package com.krzykrucz.elesson.currentlesson.domain.preparedness.domain.api

import arrow.core.Either
import com.krzykrucz.elesson.currentlesson.domain.attendance.CheckedAttendanceList
import com.krzykrucz.elesson.currentlesson.domain.attendance.PresentStudent
import com.krzykrucz.elesson.currentlesson.domain.preparedness.readmodel.StudentInSemesterReadError
import com.krzykrucz.elesson.currentlesson.domain.preparedness.readmodel.StudentSubjectUnpreparednessInASemester
import com.krzykrucz.elesson.currentlesson.domain.shared.AsyncOutput
import com.krzykrucz.elesson.currentlesson.domain.shared.ClassName


typealias CheckNumberOfTimesStudentWasUnpreparedInSemester = (PresentStudent, ClassName) -> AsyncOutput<StudentInSemesterReadError, StudentSubjectUnpreparednessInASemester>

typealias HasStudentUsedAllUnpreparedness = (StudentSubjectUnpreparednessInASemester) -> Boolean

typealias HasStudentAlreadyRaisedUnprepared = (StudentsUnpreparedForLesson, PresentStudent) -> Boolean

typealias CheckStudentIsPresent = (StudentReportingUnpreparedness, CheckedAttendanceList) -> Either<UnpreparednessError.StudentNotPresent, PresentStudent>

typealias AreStudentsEqual = (PresentStudent, StudentReportingUnpreparedness) -> Boolean
