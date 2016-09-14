package seng302.managers;

import java.util.ArrayList;
import java.util.Date;

import javafx.util.Pair;

/**
 * A class used for managing tutoring sessions. Stores the user's progress.
 */
public class TutorManager {


    /**
     * A list of pairs that stores the temporary incorrect responses
     */
    private ArrayList<Pair> tempIncorrectResponses = new ArrayList<Pair>();


    /**
     * A list of pairs that stores all the correct responses
     */
    private ArrayList<Pair> correctResponses = new ArrayList<Pair>();


    /**
     * A list of pairs that stores all the incorrect responses that haven't been re-attempted
     */
    private ArrayList<Pair> incorrectResponses = new ArrayList<Pair>();


    /**
     * Stores the number of questions that are currently being answered
     */
    public int questions = 0;

    /**
     * Stores the number of questions answered in the current set
     */
    public int answered = 0;

    /**
     * Stores the number of questions that have been answered correctly
     */
    public int correct = 0;

    /**
     * Stores the number of questions that have been skipped
     */
    public int skipped = 0;

    /**
     * Stores the number of questions that have been answered incorrectly
     */
    public int incorrect = 0;

    /**
     * Stores the date and time the tutor session was initiated
     */
    public Date dateAndTime = new Date();


    /**
     * Add note pair to to the corresponding array
     *
     * @param pair    pair of objects to store - note+note, interval+note, etc
     * @param outcome value that represents if the question was answered correctly
     */
    public void add(Pair pair, int outcome) {
        answered += 1;
        if (outcome == 0) {
            // Wrong answer
            incorrect += 1;
            tempIncorrectResponses.add(pair);
        }
        if (outcome == 1) {
            // Right answer
            correct += 1;
            correctResponses.add(pair);
        }
        if (outcome == 2) {
            // Skipped question
            skipped += 1;
            tempIncorrectResponses.add(pair);
        }
    }


    /**
     * Saves the temporary incorrect list to the permanent incorrect list.
     */
    public void saveTempIncorrect() {
        for (Pair pair : tempIncorrectResponses) {
            incorrectResponses.add(pair);
        }
        tempIncorrectResponses.clear();
    }


    /**
     * Clears the temporary incorrect list
     */
    public void clearTempIncorrect() {
        tempIncorrectResponses.clear();
    }


    /**
     * Gets The temporary incorrect responses
     *
     * @return the temporary incorrect responses
     */
    public ArrayList<Pair> getTempIncorrectResponses() {
        return tempIncorrectResponses;
    }

    /**
     * Calculates a user's score after a tutoring session
     *
     * @return the user's score as a percentage value
     */
    public float getScore() {
        float score = 0;
        if (answered > 0) {
            score = (float) correct / (float) answered * 100;
        }
        return score;
    }

    /**
     * Clears the variables that are used to track user's scores. This allows for re-testing without
     * holding onto the previous info.
     */
    public void resetStats() {
        answered = 0;
        correct = 0;
        skipped = 0;
        incorrect = 0;
    }

    public void resetEverything() {
        resetStats();
        tempIncorrectResponses.clear();
        correctResponses.clear();
        incorrectResponses.clear();
    }

    /**
     * Getter method for all correct responses
     *
     * @return the list of correctly answered questions
     */
    public ArrayList<Pair> getCorrectResponses() {
        return correctResponses;
    }

    /**
     * Getter method for all incorrect responses
     *
     * @return the list of incorrectly answered questions
     */
    public ArrayList<Pair> getIncorrectResponses() {
        return incorrectResponses;
    }
}
