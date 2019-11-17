package com.krzykrucz.elesson.currentlesson.preparedness.domain.api

import com.krzykrucz.elesson.currentlesson.attendance.domain.CheckedAttendanceList
import com.krzykrucz.elesson.currentlesson.attendance.domain.PresentStudent
import com.krzykrucz.elesson.currentlesson.preparedness.readmodel.StudentInSemesterReadError
import com.krzykrucz.elesson.currentlesson.preparedness.readmodel.StudentSubjectUnpreparednessInASemester
import com.krzykrucz.elesson.currentlesson.shared.AsyncOutput
import com.krzykrucz.elesson.currentlesson.shared.ClassName
import com.krzykrucz.elesson.currentlesson.shared.Output


typealias CheckNumberOfTimesStudentWasUnpreparedInSemester = (PresentStudent, ClassName) -> AsyncOutput<StudentInSemesterReadError, StudentSubjectUnpreparednessInASemester>

typealias HasStudentUsedAllUnpreparedness = (StudentSubjectUnpreparednessInASemester) -> Boolean

typealias HasStudentAlreadyRaisedUnprepared = (StudentsUnpreparedForLesson, PresentStudent) -> Boolean

typealias CheckStudentIsPresent = (StudentReportingUnpreparedness, CheckedAttendanceList) -> Output<UnpreparednessError.StudentNotPresent, PresentStudent>

typealias AreStudentsEqual = (PresentStudent, StudentReportingUnpreparedness) -> Boolean