#Manual Testing for the Microphone Input Story.

Listed are all of the manual tests that were carried out on the application, flagged with either
pass or fail

##Test 1: Tutor will only display questions that are in the selected range
The slider restricts the user to select between octaves C-1 and G9
This test will be run 5 times.
This test was run with the following octave ranges:
    C1 - C3, B4 - B6, F3 - B4, D5 - A6, F3 - A6
Procedure:
1) I selected an octave range from the above.
2) I pressed the 'Go' button.
3) All questions displayed only asked for questions in the given octave range.
Result: PASS

##Test 2: Changing the Volume threshold
Conditions for pass:
Changing the volume threshold in the microphone settings page changes the maximum range of sounds that the
 microphone can pick up. Changing this to zero should result in no sound being detected, and reducing the threshold
 should result in more sounds being detected.

Procedure:
1) Open the microphone settings page.
2) Select a valid microphone input.
3) Set the threshold slider to 0.
4) Open the microphone popup and click record.
5) Play sounds loudly by microphone input device. For this step, I set my laptop speakers to maximum volume
    and played middle C on the built in keyboard.
6) Press stop recording and observe changes in list of recorded notes. (At threshold == 0, no notes should be detected.)
7) Close popup
8) Repeat steps 4 -> 7, decreasing threshold by 10 each time. Repeat this until threshold = -100.
9) If at this point all played notes are detected, as well as occasional background noise
    (speaking while playing should make notes either undetectable or recognise voice as notes),
    test is deemed to have passed.
Result: PASS

##Test3: The Microphone input tutor marks questions correctly
Conditions for pass:
When entering using the built in keyboard to answer a question in the microphone input tutor,
    the answer is marked as correct (Assuming that the note played is the one requested.). This should be that case
    because the note synthesised by the program is the theoretical perfectly tuned frequency of the note.
    This test was run using the Grand Piano instrument
Procedure:
1) Open the Microphone input tutor and the on screen piano.
2) In practice mode, with the parameters of note range (C3-C5) and number of questions set to 10,
    click go on the microphone  input tutor.
3) For quetions 1 through 10, repeat the following steps:
4) Click the record button for the question.
5) With volume set to maximum, click on the corresponding note on the on-screen keyboard.
6) Click the stop button in the question.
7) Verify that the recognised note matches the expected note. The question accordian should highlight green.
Result: PASS