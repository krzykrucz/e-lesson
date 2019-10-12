Feature: Start lesson

  Scenario Outline: Should start new lesson after bell rang and before the next one
    Given Some teacher
    And Scheduled lesson for class 1A and 2019-09-09T10:00
    And Class registry for class 1A
    When Lesson is started at <startTime>
    Then Lesson before attendance should be started
    Examples:
      | startTime        |
      | 2019-09-09T10:00 |
      | 2019-09-09T10:44 |

  Scenario Outline: Should not start a new lesson before bell rang or after the next one
    Given Some teacher
    And Scheduled lesson for class 1A and 2019-09-09T10:00
    And Class registry for class 1A
    When Lesson is started at <startTime>
    Then Lesson should not be started because no scheduled lesson
    Examples:
      | startTime        |
      | 2019-09-09T09:59 |
      | 2019-09-09T10:45 |

  Scenario: Should not start new lesson without a schedule
    Given Some teacher
    And Failed to check lesson schedule
    When Lesson is started at 2019-09-09T10:00
    Then Lesson should not be started because no scheduled lesson

  Scenario: Should not start new lesson without a class registry
    Given Some teacher
    And Scheduled lesson for class 1A and 2019-09-09T10:00
    And Failed to fetch class registry
    When Lesson is started at 2019-09-09T10:00
    Then Lesson should not be started because class registry unavailable

  # TODO
#  Scenario: Should not start new lesson twice
#    Given Some teacher
#    And Scheduled lesson for class 1A and 2019-09-09T10:00
#    And Class registry for class 1A
#    And Lesson was already started
#    When Lesson is started at 2019-09-09T10:00
#    Then Lesson should not be started because it's already started
