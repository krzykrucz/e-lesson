Feature: Start lesson

  Scenario Outline: Should start new lesson after bell rang and before the next one
    Given Some teacher
    And Scheduled lesson for class 1A and 2019-09-09T10:00
    And Class registry for class 1A
    When Lesson is started at <startTime>
    Then Lesson should be started
    And Lesson should be before attendance
    Examples:
      | startTime        |
      | 2019-09-09T10:00 |
      | 2019-09-09T10:44 |

  Scenario Outline: Should not start a new lesson before bell rang or after the next one
    Given Some teacher
    And Scheduled lesson for class 1A and 2019-09-09T10:00
    And Class registry for class 1A
    When Lesson is started at <startTime>
    Then Lesson should not be started
    Examples:
      | startTime        |
      | 2019-09-09T09:59 |
      | 2019-09-09T10:45 |

  Scenario: Should not start new lesson without a schedule
    Given Some teacher
    When Lesson is started at 2019-09-09T10:00
    Then Lesson should not be started

  Scenario: Should not start new lesson twice
    Given Some teacher
    And Scheduled lesson for class 1A and 2019-09-09T10:00
    And Class registry for class 1A
    When Lesson is started at 2019-09-09T10:00
    And Lesson is started at 2019-09-09T10:01
    Then Lesson should be started
