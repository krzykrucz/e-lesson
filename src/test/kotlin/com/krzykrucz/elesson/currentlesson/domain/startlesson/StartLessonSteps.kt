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
        Given("Failed to check lesson schedule") {
        }
        Given("Failed to fetch class registry") {
        }
        When("Lesson is started at {word}") { startTime: String ->
        }
        Then("Lesson before attendance should be started") {
        }
        Then("Lesson should not be started because no scheduled lesson") {
        }
        Then("Lesson should not be started because class registry unavailable") {
        }
    }

}
