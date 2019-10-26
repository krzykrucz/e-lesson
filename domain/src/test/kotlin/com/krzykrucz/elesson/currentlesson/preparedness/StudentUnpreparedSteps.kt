package com.krzykrucz.elesson.currentlesson.preparedness


import arrow.effects.IO
import com.krzykrucz.elesson.currentlesson.lessonHourNumberOf
import com.krzykrucz.elesson.currentlesson.shared.ClassName
import com.krzykrucz.elesson.currentlesson.shared.Output
import com.krzykrucz.elesson.currentlesson.shared.Teacher
import com.krzykrucz.elesson.currentlesson.startlesson.domain.CheckScheduledLesson
import com.krzykrucz.elesson.currentlesson.startlesson.domain.FetchClassRegistry
import com.krzykrucz.elesson.currentlesson.startlesson.domain.StartLessonError
import com.krzykrucz.elesson.currentlesson.startlesson.domain.StartedLesson
import io.cucumber.java8.En
import java.time.LocalDate
import java.time.LocalDateTime

class StudentUnpreparedSteps : En {
    lateinit var teacher: Teacher
    var scheduledLessonProvider: CheckScheduledLesson = { teacher, localDateTime -> IO.never }
    var classRegistryProvider: FetchClassRegistry = { className -> IO.never }
    //    var checkLessonStarted: CheckLessonStarted = { false }
    lateinit var currentLessonOrError: Output<StartedLesson, StartLessonError>
    lateinit var givenClassName: ClassName
    lateinit var givenDate: LocalDate
    lateinit var attemptedStartTime: LocalDateTime
    private val givenLessonHourNumber = lessonHourNumberOf(1)

    init {
        Given("Present {word} {word} from class {word}") { firstName: String, secondName: String, className: String ->

        }
        Given("Lesson after attendance checked but before topic assigned") {

        }
        Given("Lesson after topic assigned") {

        }
        Given("{word} {word} reported unprepared {int} times in a semester") { firstName: String, secondName: String, number: Int ->

        }
        Given("Empty list of unprepared students") {

        }
        Given("{word} {word} on the list of unprepared students") { firstName: String, secondName: String ->

        }
        When("{word} {word} reports unprepared") { firstName: String, secondName: String ->

        }
        Then("{word} {word} should've been unprepared {int} times in the semester") { firstName: String, secondName: String, number: Int ->

        }

        Then("{word} {word} should be noted unprepared for lesson") { firstName: String, secondName: String ->

        }

        Then("{word} {word} should not be noted unprepared for lesson") { firstName: String, secondName: String ->

        }

    }

}