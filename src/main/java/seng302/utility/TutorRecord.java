package seng302.utility;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A record that holds the information of one tutoring session
 */
public class TutorRecord {

    private List<Map<String, String>> questions = new ArrayList();
    private Map<String, Number> stats = new HashMap<>();
    private Date date;
    private boolean finished = false;

    /**
     * Adds a question and answer to the record

     * @param questionSet A list of strings containing information about a single question.
     *                    The third string has one of the following values:
     *                    2 for skip, 1 for correct, 0 for incorrect
     *
     */
    public Map<String, String> addQuestionAnswer(String[] questionSet) {
        Map<String, String> question = new HashMap<>();
        question.put("question", questionSet[0]);
        question.put("answer", questionSet[1]);
        question.put("correct", questionSet[2]);
        questions.add(question);
        return question;
    }

    public List getQuestions() {
        return questions;
    }

    public Map<String, Number> getStats() {
        return stats;
    }

    public Date getDate() {
        return date;
    }

    /**
     * Sets the user's score, etc
     *
     * @param questionsAnsweredCorrectly   The number of questions the user answered correctly
     * @param questionsAnsweredIncorrectly The number of questions the user answered incorrectly
     */
    public Map<String, Number> setStats(int questionsAnsweredCorrectly, int questionsAnsweredIncorrectly, float score) {
        stats.put("questionsCorrect", questionsAnsweredCorrectly);
        stats.put("questionsIncorrect", questionsAnsweredIncorrectly);
        stats.put("percentageCorrect", score);
        return stats;
    }


    public void setDate() {
        date = new Date();
    }

    public void setFinished() {
        finished = true;
    }

    public boolean isFinished() {
        return finished;
    }


}
