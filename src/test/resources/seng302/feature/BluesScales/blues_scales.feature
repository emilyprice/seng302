Feature: Blues Scales

  Scenario Outline: Retrieve Notes in Specific Blues Scales
    Given I am on the transcript pane
    When I type the command 'play scale <startingNote> blues'
    Then The following is printed to the transcript pane - <scale>

    Examples:
      | startingNote | scale             |
      | C            | C Eb F Gb G Bb C  |
      | D            | D F G Ab A C D    |
      | E            | E G A Bb B D E    |
      | F            | F Ab Bb B C Eb F  |
      | G            | G Bb C Db D F G   |
      | A            | A C D Eb E G A    |
      | B            | B D E F F# A B    |

      | C#           | C# E F# G G# B C#       |
      | Db           | Db E Gb G Ab B Db       |
      | D#           | D# F# G# A A# C# D#     |
      | Eb           | Eb Gb Ab A Bb Db Eb     |
      | F#           | F# A B C C# E F#        |
      | Gb           | Gb A B C Db E Gb        |
      | G#           | G# B C# D D# F# G#      |
      | Ab           | Ab B Db D Eb Gb Ab      |
      | A#           | A# C# D# E F G# A#      |
      | Bb           | Bb Db Eb E F Ab Bb      |
      | Cb           | Cb D E F Gb A Cb        |


      | c            | C Eb F Gb G Bb C  |
      | d            | D F G Ab A C D    |
      | e            | E G A Bb B D E    |
      | f            | F Ab Bb B C Eb F  |
      | g            | G Bb C Db D F G   |
      | a            | A C D Eb E G A    |
      | b            | B D E F F# A B    |

      | C2           | C2 Eb2 F2 Gb2 G2 Bb2 C3  |
      | D2           | D2 F2 G2 Ab2 A2 C3 D3    |
      | E2           | E2 G2 A2 Bb2 B2 D3 E3    |
      | F2           | F2 Ab2 Bb2 B2 C3 Eb3 F3  |
      | G2           | G2 Bb2 C3 Db3 D3 F3 G3   |
      | A2           | A2 C3 D3 Eb3 E3 G3 A3    |
      | B2           | B2 D3 E3 F3 F#3 A3 B3    |

      | C5           | C5 Eb5 F5 Gb5 G5 Bb5 C6  |
      | D5           | D5 F5 G5 Ab5 A5 C6 D6    |
      | E5           | E5 G5 A5 Bb5 B5 D6 E6    |
      | F5           | F5 Ab5 Bb5 B5 C6 Eb6 F6  |
      | G5           | G5 Bb5 C6 Db6 D6 F6 G6   |
      | A5           | A5 C6 D6 Eb6 E6 G6 A6    |
      | B5           | B5 D6 E6 F6 F#6 A6 B6    |

  Scenario Outline: Retrieve Notes in Specific Blues Scales Downwards
    Given I am on the transcript pane
    When I type the command 'play scale <startingNote> blues down'
    Then The following is printed to the transcript pane - <scale>

    Examples:
      | startingNote | scale             |
      | C            | C Bb G Gb F Eb C  |
      | D            | D C A Ab G F D    |
      | E            | E D B Bb A G E    |
      | F            | F Eb C B Bb Ab F  |
      | G            | G F D Db C Bb G   |
      | A            | A G E Eb D C A    |
      | B            | B A F# F E D B    |

      | C5            | C5 Bb4 G4 Gb4 F4 Eb4 C4  |
      | D5            | D5 C5 A4 Ab4 G4 F4 D4    |
      | E5            | E5 D5 B4 Bb4 A4 G4 E4    |
      | F5            | F5 Eb5 C5 B4 Bb4 Ab4 F4  |
      | G5            | G5 F5 D5 Db5 C5 Bb4 G4   |
      | A5            | A5 G5 E5 Eb5 D5 C5 A4    |
      | B5            | B5 A5 F#5 F5 E5 D5 B4    |

  Scenario Outline: Retrieve Notes in Specific Blues Scales Up and Downwards
    Given I am on the transcript pane
    When I type the command 'play scale <startingNote> blues updown'
    Then The following is printed to the transcript pane - <scale>

    Examples:
      | startingNote | scale                             |
      | C            | C Eb F Gb G Bb C C Bb G Gb F Eb C |
      | D            | D F G Ab A C D D C A Ab G F D     |
      | E            | E G A Bb B D E E D B Bb A G E     |
      | F            | F Ab Bb B C Eb F F Eb C B Bb Ab F |
      | G            | G Bb C Db D F G G F D Db C Bb G   |
      | A            | A C D Eb E G A A G E Eb D C A     |
      | B            | B D E F F# A B B A F# F E D B     |

      | C5           | C5 Eb5 F5 Gb5 G5 Bb5 C6 C6 Bb5 G5 Gb5 F5 Eb5 C5 |
      | D5           | D5 F5 G5 Ab5 A5 C6 D6 D6 C6 A5 Ab5 G5 F5 D5     |
      | E5           | E5 G5 A5 Bb5 B5 D6 E6 E6 D6 B5 Bb5 A5 G5 E5     |
      | F5           | F5 Ab5 Bb5 B5 C6 Eb6 F6 F6 Eb6 C6 B5 Bb5 Ab5 F5 |
      | G5           | G5 Bb5 C6 Db6 D6 F6 G6 G6 F6 D6 Db6 C6 Bb5 G5   |
      | A5           | A5 C6 D6 Eb6 E6 G6 A6 A6 G6 E6 Eb6 D6 C6 A5     |
      | B5           | B5 D6 E6 F6 F#6 A6 B6 B6 A6 F#6 F6 E6 D6 B5     |


  Scenario Outline: Retrieve Notes in Specific Blues Scales over Multiple Octaves
    Given I am on the transcript pane
    When I type the command 'play scale <startingNote> blues <octaves>'
    Then The following is printed to the transcript pane - <scale>

    Examples:
      | startingNote | octaves | scale                           |
      | C            | 2       | C Eb F Gb G Bb C Eb F Gb G Bb C |
      | D            | 2       | D F G Ab A C D F G Ab A C D     |
      | E            | 2       | E G A Bb B D E G A Bb B D E     |
      | F            | 2       | F Ab Bb B C Eb F Ab Bb B C Eb F |
      | G            | 2       | G Bb C Db D F G Bb C Db D F G   |
      | A            | 2       | A C D Eb E G A C D Eb E G A     |
      | B            | 2       | B D E F F# A B D E F F# A B     |

      | C5           | 2       | C5 Eb5 F5 Gb5 G5 Bb5 C6 Eb6 F6 Gb6 G6 Bb6 C7 |
      | D5           | 2       | D5 F5 G5 Ab5 A5 C6 D6 F6 G6 Ab6 A6 C7 D7     |
      | E5           | 2       | E5 G5 A5 Bb5 B5 D6 E6 G6 A6 Bb6 B6 D7 E7     |
      | F5           | 2       | F5 Ab5 Bb5 B5 C6 Eb6 F6 Ab6 Bb6 B6 C7 Eb7 F7 |
      | G5           | 2       | G5 Bb5 C6 Db6 D6 F6 G6 Bb6 C7 Db7 D7 F7 G7   |
      | A5           | 2       | A5 C6 D6 Eb6 E6 G6 A6 C7 D7 Eb7 E7 G7 A7     |
      | B5           | 2       | B5 D6 E6 F6 F#6 A6 B6 D7 E7 F7 F#7 A7 B7     |

      | C3           | 3       | C3 Eb3 F3 Gb3 G3 Bb3 C4 Eb4 F4 Gb4 G4 Bb4 C5 Eb5 F5 Gb5 G5 Bb5 C6 |
      | D3           | 3       | D3 F3 G3 Ab3 A3 C4 D4 F4 G4 Ab4 A4 C5 D5 F5 G5 Ab5 A5 C6 D6       |
      | E3           | 3       | E3 G3 A3 Bb3 B3 D4 E4 G4 A4 Bb4 B4 D5 E5 G5 A5 Bb5 B5 D6 E6       |
      | F3           | 3       | F3 Ab3 Bb3 B3 C4 Eb4 F4 Ab4 Bb4 B4 C5 Eb5 F5 Ab5 Bb5 B5 C6 Eb6 F6 |
      | G3           | 3       | G3 Bb3 C4 Db4 D4 F4 G4 Bb4 C5 Db5 D5 F5 G5 Bb5 C6 Db6 D6 F6 G6    |
      | A3           | 3       | A3 C4 D4 Eb4 E4 G4 A4 C5 D5 Eb5 E5 G5 A5 C6 D6 Eb6 E6 G6 A6       |
      | B3           | 3       | B3 D4 E4 F4 F#4 A4 B4 D5 E5 F5 F#5 A5 B5 D6 E6 F6 F#6 A6 B6       |