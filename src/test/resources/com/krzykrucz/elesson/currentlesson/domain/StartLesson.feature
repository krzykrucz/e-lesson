Feature: Start lesson

  Scenario Outline: Should start new lesson after bell rang and before the next one
    Given Teacher <teacher>
    And Current time <startTime>
    And Scheduled lesson for class Gryffindor and lesson number 1
    And Class registry for class Gryffindor
    When Lesson is started
    Then Lesson before attendance should be started
    Examples:
      | startTime        | teacher              |
      | 2019-09-09T08:00 | "Albus Dumbledore"   |
      | 2019-09-09T08:44 | "Minerva McGonagall" |