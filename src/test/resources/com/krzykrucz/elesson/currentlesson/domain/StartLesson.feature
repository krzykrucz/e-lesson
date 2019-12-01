Feature: Start lesson

  Scenario Outline: Should start new lesson after bell rang and before the next one
    Given Teacher 'Albus Dumbledore'
    And Current time <startTime>
    And Scheduled lesson for class <className> and lesson number 1
    And Class registry for class Gryffindor
    When Lesson is started
    Then Lesson before attendance should be started
    Examples:
      | startTime        | className  |
      | 2019-09-09T08:00 | Gryffindor |
      | 2019-09-09T08:44 | Gryffindor |
      | 2019-09-09T08:00 | Slytherin  |
