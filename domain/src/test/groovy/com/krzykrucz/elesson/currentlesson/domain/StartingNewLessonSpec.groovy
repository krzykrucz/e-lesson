package com.krzykrucz.elesson.currentlesson.domain

import arrow.effects.IO
import spock.lang.Specification

import java.time.LocalDateTime

import static com.krzykrucz.elesson.currentlesson.domain.WorkflowImplementationKt.startLesson
import static java.time.LocalDateTime.now

class StartingNewLessonSpec extends Specification {

    final static EL_PROFESOR = teacherOf("El", "Profesor")
    final static POTTER = studentOf("Harry", "Potter", 2)
    final static CLASS_1A = new ClassName(new NonEmptyText("1A"))
    final static CALENDAR = new TeacherCalendar([])
    final static CLASS = new ClassRegistry([POTTER], CLASS_1A)

    def "should start new lesson after bell rang and before the next one"() {
        given: "teacher"
        def teacher = EL_PROFESOR
        and: "scheduled lesson for this teacher"
        def checkSchedule = { teacherr, time -> new ScheduledLesson(time, teacherr, CLASS_1A, CALENDAR) }
        and: "class registry"
        def fetchRegistry = { className -> className == CLASS_1A ? new IO<ClassRegistry>.Pure(CLASS) : null }
        when: "lesson is started between ring bells"
        startLesson()(fetchRegistry, checkSchedule, teacher, now())
        then: "there is lesson before attendace"
    }

    def "should not start new lesson without a schedule"() {
        given: "teacher"
        and: "no scheduled lesson for this teacher"
        when: "lesson is started between ring bells"
        then: "there is no lesson before attendace"
    }

    def "should not start new lesson twice"() {
        given: "teacher"
        and: "scheduled lesson for this teacher"
        and: "lesson is started between ring bells"
        when: "lesson is started between ring bells"
        then: "there is no lesson before attendace"
    }

    private static Teacher teacherOf(String firstName, String secondName) {
        new Teacher(new FirstName(new NonEmptyText(firstName)), new SecondName(new NonEmptyText(secondName)))
    }
    private static Student studentOf(String firstName, String secondName, int registryOrdinal) {
        new Student(
                new FirstName(new NonEmptyText(firstName)),
                new SecondName(new NonEmptyText(secondName)),
                new NumberInRegister(new NaturalNumber(registryOrdinal))
        )
    }

}