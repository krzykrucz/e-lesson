Feature: Student reports unprepared to lesson

  Scenario: Should note student unprepared
    Given Present Ron Weasley from class 1A
    And Ron Weasley reported unprepared 2 times in a semester
    And Empty list of unprepared students
    And Lesson after attendance checked but before topic assigned
    When Ron Weasley reports unprepared
    Then Ron Weasley should be noted unprepared for lesson

  Scenario: Should not note not present student unprepared
    Given Present Ron Weasley from class 1A
    And Ron Weasley reported unprepared 2 times in a semester
    And Empty list of unprepared students
    And Lesson after attendance checked but before topic assigned
    When Harry Potter reports unprepared
    Then Harry Potter should not be noted unprepared for lesson

  Scenario: Should not note not present student unprepared
    Given Present Ron Weasley from class 1A
    And Ron Weasley reported unprepared 2 times in a semester
    And Empty list of unprepared students
    And Lesson after topic assigned
    When Ron Weasley reports unprepared
    Then Ron Weasley should not be noted unprepared for lesson

  Scenario: Should not note student unprepared twice
    Given Present Ron Weasley from class 1A
    And Ron Weasley reported unprepared 2 times in a semester
    And Ron Weasley on the list of unprepared students
    And Lesson after attendance checked but before topic assigned
    When Ron Weasley reports unprepared
    Then Ron Weasley should not be noted unprepared for lesson

  Scenario: Should not note student unprepared too many times in the semester
    Given Present Ron Weasley from class 1A
    And Ron Weasley reported unprepared 3 times in a semester
    And Empty list of unprepared students
    And Lesson after attendance checked but before topic assigned
    When Ron Weasley reports unprepared
    Then Ron Weasley should not be noted unprepared for lesson

