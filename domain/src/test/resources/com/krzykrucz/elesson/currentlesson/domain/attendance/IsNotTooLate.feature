Feature: Determining if it's too late to be present on lesson

  Scenario Outline: Should tell that it is too late when there's more than 15 minutes difference between now and lesson start
    Given Lesson hour number is 3
    And Time is at <currentTime>
    When Checking if it is too late
    Then It's too late
    Examples:
      | currentTime      |
      | 2019-09-09T10:55 |


  Scenario Outline: Should tell that it is ok when there's less or equal than 15 minutes difference between now and lesson start
    Given Lesson hour number is 3
    And Time is at <currentTime>
    When Checking if it is too late
    Then It's not too late
    Examples:
      | currentTime      |
      | 2019-09-09T10:43 |
