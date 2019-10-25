Feature: Choose Topic

  Scenario: Should choose topic for lesson
    Given Topic title
    And Checked Attendance
    And Finished Lessons Count
    When Choosing a topic
    Then Lesson is in progress

  Scenario: Should return error when attendance is not checked
    Given Topic title
    And Attendance is not checked
    And Finished Lessons Count
    When Choosing a topic
    Then Choose topic error is returned
