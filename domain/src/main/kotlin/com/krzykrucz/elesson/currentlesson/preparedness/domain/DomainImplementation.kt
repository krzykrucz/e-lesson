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
    StudentInSemesterReadModel.getStudentSubjectUnpreparednessInASemester(StudentInSemester(className, student.firstName, student.secondName))
}

val hasStudentAlreadyRaisedUnprepared: HasStudentAlreadyRaisedUnprepared = { studentsUnpreparedForLesson, unpreparedStudent ->
    studentsUnpreparedForLesson.students.contains(unpreparedStudent)
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
): MarkStudentUnprepared = { presentStudent, className ->
    checkNumberOfTimesStudentWasUnpreparedInSemester(presentStudent, className)
            .handleError { presentStudent.toStudentInSemester(className).let(StudentSubjectUnpreparednessInASemester.Companion::createEmpty) }
            .failIf(hasStudentUsedAllUnpreparednesses, UnpreparednessError.UnpreparedTooManyTimes as UnpreparednessError)
            .mapSuccess { UnpreparedStudent(presentStudent.firstName, presentStudent.secondName) }
            .mapError { UnpreparednessError.Unknown }
}

fun noteStudentUnprepared(
        hasStudentAlreadyRaisedUnprepared: HasStudentAlreadyRaisedUnprepared
): NoteStudentUnpreparedInTheRegister = { unpreparedStudent, studentsUnpreparedForLesson ->
    hasStudentAlreadyRaisedUnprepared(studentsUnpreparedForLesson, unpreparedStudent)
            .maybe { studentsUnpreparedForLesson.students + unpreparedStudent }
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
            editedUnpreparedStudentsList = studentsUnpreparedForLesson
    )
}

//pipeline
fun reportUnpreparedness(
        markStudentUnprepared: MarkStudentUnprepared,
        noteStudentUnpreparedInTheRegister: NoteStudentUnpreparedInTheRegister,
        checkStudentIsPresent: CheckStudentIsPresent,
        createEvent: CreateEvent
): ReportUnpreparedness = { studentReportingUnpreparedness, lesson ->

    when (lesson) {
        is LessonAfterAttendance ->
            checkStudentIsPresent(studentReportingUnpreparedness, lesson.attendance)
                    .let(AsyncOutputFactory::just)
                    .flatMapAsyncSuccess { markStudentUnprepared(it, lesson.identifier.className) }
                    .flatMapSuccess { noteStudentUnpreparedInTheRegister(it, lesson.unpreparedStudents) }
                    .mapSuccess { createEvent(lesson.identifier, it) }
                    .mapError { UnpreparednessError.Unknown }
        else -> AsyncFactory.justError(UnpreparednessError.TooLateToRaiseUnpreparedness)
    }
}