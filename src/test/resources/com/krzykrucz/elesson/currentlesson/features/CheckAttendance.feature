Feature: Check Attendance
  Scenario: Should note student's presence
    Given Student has unchecked attendance
    And Class registry of student
    And Attendance is not completed
    When Noting Student Presence
    Then Attendance has another present student

  Scenario: Should note student's absence
    Given Student has unchecked attendance
    And Class registry of student
    And Attendance is not completed
    When Noting Student Absence
    Then Attendance has another absent student

  Scenario: Should return error when student is not in registry when noting presence
    Given Student that is not in registry
    And Class registry of student
    And Attendance is not completed
    When Noting Student Presence
    Then The result should be an error explaining that student is not in registry

  Scenario: Should return error when student is not in registry when noting absence
    Given Student that is not in registry
    And Class registry of student
    And Attendance is not completed
    When Noting Student Absence
    Then The result should be an error explaining that student is not in registry

  Scenario: Should complete attendance when noting presence of last student
    Given Student has unchecked attendance
    And Class registry of student
    And Attendance has only one unchecked student
    When Noting Student Presence
    Then Attendance is completed

  Scenario: Should complete attendance when noting absence of last student
    Given Student has unchecked attendance
    And Class registry of student
    And Attendance has only one unchecked student
    When Noting Student Absence
    Then Attendance is completed

  Scenario: Should note student's late if he or she is not late less than 15 minutes
    Given Student is absent
    And Class registry of student
    And Checked attendance
    And That the current time is 2019-09-09T10:05
    When Noting Student is late
    Then Student is present

  Scenario: Should leave student absent when he or she is late more than 15 minutes
    Given Student is absent
    And Class registry of student
    And Checked attendance
    And That the current time is 2019-09-09T10:18
    When Noting Student is late
    Then Student is still absent
