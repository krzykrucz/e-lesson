Feature: Finish Lesson

  Scenario: Should finish lesson after 45min
    Given Current time of 10:45:30
    And In progress lesson number 1
    When Finishing a lesson
    Then Lesson is finished

  Scenario: Should not finish lesson before 45min
    Given Current time of 10:20:00
    And In progress lesson number 1
    When Finishing a lesson
    Then Lesson is not finished

