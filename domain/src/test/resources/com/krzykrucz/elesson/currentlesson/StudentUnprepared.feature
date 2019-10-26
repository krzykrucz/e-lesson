Feature: Student reports unprepared to lesson

  Scenario: Should note student unprepared
    Given Present Ron Weasley from class 1A
    And Lesson after attendance checked but before topic assigned
    And Ron Weasley reported unprepared 2 times in a semester
    And Empty list of unprepared students
    When Ron Weasley reports unprepared
    Then Ron Weasley should be noted unprepared for lesson
    And Ron Weasley should've been unprepared 3 times in the semester

  Scenario: Should not note not present student unprepared
    Given Present Ron Weasley from class 1A
    And Lesson after attendance checked but before topic assigned
    And Ron Weasley reported unprepared 2 times in a semester
    And Empty list of unprepared students
    When Harry Potter reports unprepared
    Then Harry Potter should not be noted unprepared for lesson
    And Ron Weasley should've been unprepared 2 times in the semester

  Scenario: Should not note not present student unprepared
    Given Present Ron Weasley from class 1A
    And Lesson after topic assigned
    And Ron Weasley reported unprepared 2 times in a semester
    And Empty list of unprepared students
    When Ron Weasley reports unprepared
    Then Ron Weasley should not be noted unprepared for lesson
    And Ron Weasley should've been unprepared 2 times in the semester

  Scenario: Should not note student unprepared twice
    Given Present Ron Weasley from class 1A
    And Lesson after topic assigned
    And Ron Weasley reported unprepared 2 times in a semester
    And Ron Weasley on the list of unprepared students
    When Ron Weasley reports unprepared
    Then Ron Weasley should not be noted unprepared for lesson
    And Ron Weasley should've been unprepared 2 times in the semester

  Scenario: Should not note student unprepared too many times in the semester
    Given Present Ron Weasley from class 1A
    And Lesson after attendance checked but before topic assigned
    And Ron Weasley reported unprepared 3 times in a semester
    And Empty list of unprepared students
    When Ron Weasley reports unprepared
    Then Ron Weasley should not be noted unprepared for lesson
    And Ron Weasley should've been unprepared 3 times in the semester

