package com.krzykrucz.elesson.currentlesson.preparedness.domain.api

import com.krzykrucz.elesson.currentlesson.attendance.domain.PresentStudent
import com.krzykrucz.elesson.currentlesson.shared.AsyncOutput
import com.krzykrucz.elesson.currentlesson.shared.ClassName
import com.krzykrucz.elesson.currentlesson.shared.CurrentLesson
import com.krzykrucz.elesson.currentlesson.shared.LessonIdentifier
import com.krzykrucz.elesson.currentlesson.shared.Output

typealias CheckStudentCanReportUnprepared = (PresentStudent, ClassName) -> AsyncOutput<UnpreparednessError, PresentStudent>

typealias NoteStudentUnpreparedForLesson = (PresentStudent, StudentsUnpreparedForLesson) -> Output<UnpreparednessError.AlreadyRaised, StudentsUnpreparedForLesson>

typealias CreateEvent = (LessonIdentifier, StudentsUnpreparedForLesson) -> StudentMarkedUnprepared


typealias ReportUnpreparedness = (StudentReportingUnpreparedness, CurrentLesson) -> AsyncOutput<UnpreparednessError, StudentMarkedUnprepared>