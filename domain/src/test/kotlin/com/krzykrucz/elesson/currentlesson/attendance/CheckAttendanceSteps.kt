package com.krzykrucz.elesson.currentlesson.attendance

import arrow.core.Either
import com.krzykrucz.elesson.currentlesson.attendance.domain.AbsentStudent
import com.krzykrucz.elesson.currentlesson.attendance.domain.Attendance
import com.krzykrucz.elesson.currentlesson.attendance.domain.AttendanceError
import com.krzykrucz.elesson.currentlesson.attendance.domain.CheckedAttendanceList
import com.krzykrucz.elesson.currentlesson.attendance.domain.CompleteListIfAllStudentsChecked
import com.krzykrucz.elesson.currentlesson.attendance.domain.IncompleteAttendanceList
import com.krzykrucz.elesson.currentlesson.attendance.domain.IsInRegistry
import com.krzykrucz.elesson.currentlesson.attendance.domain.NoteAbsence
import com.krzykrucz.elesson.currentlesson.attendance.domain.NoteLate
import com.krzykrucz.elesson.currentlesson.attendance.domain.NotePresence
import com.krzykrucz.elesson.currentlesson.attendance.domain.PresentStudent
import com.krzykrucz.elesson.currentlesson.attendance.domain.Student
import com.krzykrucz.elesson.currentlesson.attendance.domain.UncheckedStudent
import com.krzykrucz.elesson.currentlesson.attendance.domain.addAbsentStudent
import com.krzykrucz.elesson.currentlesson.attendance.domain.addPresentStudent
import com.krzykrucz.elesson.currentlesson.attendance.domain.completeList
import com.krzykrucz.elesson.currentlesson.attendance.domain.getLessonStartTime
import com.krzykrucz.elesson.currentlesson.attendance.domain.isInRegistry
import com.krzykrucz.elesson.currentlesson.attendance.domain.isNotTooLate
import com.krzykrucz.elesson.currentlesson.attendance.domain.noteAbsence
import com.krzykrucz.elesson.currentlesson.attendance.domain.noteLate
import com.krzykrucz.elesson.currentlesson.attendance.domain.notePresence
import com.krzykrucz.elesson.currentlesson.getError
import com.krzykrucz.elesson.currentlesson.getSuccess
import com.krzykrucz.elesson.currentlesson.newClassName
import com.krzykrucz.elesson.currentlesson.newStudent
import com.krzykrucz.elesson.currentlesson.shared.ClassRegistry
import com.krzykrucz.elesson.currentlesson.shared.FirstName
import com.krzykrucz.elesson.currentlesson.shared.LessonHourNumber
import com.krzykrucz.elesson.currentlesson.shared.NaturalNumber
import com.krzykrucz.elesson.currentlesson.shared.NonEmptyText
import com.krzykrucz.elesson.currentlesson.shared.NumberInRegister
import com.krzykrucz.elesson.currentlesson.shared.SecondName
import com.krzykrucz.elesson.currentlesson.shared.isError
import com.krzykrucz.elesson.currentlesson.shared.isSuccess
import io.cucumber.java8.En
import org.assertj.core.api.Assertions.assertThat
import java.time.LocalDateTime

class CheckAttendanceSteps : En {
    lateinit var student: Student
    lateinit var incompleteAttendance: IncompleteAttendanceList
    lateinit var classRegistry: ClassRegistry
    lateinit var currentAttendanceOrError: Either<AttendanceError, Attendance>
    lateinit var currentCheckedAttendance: CheckedAttendanceList
    lateinit var checkedAttendance: CheckedAttendanceList

    val completeListIfAllStudentsChecked: CompleteListIfAllStudentsChecked = completeList()
    val isInRegistry: IsInRegistry = isInRegistry()
    val noteAbsence: NoteAbsence = noteAbsence(isInRegistry, completeListIfAllStudentsChecked, addAbsentStudent())
    val notePresence: NotePresence = notePresence(isInRegistry, completeListIfAllStudentsChecked, addPresentStudent())
    val noteLate: NoteLate = noteLate(isNotTooLate(getLessonStartTime()))


    val className = newClassName("Slytherin")
    val lessonHourNumber = LessonHourNumber.of(NaturalNumber.ONE).orNull()!!

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
            incompleteAttendance = IncompleteAttendanceList()
        }
        And("Checked attendance") {
            checkedAttendance = CheckedAttendanceList(
                    absentStudents = listOf(student as AbsentStudent),
                    presentStudents = emptyList()
            )
        }
        And("Class registry of student") {
            classRegistry = ClassRegistry(
                    students = listOf(newStudent("Tom", "Riddle", 1)),
                    className = className
            )
        }

        When("Noting Student is late") {
            val absentStudent = student as AbsentStudent
            currentCheckedAttendance = noteLate(
                    lessonHourNumber,
                    absentStudent,
                    checkedAttendance,
                    LocalDateTime.now()
            )
        }

        When("Noting Student Presence") {
            currentAttendanceOrError = notePresence(student as UncheckedStudent, incompleteAttendance, classRegistry)
        }

        When("Noting Student Absence") {
            currentAttendanceOrError = noteAbsence(student as UncheckedStudent, incompleteAttendance, classRegistry)
        }


        Then("Attendance has another present student") {
            assertThat(currentAttendanceOrError.isSuccess()).isTrue()
            val updatedAttendance = currentAttendanceOrError.getSuccess() as IncompleteAttendanceList
            val presentStudents = updatedAttendance.presentStudents
            assertThat(presentStudents).hasSize(1)
            assertThat(presentStudents[0]).isEqualToComparingFieldByField(PresentStudent(
                    firstName = student.firstName,
                    secondName = student.secondName,
                    numberInRegister = student.numberInRegister
            ))
        }

        Then("Attendance has another absent student") {
            assertThat(currentAttendanceOrError.isSuccess()).isTrue()
            val updatedAttendance = currentAttendanceOrError.getSuccess() as IncompleteAttendanceList
            val absentStudents = updatedAttendance.absentStudents
            assertThat(absentStudents).hasSize(1)
            assertThat(absentStudents[0]).isEqualToComparingFieldByField(AbsentStudent(
                    firstName = student.firstName,
                    secondName = student.secondName,
                    numberInRegister = student.numberInRegister
            ))
        }
        Then("Attendance is completed") {
            assertThat(currentAttendanceOrError.isSuccess()).isTrue()
            assertThat(currentAttendanceOrError.getSuccess()).isInstanceOf(CheckedAttendanceList::class.java)
        }

        Then("Student is present") {
            assertThat(currentCheckedAttendance.absentStudents).doesNotContain(student as AbsentStudent)
            assertThat(currentCheckedAttendance.presentStudents)
                    .contains(PresentStudent(student.firstName, student.secondName, student.numberInRegister))
        }

        Then("Student is still absent") {
            assertThat(currentCheckedAttendance.absentStudents).contains(student as AbsentStudent)
            assertThat(currentCheckedAttendance.presentStudents)
                    .doesNotContain(PresentStudent(student.firstName, student.secondName, student.numberInRegister))
        }

        Then("The result should be an error explaining that student is not in registry") {
            assertThat(currentAttendanceOrError.isError()).isTrue()
            assertThat(currentAttendanceOrError.getError()).isInstanceOf(AttendanceError.StudentNotInRegistry::class.java)
        }
    }
}