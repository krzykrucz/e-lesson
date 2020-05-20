package com.krzykrucz.elesson.currentlesson.domain.preparedness.domain.api

import arrow.core.Either
import com.krzykrucz.elesson.currentlesson.domain.attendance.PresentStudent
import com.krzykrucz.elesson.currentlesson.domain.shared.AsyncOutput
import com.krzykrucz.elesson.currentlesson.domain.shared.ClassName
import com.krzykrucz.elesson.currentlesson.domain.shared.CurrentLesson
import com.krzykrucz.elesson.currentlesson.domain.shared.LessonIdentifier

typealias CheckStudentCanReportUnprepared = (PresentStudent, ClassName) -> AsyncOutput<UnpreparednessError, PresentStudent>

typealias NoteStudentUnpreparedForLesson = (PresentStudent, StudentsUnpreparedForLesson) -> Either<UnpreparednessError.AlreadyRaised, StudentsUnpreparedForLesson>

typealias CreateEvent = (LessonIdentifier, StudentsUnpreparedForLesson) -> StudentMarkedUnprepared


typealias ReportUnpreparedness = (StudentReportingUnpreparedness, CurrentLesson) -> AsyncOutput<UnpreparednessError, StudentMarkedUnprepared>
