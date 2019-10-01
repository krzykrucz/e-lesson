package com.krzykrucz.elesson.currentlesson.domain.attendance

import com.krzykrucz.elesson.currentlesson.domain.startlesson.FirstName
import com.krzykrucz.elesson.currentlesson.domain.startlesson.NumberInRegister
import com.krzykrucz.elesson.currentlesson.domain.startlesson.SecondName

sealed class Student(val firstName: FirstName,
                     val secondName: SecondName,
                     val numberInRegister: NumberInRegister) {

    data class UncheckedStudent(val first: FirstName, val second: SecondName, val no: NumberInRegister) : Student(
            first, second, no
    ) {
        fun toAbsent(): AbsentStudent =
                AbsentStudent(this.first, this.second, this.no)

        fun toPresent(): PresentStudent =
                PresentStudent(this.first, this.second, this.no)
    }

    data class AbsentStudent(val first: FirstName, val second: SecondName, val no: NumberInRegister) : Student(
            first, second, no

    ) {
        fun toLate(): LateStudent =
                LateStudent(this.first, this.second, this.no)
    }

    data class PresentStudent(val first: FirstName, val second: SecondName, val no: NumberInRegister) : Student(
            first, second, no
    )

    data class LateStudent(val first: FirstName, val second: SecondName, val no: NumberInRegister) : Student(
            first, second, no
    )
}