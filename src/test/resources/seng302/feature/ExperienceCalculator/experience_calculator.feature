Feature: Experience Calculator

  Scenario Outline: Calculate the correct experience from a tutoring session
    Given I have completed a tutoring session
    When I get <correct> questions out of <answered>
    Then The calculator returns a value of <xp> experience points

    Examples:
      | correct | answered | xp  |
      | 10      | 10       | 110 |
      | 50      | 50       | 550 |
      | 25      | 50       | 250 |
      | 40      | 50       | 420 |
      | 9       | 10       | 99  |
      | 75      | 100      | 787 |
      | 0       | 5        | 0   |
