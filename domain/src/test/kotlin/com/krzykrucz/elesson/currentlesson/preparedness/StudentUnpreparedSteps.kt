package com.krzykrucz.elesson.currentlesson.preparedness


import arrow.core.orNull
import com.krzykrucz.elesson.currentlesson.attendance.domain.CheckedAttendanceList
import com.krzykrucz.elesson.currentlesson.attendance.domain.PresentStudent
import com.krzykrucz.elesson.currentlesson.evaluate
import com.krzykrucz.elesson.currentlesson.preparedness.domain.ReportUnpreparedness
import com.krzykrucz.elesson.currentlesson.preparedness.domain.StudentMarkedUnprepared
import com.krzykrucz.elesson.currentlesson.preparedness.domain.StudentReportingUnpreparedness
import com.krzykrucz.elesson.currentlesson.preparedness.domain.StudentsUnpreparedForLesson
import com.krzykrucz.elesson.currentlesson.preparedness.domain.UnpreparedStudent
import com.krzykrucz.elesson.currentlesson.preparedness.domain.UnpreparednessError
import com.krzykrucz.elesson.currentlesson.preparedness.domain.areStudentsEqual
import com.krzykrucz.elesson.currentlesson.preparedness.domain.checkNumberOfTimesStudentWasUnpreparedInSemester
import com.krzykrucz.elesson.currentlesson.preparedness.domain.checkStudentIsPresent
import com.krzykrucz.elesson.currentlesson.preparedness.domain.createEvent
import com.krzykrucz.elesson.currentlesson.preparedness.domain.hasStudentAlreadyRaisedUnprepared
import com.krzykrucz.elesson.currentlesson.preparedness.domain.hasStudentUsedAllUnpreparednesses
import com.krzykrucz.elesson.currentlesson.preparedness.domain.markStudentUnprepared
import com.krzykrucz.elesson.currentlesson.preparedness.domain.noteStudentUnprepared
import com.krzykrucz.elesson.currentlesson.preparedness.domain.reportUnpreparedness
import com.krzykrucz.elesson.currentlesson.preparedness.readmodel.StudentInSemester
import com.krzykrucz.elesson.currentlesson.preparedness.readmodel.StudentSubjectUnpreparednessInASemester
import com.krzykrucz.elesson.currentlesson.shared.ClassName
import com.krzykrucz.elesson.currentlesson.shared.CurrentLesson
import com.krzykrucz.elesson.currentlesson.shared.FirstName
import com.krzykrucz.elesson.currentlesson.shared.LessonAfterAttendance
import com.krzykrucz.elesson.currentlesson.shared.LessonHourNumber
import com.krzykrucz.elesson.currentlesson.shared.LessonIdentifier
import com.krzykrucz.elesson.currentlesson.shared.NaturalNumber
import com.krzykrucz.elesson.currentlesson.shared.NonEmptyText
import com.krzykrucz.elesson.currentlesson.shared.NumberInRegister
import com.krzykrucz.elesson.currentlesson.shared.Output
import com.krzykrucz.elesson.currentlesson.shared.SecondName
import io.cucumber.java8.En
import java.time.LocalDate
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class StudentUnpreparedSteps : En {

    val reportUnpreparednessFacade: ReportUnpreparedness = reportUnpreparedness(
            markStudentUnprepared(checkNumberOfTimesStudentWasUnpreparedInSemester, hasStudentUsedAllUnpreparednesses),
            noteStudentUnprepared(hasStudentAlreadyRaisedUnprepared),
            checkStudentIsPresent(areStudentsEqual),
            createEvent
    )
    lateinit var attendance: CheckedAttendanceList
    lateinit var lessonIdentifier: LessonIdentifier
    lateinit var currentLesson: CurrentLesson
    lateinit var studentsUnpreparedForLesson: StudentsUnpreparedForLesson
    lateinit var studentSubjectUnpreparednessInASemester: StudentSubjectUnpreparednessInASemester

    lateinit var result: Output<StudentMarkedUnprepared, UnpreparednessError>

    init {
        Given("Present {word} {word} from class {word}") { firstName: String, secondName: String, className: String ->
            attendance = CheckedAttendanceList(
                    listOf(PresentStudent(
                            FirstName(NonEmptyText.of(firstName)!!),
                            SecondName(NonEmptyText.of(secondName)!!),
                            NumberInRegister(NaturalNumber.ONE))
                    ),
                    emptyList()
            )
            lessonIdentifier = LessonIdentifier(
                    LocalDate.now(),
                    LessonHourNumber.of(1).orNull()!!,
                    ClassName(NonEmptyText.of(className)!!)
            )
        }
        Given("Lesson after attendance checked but before topic assigned") {
            currentLesson = LessonAfterAttendance(lessonIdentifier, attendance, studentsUnpreparedForLesson)
        }
        Given("Lesson after topic assigned") {
            TODO()
        }
        Given("{word} {word} reported unprepared {int} times in a semester") { firstName: String, secondName: String, number: Int ->
            val studentInSemester = StudentInSemester(lessonIdentifier.className, FirstName(NonEmptyText.of(firstName)!!), SecondName(NonEmptyText.of(secondName)!!))

            studentSubjectUnpreparednessInASemester = StudentSubjectUnpreparednessInASemester.create(number, studentInSemester).orNull()!!
        }
        Given("Empty list of unprepared students") {
            studentsUnpreparedForLesson = StudentsUnpreparedForLesson()
        }
        Given("{word} {word} on the list of unprepared students") { firstName: String, secondName: String ->
            val unpreparedStudent = UnpreparedStudent(
                    FirstName(NonEmptyText.of(firstName)!!),
                    SecondName(NonEmptyText.of(secondName)!!)
            )

            studentsUnpreparedForLesson = StudentsUnpreparedForLesson(listOf(unpreparedStudent))
        }
        When("{word} {word} reports unprepared") { firstName: String, secondName: String ->
            val student = StudentReportingUnpreparedness(
                    FirstName(NonEmptyText.of(firstName)!!),
                    SecondName(NonEmptyText.of(secondName)!!)
            )
            result = reportUnpreparednessFacade(student, currentLesson).evaluate()
        }

        Then("{word} {word} should be noted unprepared for lesson") { firstName: String, secondName: String ->
            val expectedStudent = UnpreparedStudent(FirstName(NonEmptyText.of(firstName)!!), SecondName(NonEmptyText.of(secondName)!!))

            assertTrue(result.isRight())
            assertEquals(result.orNull()?.lessonId, lessonIdentifier)
            assertEquals(result.orNull()?.editedUnpreparedStudentsList?.students?.size, 1)
            assertEquals(result.orNull()?.editedUnpreparedStudentsList?.students?.get(0), expectedStudent)
        }

        Then("{word} {word} should not be noted unprepared for lesson") { firstName: String, secondName: String ->
            assertTrue(result.isLeft())
        }

    }

}