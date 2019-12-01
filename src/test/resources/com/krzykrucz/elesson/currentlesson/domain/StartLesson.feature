Feature: Start lesson

  Scenario Outline: Should start new lesson after bell rang and before the next one
    Given Teacher 'Albus Dumbledore'
    And Current time <startTime>
    And Scheduled lesson for class Gryffindor and lesson number <lessonNumber>
    And Class registry for class Gryffindor
    When Lesson is started
    Then Lesson before attendance should be started
    Examples:
      | startTime        | lessonNumber |
      | 2019-09-09T08:00 | 1            |
      | 2019-09-09T08:44 | 1            |
      | 2019-09-09T08:55 | 2            |
      | 2019-09-09T09:50 | 3            |
