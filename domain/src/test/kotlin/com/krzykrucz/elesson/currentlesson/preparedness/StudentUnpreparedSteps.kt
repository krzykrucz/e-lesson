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
import com.krzykrucz.elesson.currentlesson.preparedness.domain.checkStudentCanReportUnprepared
import com.krzykrucz.elesson.currentlesson.preparedness.domain.checkStudentIsPresent
import com.krzykrucz.elesson.currentlesson.preparedness.domain.createEvent
import com.krzykrucz.elesson.currentlesson.preparedness.domain.hasStudentAlreadyRaisedUnprepared
import com.krzykrucz.elesson.currentlesson.preparedness.domain.hasStudentUsedAllUnpreparednesses
import com.krzykrucz.elesson.currentlesson.preparedness.domain.noteStudentUnprepared
import com.krzykrucz.elesson.currentlesson.preparedness.domain.reportUnpreparedness
import com.krzykrucz.elesson.currentlesson.preparedness.readmodel.StudentInSemester
import com.krzykrucz.elesson.currentlesson.preparedness.readmodel.StudentSubjectUnpreparednessInASemester
import com.krzykrucz.elesson.currentlesson.shared.AsyncFactory
import com.krzykrucz.elesson.currentlesson.shared.ClassName
import com.krzykrucz.elesson.currentlesson.shared.CurrentLesson
import com.krzykrucz.elesson.currentlesson.shared.FirstName
import com.krzykrucz.elesson.currentlesson.shared.InProgressLesson
import com.krzykrucz.elesson.currentlesson.shared.LessonAfterAttendance
import com.krzykrucz.elesson.currentlesson.shared.LessonHourNumber
import com.krzykrucz.elesson.currentlesson.shared.LessonIdentifier
import com.krzykrucz.elesson.currentlesson.shared.NaturalNumber
import com.krzykrucz.elesson.currentlesson.shared.NonEmptyText
import com.krzykrucz.elesson.currentlesson.shared.NumberInRegister
import com.krzykrucz.elesson.currentlesson.shared.Output
import com.krzykrucz.elesson.currentlesson.shared.SecondName
import com.krzykrucz.elesson.currentlesson.topic.domain.LessonOrdinalNumber
import com.krzykrucz.elesson.currentlesson.topic.domain.LessonTopic
import com.krzykrucz.elesson.currentlesson.topic.domain.TopicTitle
import io.cucumber.java8.En
import java.time.LocalDate
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class StudentUnpreparedSteps : En {

    lateinit var attendance: CheckedAttendanceList
    lateinit var lessonIdentifier: LessonIdentifier
    lateinit var currentLesson: CurrentLesson
    lateinit var studentsUnpreparedForLesson: StudentsUnpreparedForLesson
    lateinit var studentSubjectUnpreparednessInASemester: StudentSubjectUnpreparednessInASemester

    val reportUnpreparednessFacade: ReportUnpreparedness = reportUnpreparedness(
        checkStudentCanReportUnprepared(checkNumberOfTimesStudentWasUnpreparedInSemester {
            studentSubjectUnpreparednessInASemester
                .let { AsyncFactory.justSuccess(it) }
        }, hasStudentUsedAllUnpreparednesses),
        noteStudentUnprepared(hasStudentAlreadyRaisedUnprepared),
        checkStudentIsPresent(areStudentsEqual),
        createEvent
    )

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
            val anyTopic = LessonTopic(
                LessonOrdinalNumber(NaturalNumber.ONE),
                TopicTitle(NonEmptyText.of("Wingardium Leviosa charm")!!),
                LocalDate.now()
            )
            currentLesson = InProgressLesson(lessonIdentifier, attendance, studentsUnpreparedForLesson, anyTopic)
        }
        Given("{word} {word} reported unprepared {int} times in a semester") { firstName: String, secondName: String, number: Int ->
            val student = StudentInSemester(
                lessonIdentifier.className,
                FirstName(NonEmptyText.of(firstName)!!),
                SecondName(NonEmptyText.of(secondName)!!)
            )

            studentSubjectUnpreparednessInASemester =
                StudentSubjectUnpreparednessInASemester.create(number, student).orNull()!!
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
                firstName,
                secondName
            )
            result = reportUnpreparednessFacade(student, currentLesson).evaluate()
        }

        Then("{word} {word} should be noted unprepared for lesson") { firstName: String, secondName: String ->
            val expectedStudent = UnpreparedStudent(FirstName(NonEmptyText.of(firstName)!!), SecondName(NonEmptyText.of(secondName)!!))

            assertTrue(result.isRight())
            assertEquals(result.orNull()?.lessonId, lessonIdentifier)
            assertEquals(result.orNull()?.unpreparedStudent, expectedStudent)
        }

        Then("{word} {word} should not be noted unprepared for lesson") { firstName: String, secondName: String ->
            assertTrue(result.isLeft())
        }

    }

}