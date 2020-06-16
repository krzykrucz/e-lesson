package com.krzykrucz.elesson.currentlesson

import com.krzykrucz.elesson.currentlesson.attendance.PresentStudent
import com.krzykrucz.elesson.currentlesson.shared.ClassName
import com.krzykrucz.elesson.currentlesson.shared.ClassRegistry
import com.krzykrucz.elesson.currentlesson.shared.FirstName
import com.krzykrucz.elesson.currentlesson.shared.LessonHourNumber
import com.krzykrucz.elesson.currentlesson.shared.LessonIdentifier
import com.krzykrucz.elesson.currentlesson.shared.NaturalNumber
import com.krzykrucz.elesson.currentlesson.shared.NonEmptyText
import com.krzykrucz.elesson.currentlesson.shared.NumberInRegister
import com.krzykrucz.elesson.currentlesson.shared.SecondName
import com.krzykrucz.elesson.currentlesson.shared.StudentRecord
import com.krzykrucz.elesson.currentlesson.shared.WholeNumber
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import spock.lang.Specification

import java.time.LocalDate

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AcceptanceSpec extends Specification {
    @Autowired
    protected TestRestTemplate rest

    protected static ClassName className1A() {
        new ClassName(new NonEmptyText("1A"))
    }

    protected static LessonIdentifier lessonIdOfFirst1ALesson() {
        new LessonIdentifier(
                LocalDate.parse("2019-09-09"),
                new LessonHourNumber(NaturalNumber.ONE),
                className1A()
        )
    }

    protected static ClassRegistry classRegistry1A() {
        new ClassRegistry(new ArrayList<StudentRecord>(), className1A())
    }

    protected static PresentStudent presentStudent(String firstNombre, String secondNombre, int number) {
        new PresentStudent(
                firstName(firstNombre),
                secondName(secondNombre),
                new NumberInRegister(new NaturalNumber(number))
        )
    }

    protected static FirstName firstName(String name) {
        new FirstName(new NonEmptyText(name))
    }

    protected static SecondName secondName(String name) {
        new SecondName(new NonEmptyText(name))
    }

    protected static WholeNumber wholeNumber(int number) {
        new WholeNumber(number)
    }
}
