package com.krzykrucz.elesson.currentlesson.attendance

import com.krzykrucz.elesson.currentlesson.AcceptanceSpec
import com.krzykrucz.elesson.currentlesson.domain.NaturalNumber
import com.krzykrucz.elesson.currentlesson.domain.NonEmptyText
import com.krzykrucz.elesson.currentlesson.domain.attendance.*
import com.krzykrucz.elesson.currentlesson.domain.startlesson.FirstName
import com.krzykrucz.elesson.currentlesson.domain.startlesson.LessonHourNumber
import com.krzykrucz.elesson.currentlesson.domain.startlesson.NumberInRegister
import com.krzykrucz.elesson.currentlesson.domain.startlesson.SecondName

import java.time.LocalDate

class AttendanceBaseSpec extends AcceptanceSpec {

    protected static UncheckedStudent uncheckedStudentOf(String name, String surname, int number) {
        new UncheckedStudent(
                new FirstName(new NonEmptyText(name)),
                new SecondName(new NonEmptyText(surname)),
                new NumberInRegister(new NaturalNumber(number))
        )
    }

    protected static PresentStudent presentStudentOf(String name, String surname, int number) {
        new PresentStudent(
                new FirstName(new NonEmptyText(name)),
                new SecondName(new NonEmptyText(surname)),
                new NumberInRegister(new NaturalNumber(number))
        )
    }

    protected static AbsentStudent absentStudentOf(String name, String surname, int number) {
        new AbsentStudent(
                new FirstName(new NonEmptyText(name)),
                new SecondName(new NonEmptyText(surname)),
                new NumberInRegister(new NaturalNumber(number))
        )
    }

    protected static NotCompletedAttendance createEmptyAttendance() {
        new NotCompletedAttendance(new AttendanceList(
                className1A(),
                LocalDate.parse("2019-09-09"),
                new LessonHourNumber(NaturalNumber.ONE),
                new LinkedList<PresentStudent>(),
                new LinkedList<AbsentStudent>()
        ))
    }

}
