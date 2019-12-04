Feature: Start lesson

  Scenario Outline: Should start new lesson after bell rang and before the next one
    Given Teacher 'Albus Dumbledore'
    And Current time <startTime>
    And Scheduled lesson for class Gryffindor, lesson number 1 and date 2019-09-09
    And Class registry for class Gryffindor
    When Lesson is started
    Then Lesson before attendance should be started
    Examples:
      | startTime        |
      | 2019-09-09T08:00 |
      | 2019-09-09T08:44 |

  Scenario Outline: Should fail to start new lesson when before or after bell
    Given Teacher 'Albus Dumbledore'
    And Current time <startTime>
    And Scheduled lesson for class Gryffindor, lesson number 1 and date 2019-09-09
    And Class registry for class Gryffindor
    When Lesson is started
    Then Lesson should not be started because it's too late or too soon
    Examples:
      | startTime        |
      | 2019-09-09T07:59 |
      | 2019-09-09T08:46 |
