package com.krzykrucz.elesson.currentlesson

import com.krzykrucz.elesson.currentlesson.shared.*
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
}
