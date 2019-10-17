Feature: Choose Topic

  Scenario: Should choose topic for lesson
    Given Topic title
    And Checked Attendance
    When Choosing a topic
    Then Lesson is in progress
