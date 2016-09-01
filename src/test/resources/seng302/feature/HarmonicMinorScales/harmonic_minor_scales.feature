Feature: Harmonic Minor Scales

  Scenario Outline: Retrieve Notes in Specific Harmonic Minor Scales
    Given I am on the transcript pane
    When I type the command 'scale <startingNote> harmonic minor'
    Then The following is printed to the transcript pane - <scale>

    Examples:
      | startingNote | scale                      |
      | C            | C D Eb F G Ab B C          |
      | D            | D E F G A Bb C# D          |
      | E            | E F# G A B C D# E          |
      | F            | F G Ab Bb C Db E F         |
      | G            | G A Bb C D Eb F# G         |
      | A            | A B C D E F G# A           |
      | B            | B C# D E F# G A# B         |

      | c            | C D Eb F G Ab B C          |
      | d            | D E F G A Bb C# D          |
      | e            | E F# G A B C D# E          |
      | f            | F G Ab Bb C Db E F         |
      | g            | G A Bb C D Eb F# G         |
      | a            | A B C D E F G# A           |
      | b            | B C# D E F# G A# B         |

      | C2           | C2 D2 Eb2 F2 G2 Ab2 B2 C3  |
      | D2           | D2 E2 F2 G2 A2 Bb2 C#3 D3  |
      | E2           | E2 F#2 G2 A2 B2 C3 D#3 E3  |
      | F2           | F2 G2 Ab2 Bb2 C3 Db3 E3 F3 |
      | G2           | G2 A2 Bb2 C3 D3 Eb3 F#3 G3 |
      | A2           | A2 B2 C3 D3 E3 F3 G#3 A3   |
      | B2           | B2 C#3 D3 E3 F#3 G3 A#3 B3 |


  Scenario Outline: Retrieve Notes in Specific Harmonic Minor Scales Played Normally
    Given I am on the transcript pane
    When I type the command 'play scale <startingNote> harmonic minor'
    Then The following is printed to the transcript pane - <scale>

    Examples:
      | startingNote | scale                      |
      | C            | C D Eb F G Ab B C          |
      | D            | D E F G A Bb C# D          |
      | E            | E F# G A B C D# E          |
      | F            | F G Ab Bb C Db E F         |
      | G            | G A Bb C D Eb F# G         |
      | A            | A B C D E F G# A           |
      | B            | B C# D E F# G A# B         |


      | C2           | C2 D2 Eb2 F2 G2 Ab2 B2 C3  |
      | D2           | D2 E2 F2 G2 A2 Bb2 C#3 D3  |
      | E2           | E2 F#2 G2 A2 B2 C3 D#3 E3  |
      | F2           | F2 G2 Ab2 Bb2 C3 Db3 E3 F3 |
      | G2           | G2 A2 Bb2 C3 D3 Eb3 F#3 G3 |
      | A2           | A2 B2 C3 D3 E3 F3 G#3 A3   |
      | B2           | B2 C#3 D3 E3 F#3 G3 A#3 B3 |

  Scenario Outline: Retrieve Notes in Specific Harmonic Minor Scales Played Downwards
    Given I am on the transcript pane
    When I type the command 'play scale <startingNote> harmonic minor down'
    Then The following is printed to the transcript pane - <scale>

    Examples:
      | startingNote | scale                      |
      | C            | C B Ab G F Eb D C          |
      | D            | D C# Bb A G F E D          |
      | E            | E D# C B A G F# E          |
      | F            | F E Db C Bb Ab G F         |
      | G            | G F# Eb D C Bb A G         |
      | A            | A G# F E D C B A           |
      | B            | B A# G F# E D C# B         |


      | C2           | C2 B1 Ab1 G1 F1 Eb1 D1 C1  |
      | D2           | D2 C#2 Bb1 A1 G1 F1 E1 D1  |
      | E2           | E2 D#2 C2 B1 A1 G1 F#1 E1  |
      | F2           | F2 E2 Db2 C2 Bb1 Ab1 G1 F1 |
      | G2           | G2 F#2 Eb2 D2 C2 Bb1 A1 G1 |
      | A2           | A2 G#2 F2 E2 D2 C2 B1 A1   |
      | B2           | B2 A#2 G2 F#2 E2 D2 C#2 B1 |