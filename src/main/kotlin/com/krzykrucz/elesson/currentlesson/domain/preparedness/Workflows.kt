package com.krzykrucz.elesson.currentlesson.domain.preparedness

import arrow.core.Either
import arrow.core.extensions.either.applicativeError.handleError
import arrow.core.extensions.list.foldable.find
import arrow.core.left
import arrow.core.maybe
import com.krzykrucz.elesson.currentlesson.adapters.asyncFlatMap
import com.krzykrucz.elesson.currentlesson.adapters.asyncMap
import com.krzykrucz.elesson.currentlesson.domain.attendance.CheckedAttendanceList
import com.krzykrucz.elesson.currentlesson.domain.attendance.PresentStudent
import com.krzykrucz.elesson.currentlesson.domain.shared.ClassName
import com.krzykrucz.elesson.currentlesson.domain.shared.CurrentLesson
import com.krzykrucz.elesson.currentlesson.domain.shared.LessonAfterAttendance
import com.krzykrucz.elesson.currentlesson.domain.shared.LessonIdentifier
import com.krzykrucz.elesson.currentlesson.domain.shared.failIf

private typealias CheckNumberOfTimesStudentWasUnpreparedInSemester = suspend (PresentStudent, ClassName) -> Either<StudentInSemesterReadError, StudentSubjectUnpreparednessInASemester>

private fun checkNumberOfTimesStudentWasUnpreparedInSemester(
    getStudentSubjectUnpreparednessInASemester: GetStudentSubjectUnpreparednessInASemester
): CheckNumberOfTimesStudentWasUnpreparedInSemester = { student, className ->
    StudentInSemester(
        className,
        student.firstName,
        student.secondName
    )
        .let { getStudentSubjectUnpreparednessInASemester(it) }
}

private typealias HasStudentUsedAllUnpreparedness = (StudentSubjectUnpreparednessInASemester) -> Boolean

private val hasStudentUsedAllUnpreparedness: HasStudentUsedAllUnpreparedness = {
    it.count >= 3
}

private typealias HasStudentAlreadyRaisedUnprepared = (StudentsUnpreparedForLesson, PresentStudent) -> Boolean

private val hasStudentAlreadyRaisedUnprepared: HasStudentAlreadyRaisedUnprepared = { studentsUnpreparedForLesson, presentStudent ->
    UnpreparedStudent(
        presentStudent.firstName,
        presentStudent.secondName
    )
        .let { studentsUnpreparedForLesson.students.contains(it) }
}

private typealias CheckStudentIsPresent = (StudentReportingUnpreparedness, CheckedAttendanceList) -> Either<UnpreparednessError.StudentNotPresent, PresentStudent>

private fun checkStudentIsPresent(
    areStudentsEqual: AreStudentsEqual
): CheckStudentIsPresent = { studentReportingUnpreparedness, checkedAttendanceList ->
    checkedAttendanceList.presentStudents
        .find { areStudentsEqual(it, studentReportingUnpreparedness) }
        .toEither { UnpreparednessError.StudentNotPresent }
}

private typealias AreStudentsEqual = (PresentStudent, StudentReportingUnpreparedness) -> Boolean

private val areStudentsEqual: AreStudentsEqual = { presentStudent, studentReportingUnpreparedness ->
    (presentStudent.firstName.name.text == studentReportingUnpreparedness.firstName)
        .and(presentStudent.secondName.name.text == studentReportingUnpreparedness.secondName)
}

typealias CheckStudentCanReportUnprepared = suspend (PresentStudent, ClassName) -> Either<UnpreparednessError, PresentStudent>

private fun checkStudentCanReportUnprepared(
    checkNumberOfTimesStudentWasUnpreparedInSemester: CheckNumberOfTimesStudentWasUnpreparedInSemester,
    hasStudentUsedAllUnpreparedness: HasStudentUsedAllUnpreparedness
): CheckStudentCanReportUnprepared = { presentStudent, className ->
    checkNumberOfTimesStudentWasUnpreparedInSemester(presentStudent, className)
        .handleError { presentStudent.toStudentInSemester(className).let(StudentSubjectUnpreparednessInASemester.Companion::createEmpty) }
        .failIf(hasStudentUsedAllUnpreparedness, UnpreparednessError.UnpreparedTooManyTimes)
        .map { presentStudent }
        .mapLeft { it as UnpreparednessError }
}

typealias NoteStudentUnpreparedForLesson = (PresentStudent, StudentsUnpreparedForLesson) -> Either<UnpreparednessError.AlreadyRaised, StudentsUnpreparedForLesson>

private fun noteStudentUnprepared(
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

typealias CreateEvent = (LessonIdentifier, StudentsUnpreparedForLesson) -> StudentMarkedUnprepared

private fun createEvent(): CreateEvent = { lessonIdentifier, studentsUnpreparedForLesson ->
    StudentMarkedUnprepared(
        lessonId = lessonIdentifier,
        unpreparedStudent = studentsUnpreparedForLesson.students.last(),
        studentsUnpreparedForLesson = studentsUnpreparedForLesson
    )
}


typealias ReportUnpreparedness = suspend (StudentReportingUnpreparedness, CurrentLesson) -> Either<UnpreparednessError, StudentMarkedUnprepared>

fun reportUnpreparednessWorkflow(
    getStudentSubjectUnpreparednessInASemester: GetStudentSubjectUnpreparednessInASemester
): ReportUnpreparedness = { studentReportingUnpreparedness, lesson ->
    val checkIfStudentCanReportUnprepared = checkStudentCanReportUnprepared(
        checkNumberOfTimesStudentWasUnpreparedInSemester(getStudentSubjectUnpreparednessInASemester),
        hasStudentUsedAllUnpreparedness
    )
    val checkStudentIsPresent = checkStudentIsPresent(areStudentsEqual)
    val noteStudentUnpreparedForLesson = noteStudentUnprepared(hasStudentAlreadyRaisedUnprepared)
    val createEvent = createEvent()

    when (lesson) {
        is LessonAfterAttendance ->
            checkStudentIsPresent(studentReportingUnpreparedness, lesson.attendance)
                .asyncFlatMap { checkIfStudentCanReportUnprepared(it, lesson.identifier.className) }
                .asyncFlatMap { noteStudentUnpreparedForLesson(it, lesson.unpreparedStudents) }
                .asyncMap { createEvent(lesson.identifier, it) }
        else -> UnpreparednessError.TooLateToRaiseUnpreparedness.left()
    }
}

private fun PresentStudent.toStudentInSemester(className: ClassName) =
    StudentInSemester(
        className,
        this.firstName,
        this.secondName
    )
