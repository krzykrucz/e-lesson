package com.krzykrucz.elesson.currentlesson.domain.preparedness.domain.api

import com.krzykrucz.elesson.currentlesson.domain.attendance.PresentStudent
import com.krzykrucz.elesson.currentlesson.domain.shared.AsyncOutput
import com.krzykrucz.elesson.currentlesson.domain.shared.ClassName
import com.krzykrucz.elesson.currentlesson.domain.shared.CurrentLesson
import com.krzykrucz.elesson.currentlesson.domain.shared.LessonIdentifier
import com.krzykrucz.elesson.currentlesson.domain.shared.Output

typealias CheckStudentCanReportUnprepared = (PresentStudent, ClassName) -> AsyncOutput<UnpreparednessError, PresentStudent>

typealias NoteStudentUnpreparedForLesson = (PresentStudent, StudentsUnpreparedForLesson) -> Output<UnpreparednessError.AlreadyRaised, StudentsUnpreparedForLesson>

typealias CreateEvent = (LessonIdentifier, StudentsUnpreparedForLesson) -> StudentMarkedUnprepared


typealias ReportUnpreparedness = (StudentReportingUnpreparedness, CurrentLesson) -> AsyncOutput<UnpreparednessError, StudentMarkedUnprepared>