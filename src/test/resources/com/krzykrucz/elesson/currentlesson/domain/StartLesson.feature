Feature: Start lesson

  Scenario Outline: Should start new lesson after bell rang and before the next one
    Given Teacher 'Albus Dumbledore'
    And Current time <startTime>
    And Scheduled lesson for class 1A and 2019-09-09T10:00
    And Class registry for class 1A
    When Lesson is started
    Then Lesson before attendance should be started
    Examples:
      | startTime        |
      | 2019-09-09T10:00 |
      | 2019-09-09T10:44 |