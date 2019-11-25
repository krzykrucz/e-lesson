package com.krzykrucz.elesson.currentlesson.domain.startlesson


import io.cucumber.java8.En

class StartLessonSteps : En {

    init {
        Given("Some teacher") {
        }
        Given("Scheduled lesson for class {word} and {word}") { className: String, time: String ->
        }
        Given("Class registry for class {word}") { className: String ->
        }
        When("Lesson is started at {word}") { startTime: String ->
        }
        Then("Lesson before attendance should be started") {
        }
    }

}
