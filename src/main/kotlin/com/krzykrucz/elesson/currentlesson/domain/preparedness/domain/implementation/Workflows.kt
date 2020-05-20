package com.krzykrucz.elesson.currentlesson.domain.preparedness.domain.implementation

import arrow.core.extensions.either.applicativeError.handleError
import arrow.core.left
import arrow.core.maybe
import com.krzykrucz.elesson.currentlesson.adapters.asyncFlatMap
import com.krzykrucz.elesson.currentlesson.adapters.asyncMap
import com.krzykrucz.elesson.currentlesson.domain.attendance.PresentStudent
import com.krzykrucz.elesson.currentlesson.domain.preparedness.domain.api.CheckNumberOfTimesStudentWasUnpreparedInSemester
import com.krzykrucz.elesson.currentlesson.domain.preparedness.domain.api.CheckStudentCanReportUnprepared
import com.krzykrucz.elesson.currentlesson.domain.preparedness.domain.api.CheckStudentIsPresent
import com.krzykrucz.elesson.currentlesson.domain.preparedness.domain.api.CreateEvent
import com.krzykrucz.elesson.currentlesson.domain.preparedness.domain.api.HasStudentAlreadyRaisedUnprepared
import com.krzykrucz.elesson.currentlesson.domain.preparedness.domain.api.HasStudentUsedAllUnpreparedness
import com.krzykrucz.elesson.currentlesson.domain.preparedness.domain.api.NoteStudentUnpreparedForLesson
import com.krzykrucz.elesson.currentlesson.domain.preparedness.domain.api.ReportUnpreparedness
import com.krzykrucz.elesson.currentlesson.domain.preparedness.domain.api.StudentMarkedUnprepared
import com.krzykrucz.elesson.currentlesson.domain.preparedness.domain.api.UnpreparedStudent
import com.krzykrucz.elesson.currentlesson.domain.preparedness.domain.api.UnpreparednessError
import com.krzykrucz.elesson.currentlesson.domain.preparedness.readmodel.StudentInSemester
import com.krzykrucz.elesson.currentlesson.domain.preparedness.readmodel.StudentSubjectUnpreparednessInASemester
import com.krzykrucz.elesson.currentlesson.domain.shared.ClassName
import com.krzykrucz.elesson.currentlesson.domain.shared.LessonAfterAttendance
import com.krzykrucz.elesson.currentlesson.domain.shared.failIf

//workflows
fun checkStudentCanReportUnprepared(
    checkNumberOfTimesStudentWasUnpreparedInSemester: CheckNumberOfTimesStudentWasUnpreparedInSemester,
    hasStudentUsedAllUnpreparedness: HasStudentUsedAllUnpreparedness
): CheckStudentCanReportUnprepared = { presentStudent, className ->
    checkNumberOfTimesStudentWasUnpreparedInSemester(presentStudent, className)
        .handleError { presentStudent.toStudentInSemester(className).let(StudentSubjectUnpreparednessInASemester.Companion::createEmpty) }
        .failIf(hasStudentUsedAllUnpreparedness, UnpreparednessError.UnpreparedTooManyTimes)
        .map { presentStudent }
        .mapLeft { it as UnpreparednessError }
}


fun noteStudentUnprepared(
    hasStudentAlreadyRaisedUnprepared: HasStudentAlreadyRaisedUnprepared
): NoteStudentUnpreparedForLesson = { presentStudent, studentsUnpreparedForLesson ->
    hasStudentAlreadyRaisedUnprepared(studentsUnpreparedForLesson, presentStudent).not()
        .maybe {
            UnpreparedStudent(
                presentStudent.firstName,
                presentStudent.secondName
            )
        }
        .map { studentsUnpreparedForLesson.students + it }
        .map(studentsUnpreparedForLesson::copy)
        .toEither { UnpreparednessError.AlreadyRaised }
}

fun PresentStudent.toStudentInSemester(className: ClassName) =
    StudentInSemester(
        className,
        this.firstName,
        this.secondName
    )

val createEvent: CreateEvent = { lessonIdentifier, studentsUnpreparedForLesson ->
    StudentMarkedUnprepared(
        lessonId = lessonIdentifier,
        unpreparedStudent = studentsUnpreparedForLesson.students.last(),
        studentsUnpreparedForLesson = studentsUnpreparedForLesson
    )
}

//pipeline
fun reportUnpreparedness(
    checkStudentCanReportUnprepared: CheckStudentCanReportUnprepared,
    noteStudentUnpreparedForLesson: NoteStudentUnpreparedForLesson,
    checkStudentIsPresent: CheckStudentIsPresent,
    createEvent: CreateEvent
): ReportUnpreparedness = { studentReportingUnpreparedness, lesson ->

    when (lesson) {
        is LessonAfterAttendance ->
            checkStudentIsPresent(studentReportingUnpreparedness, lesson.attendance)
                .asyncFlatMap { checkStudentCanReportUnprepared(it, lesson.identifier.className) }
                .asyncFlatMap { noteStudentUnpreparedForLesson(it, lesson.unpreparedStudents) }
                .asyncMap { createEvent(lesson.identifier, it) }
        else -> UnpreparednessError.TooLateToRaiseUnpreparedness.left()
    }
}
