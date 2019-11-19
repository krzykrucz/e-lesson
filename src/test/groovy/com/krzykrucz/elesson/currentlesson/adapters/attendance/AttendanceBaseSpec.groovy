package com.krzykrucz.elesson.currentlesson.adapters.attendance

import com.krzykrucz.elesson.currentlesson.adapters.AcceptanceSpec
import com.krzykrucz.elesson.currentlesson.domain.attendance.AbsentStudent
import com.krzykrucz.elesson.currentlesson.domain.attendance.PresentStudent
import com.krzykrucz.elesson.currentlesson.domain.attendance.UncheckedStudent
import com.krzykrucz.elesson.currentlesson.domain.shared.*

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

}
