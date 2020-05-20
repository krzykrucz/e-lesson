package com.krzykrucz.elesson.currentlesson.domain.preparedness


import arrow.core.Either
import arrow.core.orNull
import arrow.core.right
import com.krzykrucz.elesson.currentlesson.domain.attendance.CheckedAttendanceList
import com.krzykrucz.elesson.currentlesson.domain.attendance.PresentStudent
import com.krzykrucz.elesson.currentlesson.domain.preparedness.domain.api.ReportUnpreparedness
import com.krzykrucz.elesson.currentlesson.domain.preparedness.domain.api.StudentMarkedUnprepared
import com.krzykrucz.elesson.currentlesson.domain.preparedness.domain.api.StudentReportingUnpreparedness
import com.krzykrucz.elesson.currentlesson.domain.preparedness.domain.api.StudentsUnpreparedForLesson
import com.krzykrucz.elesson.currentlesson.domain.preparedness.domain.api.UnpreparedStudent
import com.krzykrucz.elesson.currentlesson.domain.preparedness.domain.api.UnpreparednessError
import com.krzykrucz.elesson.currentlesson.domain.preparedness.domain.implementation.areStudentsEqual
import com.krzykrucz.elesson.currentlesson.domain.preparedness.domain.implementation.checkNumberOfTimesStudentWasUnpreparedInSemester
import com.krzykrucz.elesson.currentlesson.domain.preparedness.domain.implementation.checkStudentCanReportUnprepared
import com.krzykrucz.elesson.currentlesson.domain.preparedness.domain.implementation.checkStudentIsPresent
import com.krzykrucz.elesson.currentlesson.domain.preparedness.domain.implementation.createEvent
import com.krzykrucz.elesson.currentlesson.domain.preparedness.domain.implementation.hasStudentAlreadyRaisedUnprepared
import com.krzykrucz.elesson.currentlesson.domain.preparedness.domain.implementation.hasStudentUsedAllUnpreparedness
import com.krzykrucz.elesson.currentlesson.domain.preparedness.domain.implementation.noteStudentUnprepared
import com.krzykrucz.elesson.currentlesson.domain.preparedness.domain.implementation.reportUnpreparedness
import com.krzykrucz.elesson.currentlesson.domain.preparedness.readmodel.StudentInSemester
import com.krzykrucz.elesson.currentlesson.domain.preparedness.readmodel.StudentSubjectUnpreparednessInASemester
import com.krzykrucz.elesson.currentlesson.domain.shared.ClassName
import com.krzykrucz.elesson.currentlesson.domain.shared.CurrentLesson
import com.krzykrucz.elesson.currentlesson.domain.shared.FirstName
import com.krzykrucz.elesson.currentlesson.domain.shared.InProgressLesson
import com.krzykrucz.elesson.currentlesson.domain.shared.LessonAfterAttendance
import com.krzykrucz.elesson.currentlesson.domain.shared.LessonHourNumber
import com.krzykrucz.elesson.currentlesson.domain.shared.LessonIdentifier
import com.krzykrucz.elesson.currentlesson.domain.shared.LessonOrdinalInSemester
import com.krzykrucz.elesson.currentlesson.domain.shared.LessonTopic
import com.krzykrucz.elesson.currentlesson.domain.shared.NaturalNumber
import com.krzykrucz.elesson.currentlesson.domain.shared.NonEmptyText
import com.krzykrucz.elesson.currentlesson.domain.shared.NumberInRegister
import com.krzykrucz.elesson.currentlesson.domain.shared.SecondName
import com.krzykrucz.elesson.currentlesson.domain.shared.TopicTitle
import io.cucumber.java8.En
import kotlinx.coroutines.runBlocking
import java.time.LocalDate
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class StudentUnpreparedSteps : En {

    lateinit var attendance: CheckedAttendanceList
    lateinit var lessonIdentifier: LessonIdentifier
    lateinit var currentLesson: CurrentLesson
    lateinit var studentsUnpreparedForLesson: StudentsUnpreparedForLesson
    lateinit var studentSubjectUnpreparednessInASemester: StudentSubjectUnpreparednessInASemester

    val reportUnpreparednessFacade: ReportUnpreparedness =
        reportUnpreparedness(
            checkStudentCanReportUnprepared(
                checkNumberOfTimesStudentWasUnpreparedInSemester {
                    studentSubjectUnpreparednessInASemester.right()
                },
                hasStudentUsedAllUnpreparedness
            ),
            noteStudentUnprepared(hasStudentAlreadyRaisedUnprepared),
            checkStudentIsPresent(areStudentsEqual),
            createEvent
        )

    lateinit var result: Either<UnpreparednessError, StudentMarkedUnprepared>

    init {
        Given("Present {word} {word} from class {word}") { firstName: String, secondName: String, className: String ->
            attendance = CheckedAttendanceList(
                listOf(
                    PresentStudent(
                        FirstName(
                            NonEmptyText.of(
                                firstName
                            )!!
                        ),
                        SecondName(
                            NonEmptyText.of(
                                secondName
                            )!!
                        ),
                        NumberInRegister(NaturalNumber.ONE)
                    )
                ),
                emptyList()
            )
            lessonIdentifier = LessonIdentifier(
                LocalDate.now(),
                LessonHourNumber.of(1).orNull()!!,
                ClassName(
                    NonEmptyText.of(
                        className
                    )!!
                )
            )
        }
        Given("Lesson after attendance checked but before topic assigned") {
            currentLesson = LessonAfterAttendance(
                lessonIdentifier,
                attendance,
                studentsUnpreparedForLesson
            )
        }
        Given("Lesson after topic assigned") {
            val anyTopic = LessonTopic(
                LessonOrdinalInSemester(NaturalNumber.ONE),
                TopicTitle(
                    NonEmptyText.of(
                        "Wingardium Leviosa charm"
                    )!!
                ),
                LocalDate.now()
            )
            currentLesson = InProgressLesson(
                lessonIdentifier,
                anyTopic
            )
        }
        Given("{word} {word} reported unprepared {int} times in a semester") { firstName: String, secondName: String, number: Int ->
            val student = StudentInSemester(
                lessonIdentifier.className,
                FirstName(
                    NonEmptyText.of(
                        firstName
                    )!!
                ),
                SecondName(
                    NonEmptyText.of(
                        secondName
                    )!!
                )
            )

            studentSubjectUnpreparednessInASemester =
                StudentSubjectUnpreparednessInASemester.create(number, student).orNull()!!
        }
        Given("Empty list of unprepared students") {
            studentsUnpreparedForLesson =
                StudentsUnpreparedForLesson()
        }
        Given("{word} {word} on the list of unprepared students") { firstName: String, secondName: String ->
            val unpreparedStudent =
                UnpreparedStudent(
                    FirstName(
                        NonEmptyText.of(
                            firstName
                        )!!
                    ),
                    SecondName(
                        NonEmptyText.of(
                            secondName
                        )!!
                    )
                )

            studentsUnpreparedForLesson =
                StudentsUnpreparedForLesson(
                    listOf(unpreparedStudent)
                )
        }
        When("{word} {word} reports unprepared") { firstName: String, secondName: String ->
            val student =
                StudentReportingUnpreparedness(
                    firstName,
                    secondName
                )
            result = runBlocking { reportUnpreparednessFacade(student, currentLesson) }
        }

        Then("{word} {word} should be noted unprepared for lesson") { firstName: String, secondName: String ->
            val expectedStudent = UnpreparedStudent(
                FirstName(
                    NonEmptyText.of(
                        firstName
                    )!!
                ),
                SecondName(
                    NonEmptyText.of(secondName)!!
                )
            )

            assertTrue(result.isRight())
            assertEquals(result.orNull()?.lessonId, lessonIdentifier)
            assertEquals(result.orNull()?.unpreparedStudent, expectedStudent)
        }

        Then("{word} {word} should not be noted unprepared for lesson") { firstName: String, secondName: String ->
            assertTrue(result.isLeft())
        }

    }

}
