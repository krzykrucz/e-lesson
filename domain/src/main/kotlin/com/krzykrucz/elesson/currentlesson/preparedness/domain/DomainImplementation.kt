package com.krzykrucz.elesson.currentlesson.preparedness.domain

import arrow.core.extensions.list.foldable.find
import arrow.core.maybe
import com.krzykrucz.elesson.currentlesson.attendance.domain.PresentStudent
import com.krzykrucz.elesson.currentlesson.preparedness.readmodel.StudentInSemester
import com.krzykrucz.elesson.currentlesson.preparedness.readmodel.StudentInSemesterReadModel
import com.krzykrucz.elesson.currentlesson.preparedness.readmodel.StudentSubjectUnpreparednessInASemester
import com.krzykrucz.elesson.currentlesson.shared.AsyncFactory
import com.krzykrucz.elesson.currentlesson.shared.AsyncOutputFactory
import com.krzykrucz.elesson.currentlesson.shared.ClassName
import com.krzykrucz.elesson.currentlesson.shared.LessonAfterAttendance
import com.krzykrucz.elesson.currentlesson.shared.failIf
import com.krzykrucz.elesson.currentlesson.shared.flatMapAsyncSuccess
import com.krzykrucz.elesson.currentlesson.shared.flatMapSuccess
import com.krzykrucz.elesson.currentlesson.shared.handleError
import com.krzykrucz.elesson.currentlesson.shared.mapError
import com.krzykrucz.elesson.currentlesson.shared.mapSuccess

//dependency

val checkNumberOfTimesStudentWasUnpreparedInSemester: CheckNumberOfTimesStudentWasUnpreparedInSemester = { student, className ->
    StudentInSemester(className, student.firstName, student.secondName)
            .let { StudentInSemesterReadModel.getStudentSubjectUnpreparednessInASemester(it) }
}

val hasStudentAlreadyRaisedUnprepared: HasStudentAlreadyRaisedUnprepared = { studentsUnpreparedForLesson, presentStudent ->
    UnpreparedStudent(presentStudent.firstName, presentStudent.secondName)
            .let { studentsUnpreparedForLesson.students.contains(it) }
}

val hasStudentUsedAllUnpreparednesses: HasStudentUsedAllUnpreparednesses = {
    it.count >= 3
}

val areStudentsEqual: AreStudentsEqual = { presentStudent, studentReportingUnpreparedness ->
    (presentStudent.firstName == studentReportingUnpreparedness.firstName)
            .and(presentStudent.secondName == studentReportingUnpreparedness.secondName)
}

fun checkStudentIsPresent(
        areStudentsEqual: AreStudentsEqual
): CheckStudentIsPresent = { studentReportingUnpreparedness, checkedAttendanceList ->
    checkedAttendanceList.presentStudents
            .find { areStudentsEqual(it, studentReportingUnpreparedness) }
            .toEither { UnpreparednessError.StudentNotPresent }
}

//workflows
fun markStudentUnprepared(
        checkNumberOfTimesStudentWasUnpreparedInSemester: CheckNumberOfTimesStudentWasUnpreparedInSemester,
        hasStudentUsedAllUnpreparednesses: HasStudentUsedAllUnpreparednesses
): CheckStudentCanReportUnprepared = { presentStudent, className ->
    checkNumberOfTimesStudentWasUnpreparedInSemester(presentStudent, className)
            .handleError { presentStudent.toStudentInSemester(className).let(StudentSubjectUnpreparednessInASemester.Companion::createEmpty) }
            .failIf(hasStudentUsedAllUnpreparednesses, UnpreparednessError.UnpreparedTooManyTimes)
            .mapSuccess { presentStudent }
            .mapError { it as UnpreparednessError }
}

fun noteStudentUnprepared(
        hasStudentAlreadyRaisedUnprepared: HasStudentAlreadyRaisedUnprepared
): NoteStudentUnpreparedForLesson = { presentStudent, studentsUnpreparedForLesson ->
    hasStudentAlreadyRaisedUnprepared(studentsUnpreparedForLesson, presentStudent).not()
            .maybe { UnpreparedStudent(presentStudent.firstName, presentStudent.secondName) }
            .map { studentsUnpreparedForLesson.students + it }
            .map(studentsUnpreparedForLesson::copy)
            .toEither { UnpreparednessError.AlreadyRaised }
}

val writeUnpreparednessInTheRegister: WriteUnpreparednessInTheRegister = { studentSubjectUnpreparednessInASemester ->
    studentSubjectUnpreparednessInASemester.copy(count = studentSubjectUnpreparednessInASemester.count.inc())
}


fun PresentStudent.toStudentInSemester(className: ClassName) =
        StudentInSemester(className, this.firstName, this.secondName)

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
                    .let(AsyncOutputFactory::just)
                    .flatMapAsyncSuccess { checkStudentCanReportUnprepared(it, lesson.identifier.className) }
                    .flatMapSuccess { noteStudentUnpreparedForLesson(it, lesson.unpreparedStudents) }
                    .mapSuccess { createEvent(lesson.identifier, it) }
        else -> AsyncFactory.justError(UnpreparednessError.TooLateToRaiseUnpreparedness)
    }
}