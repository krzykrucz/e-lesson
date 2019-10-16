Feature: Choose Topic
  Scenario: Should choose topic for lesson
    Given Topic title
    And Attendance is checked
    When Choosing a topic
    Then Lesson is in progress

  Scenario: Should receive error when attendance is not checked
    Given Topic title
    And Attendance is not checked
    When Choosing a topic
    Then Attendance is not checked error is returned