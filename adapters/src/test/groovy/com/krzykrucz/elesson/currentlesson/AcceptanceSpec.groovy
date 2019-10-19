package com.krzykrucz.elesson.currentlesson

import com.krzykrucz.elesson.currentlesson.domain.NaturalNumber
import com.krzykrucz.elesson.currentlesson.domain.NonEmptyText
import com.krzykrucz.elesson.currentlesson.domain.startlesson.ClassName
import com.krzykrucz.elesson.currentlesson.domain.startlesson.LessonHourNumber
import com.krzykrucz.elesson.currentlesson.domain.startlesson.LessonIdentifier
import com.krzykrucz.elesson.currentlesson.infrastructure.Database
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import spock.lang.Specification

import java.time.LocalDate

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class AcceptanceSpec extends Specification {


    protected TestRestTemplate rest
    protected String serverUrl

    void setup() {
        serverUrl = "http://localhost:8081"
        rest = new TestRestTemplate()
        Database.ATTENDANCE_DATABASE.clear()
        Database.IN_PROGRESS_LESSON_DATABASE.clear()
        Database.STARTED_LESSON_DATABASE.clear()
    }

    protected static LessonIdentifier lessonIdOfFirst1ALesson() {
        new LessonIdentifier(
                LocalDate.parse("2019-09-09"),
                new LessonHourNumber(NaturalNumber.ONE),
                className1A()
        )
    }
    protected static ClassName className1A() {
        new ClassName(new NonEmptyText("1A"))
    }
}
