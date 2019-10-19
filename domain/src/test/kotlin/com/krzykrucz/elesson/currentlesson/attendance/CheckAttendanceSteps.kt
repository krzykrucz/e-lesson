package com.krzykrucz.elesson.currentlesson.attendance

import arrow.core.Either
import com.krzykrucz.elesson.currentlesson.attendance.domain.AbsentStudent
import com.krzykrucz.elesson.currentlesson.attendance.domain.AreAllStudentsChecked
import com.krzykrucz.elesson.currentlesson.attendance.domain.Attendance
import com.krzykrucz.elesson.currentlesson.attendance.domain.AttendanceError
import com.krzykrucz.elesson.currentlesson.attendance.domain.AttendanceList
import com.krzykrucz.elesson.currentlesson.attendance.domain.CheckedAttendance
import com.krzykrucz.elesson.currentlesson.attendance.domain.IsInRegistry
import com.krzykrucz.elesson.currentlesson.attendance.domain.IsNotTooLate
import com.krzykrucz.elesson.currentlesson.attendance.domain.NotCompletedAttendance
import com.krzykrucz.elesson.currentlesson.attendance.domain.PresentStudent
import com.krzykrucz.elesson.currentlesson.attendance.domain.Student
import com.krzykrucz.elesson.currentlesson.attendance.domain.UncheckedStudent
import com.krzykrucz.elesson.currentlesson.attendance.domain.noteAbsence
import com.krzykrucz.elesson.currentlesson.attendance.domain.noteLate
import com.krzykrucz.elesson.currentlesson.attendance.domain.notePresence
import com.krzykrucz.elesson.currentlesson.getError
import com.krzykrucz.elesson.currentlesson.getSuccess
import com.krzykrucz.elesson.currentlesson.newClassName
import com.krzykrucz.elesson.currentlesson.newStudent
import com.krzykrucz.elesson.currentlesson.shared.ClassName
import com.krzykrucz.elesson.currentlesson.shared.ClassRegistry
import com.krzykrucz.elesson.currentlesson.shared.FirstName
import com.krzykrucz.elesson.currentlesson.shared.LessonHourNumber
import com.krzykrucz.elesson.currentlesson.shared.LessonIdentifier
import com.krzykrucz.elesson.currentlesson.shared.NaturalNumber
import com.krzykrucz.elesson.currentlesson.shared.NonEmptyText
import com.krzykrucz.elesson.currentlesson.shared.NumberInRegister
import com.krzykrucz.elesson.currentlesson.shared.SecondName
import com.krzykrucz.elesson.currentlesson.shared.isError
import com.krzykrucz.elesson.currentlesson.shared.isSuccess
import io.cucumber.java8.En
import org.assertj.core.api.Assertions.assertThat
import java.time.LocalDate
import java.time.LocalDateTime

class CheckAttendanceSteps : En {
    lateinit var student: Student
    lateinit var notCompletedAttendance: NotCompletedAttendance
    lateinit var classRegistry: ClassRegistry
    lateinit var currentAttendanceOrError: Either<AttendanceError, Attendance>
    lateinit var areAllStudentsChecked: AreAllStudentsChecked
    lateinit var isInRegistry: IsInRegistry
    lateinit var isNotTooLate: IsNotTooLate
    lateinit var currentCheckedAttendance: CheckedAttendance
    lateinit var checkedAttendance: CheckedAttendance
    private val className = newClassName("Slytherin")
    private val lessonHourNumber = LessonHourNumber.of(NaturalNumber.ONE).orNull()!!

    init {
        Given("Student has unchecked attendance") {
            student = UncheckedStudent(
                    firstName = FirstName(NonEmptyText("Tom")),
                    secondName = SecondName(NonEmptyText("Riddle")),
                    numberInRegister = NaturalNumber.of(1).map(::NumberInRegister).orNull()!!
            )
        }
        Given("Student is absent") {
            student = AbsentStudent(
                    firstName = FirstName(NonEmptyText("Salazar")),
                    secondName = SecondName(NonEmptyText("Slytherin")),
                    numberInRegister = NaturalNumber.of(2).map(::NumberInRegister).orNull()!!
            )
        }
        And("Attendance is not completed") {
            notCompletedAttendance = NotCompletedAttendance(
                    attendance = AttendanceList(
                            className = ClassName(NonEmptyText("Black magic")),
                            date = LocalDate.now(),
                            lessonHourNumber = lessonHourNumber
                    ),
                    classRegistry = classRegistry
            )
        }
        And("Checked attendance") {
            checkedAttendance = CheckedAttendance(
                    attendance = AttendanceList(
                            className = ClassName(NonEmptyText("Black magic")),
                            date = LocalDate.now(),
                            lessonHourNumber = lessonHourNumber,
                            absentStudents = listOf(student as AbsentStudent)
                    ), classRegistry = classRegistry)
        }
        And("Class registry of student") {
            classRegistry = ClassRegistry(
                    students = listOf(newStudent("Tom", "Riddle", 1)),
                    className = className
            )
        }
        And("Not all students are checked") {
            areAllStudentsChecked = { _, _ -> false }
        }
        And("All students are checked") {
            areAllStudentsChecked = { _, _ -> true }
        }

        And("Student is in registry") {
            isInRegistry = { _, _ -> true }
        }

        And("Student is not in registry") {
            isInRegistry = { _, _ -> false }
        }

        And("It is not too late") {
            isNotTooLate = { _, _ -> true }
        }

        And("It is too late") {
            isNotTooLate = { _, _ -> false }
        }

        When("Noting Student is late") {
            val absentStudent = student as AbsentStudent
            currentCheckedAttendance = noteLate(isNotTooLate)(
                    LessonIdentifier(LocalDate.now(), lessonHourNumber, className),
                    absentStudent,
                    checkedAttendance,
                    LocalDateTime.now()
            )
        }

        When("Noting Student Presence") {
            currentAttendanceOrError = notePresence(
                    isInRegistry = isInRegistry,
                    areAllStudentsChecked = areAllStudentsChecked
            )(student as UncheckedStudent, notCompletedAttendance)
        }

        When("Noting Student Absence") {
            currentAttendanceOrError = noteAbsence(
                    isInRegistry = isInRegistry,
                    areAllStudentsChecked = areAllStudentsChecked
            )(student as UncheckedStudent, notCompletedAttendance)
        }


        Then("Attendance has another present student") {
            assertThat(currentAttendanceOrError.isSuccess()).isTrue()
            val updatedAttendance = currentAttendanceOrError.getSuccess() as NotCompletedAttendance
            val presentStudents = updatedAttendance.attendance.presentStudents
            assertThat(presentStudents).hasSize(1)
            assertThat(presentStudents[0]).isEqualToComparingFieldByField(PresentStudent(
                    firstName = student.firstName,
                    secondName = student.secondName,
                    numberInRegister = student.numberInRegister
            ))
        }

        Then("Attendance has another absent student") {
            assertThat(currentAttendanceOrError.isSuccess()).isTrue()
            val updatedAttendance = currentAttendanceOrError.getSuccess() as NotCompletedAttendance
            val absentStudents = updatedAttendance.attendance.absentStudents
            assertThat(absentStudents).hasSize(1)
            assertThat(absentStudents[0]).isEqualToComparingFieldByField(AbsentStudent(
                    firstName = student.firstName,
                    secondName = student.secondName,
                    numberInRegister = student.numberInRegister
            ))
        }
        Then("Attendance is completed") {
            assertThat(currentAttendanceOrError.isSuccess()).isTrue()
            assertThat(currentAttendanceOrError.getSuccess()).isInstanceOf(CheckedAttendance::class.java)
        }

        Then("Student is present") {
            assertThat(currentCheckedAttendance.attendance.absentStudents).doesNotContain(student as AbsentStudent)
            assertThat(currentCheckedAttendance.attendance.presentStudents)
                    .contains(PresentStudent(student.firstName, student.secondName, student.numberInRegister))
        }

        Then("Student is still absent") {
            assertThat(currentCheckedAttendance.attendance.absentStudents).contains(student as AbsentStudent)
            assertThat(currentCheckedAttendance.attendance.presentStudents)
                    .doesNotContain(PresentStudent(student.firstName, student.secondName, student.numberInRegister))
        }

        Then("The result should be an error explaining that student is not in registry") {
            assertThat(currentAttendanceOrError.isError()).isTrue()
            assertThat(currentAttendanceOrError.getError()).isInstanceOf(AttendanceError.StudentNotInRegistry::class.java)
        }
    }
}