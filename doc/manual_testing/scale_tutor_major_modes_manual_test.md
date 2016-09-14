#Manual Testing for Major Modes functionality for Scale Recognition Tutor

Listed are all of the manual tests that were carried out on the application, flagged with either
pass or fail

##Test 1: Tutor correctly generates major mode questions
PASS
How the test passed:
1) The "Scale type" checkcombobox was set to minor scales and major modes
2) The tutor creates the correct number of question panes upon the go button being pushed
3) Questions were answered and as expected some were minor scales and some were major modes
4) Test was repeated having selected major scales and major mode scales with the result. Major
    modes alone was also selected, resulting in a tutor session with nothing but major mode
    questions being generated.

##Test 2: Tutor will correctly generate major modes to display
PASS
How the test passed:
1) A tutor session only generating major mode scales was started (with 5 questions)
2) Major modes were confirmed to be generated correctly using print statements that showed what
    the notes in the scale played were.
3) This was repeated with the correct notes being displayed 5 times. Three tutor sessions were
    tested

##Test 3: Tutor plays major mode scales correctly
PASS
How the test passed:
1) A tutor session only generating major mode scales was started
2) The tempo was lowered to 40bpm and a guitar tuner was used to listen to the notes played in
    major modes
3) The notes picked up by the tuner were correct in that together they made up the correct major
    mode
