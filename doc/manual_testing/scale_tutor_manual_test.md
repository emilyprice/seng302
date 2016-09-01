#Manual Testing for Scale Recognition Tutor

Listed are all of the manual tests that were carried out on the application, flagged with either
pass or fail

##Test 1: Tutor correctly creates the specified number of questions
PASS
How the test passed:
1) The 'number of questions' indicator follows the slider, not allowing a selection of less than 1
 or more than 50
2) The tutor creates the correct number of question panes upon the go button being pushed
3) Quantities 1, 5, 10, 25 were generated and counted manually
4) The same quantities were also tested after previous tests had been partially completed, retested
 and cleared.

##Test 2: Tutor will only display questions that were within the specified conditions
PASS
How the test passed:
1) Both scales up and scales down were tested 5 times as well as scales up and down 5 times, each
 question set only generating scales that were specified.
2) Scales from one octave yield eight notes played (one scale, C - C) while scales from two octaves
 yield fifteen notes played, scales from three octaves yield 22 notes played et cetera.

##Test 3: Tutor plays a scale correctly
PASS
How the test passed:
1) A guitar tuner is used to verify that both major and minor scales are played in order with the
 correct notes.
2) Scales played up and down on multiple octaves are tested.

##Test 4: Tutor allows the user to select an answer and marks accordingly
PASS
How the test passed:
1) Drop down menus allow the user to select answer major and minor
2) Depending on the result of the users answer, the question pane border changes color and the drop
 down menu and skip button no longer allow for selection
3) Tutor displays correct answer if the user gets the question wrong

##Test 5: Tutor provides accurate feedback for the user when all questions are answered/skipped
PASS
How the test passed:
1) Once all questions are answered, the tutor displays a feedback page telling the user they have
 finished question set and the relevant information about how they did.
2) The tutor correctly displays how many questions were answered, skipped, correctly answered and
 incorrectly answered as well as provide a percentage of how many answers were correct.

##Test 6: Tutor retests the user on incorrectly answered questions
PASS
How the test passed:
1) After answering 5 questions, two correctly, I pressed the retest button and only 3 questions were
 displayed to me (these being the incorrectly identified scales).
2) Repeated this with 6 questions, correctly answering one question and retesting until all
questions were correctly answered.

##Test 7: Tutor clears properly after a question set is finished with
PASS
How the test passed:
1) I used the clear button after skipping all five questions in a set, after answering questions a
 mix of correctly and incorrectly, after answering all five questions correct and all five incorrect
2) In all of these cases, the question set was cleared of remaining questions and the sequential
 tests were unaffected.

##Test 8: "Harmonic Minor" scales are available to be included in the tutoring session
PASS
How the test passed:
1) I opened a new tutoring session, and expanded the scale type combo box
2) I selected "Harmonic Minor" and deselected all other choices
3) Upon clicking Go, each question presented was a scale of the type Harmonic Minor.