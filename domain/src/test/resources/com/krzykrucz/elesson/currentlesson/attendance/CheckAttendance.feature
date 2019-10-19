Feature: Check Attendance
  Scenario: Should note student's presence
    Given Student has unchecked attendance
    And Attendance is not completed
    And Class registry of student
    And Not all students are checked
    And Student is in registry
    When Noting Student Presence
    Then Attendance has another present student

  Scenario: Should note student's absence
    Given Student has unchecked attendance
    And Attendance is not completed
    And Class registry of student
    And Not all students are checked
    And Student is in registry
    When Noting Student Absence
    Then Attendance has another absent student

  Scenario: Should return error when student is not in registry when noting presence
    Given Student has unchecked attendance
    And Attendance is not completed
    And Class registry of student
    And Not all students are checked
    And Student is not in registry
    When Noting Student Presence
    Then The result should be an error explaining that student is not in registry

  Scenario: Should return error when student is not in registry when noting absence
    Given Student has unchecked attendance
    And Attendance is not completed
    And Class registry of student
    And Not all students are checked
    And Student is not in registry
    When Noting Student Absence
    Then The result should be an error explaining that student is not in registry

  Scenario: Should complete attendance when noting presence of last student
    Given Student has unchecked attendance
    And Attendance is not completed
    And Class registry of student
    And All students are checked
    And Student is in registry
    When Noting Student Presence
    Then Attendance is completed

  Scenario: Should complete attendance when noting absence of last student
    Given Student has unchecked attendance
    And Attendance is not completed
    And Class registry of student
    And All students are checked
    And Student is in registry
    When Noting Student Absence
    Then Attendance is completed

  Scenario: Should note student's late if he or she is not late less than 15 minutes
    Given Student is absent
    And Checked attendance
    And Class registry of student
    And It is not too late
    When Noting Student is late
    Then Student is present

  Scenario: Should leave student absent when he or she is late more than 15 minutes
    Given Student is absent
    And Checked attendance
    And Class registry of student
    And It is too late
    When Noting Student is late
    Then Student is still absent