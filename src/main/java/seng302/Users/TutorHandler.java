package seng302.Users;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.util.Pair;
import seng302.Environment;
import seng302.utility.TutorRecord;

/**
 * This class manages information relating to the display of tutor graphs.
 */
public class TutorHandler {
    Environment env;

    private Map<String, Date> dates;


    private static final List<String> tutorIds = new ArrayList<String>() {{
        add("pitchTutor");
        add("scaleTutor");
        add("intervalTutor");
        add("musicalTermsTutor");
        add("chordTutor");
        add("chordSpellingTutor");
        add("keySignatureTutor");
        add("diatonicChordTutor");
        add("scaleModesTutor");
        add("scaleSpellingTutor");
    }};

    /**
     * Constructor for creating a new tutor handler
     *
     * @param env The environment in which the tutorhandler is being created
     */
    public TutorHandler(Environment env) {
        this.env = env;
        dates = new HashMap<>();
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR_OF_DAY, -24);
        dates.put("Last 24 Hours", cal.getTime());
        cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -7);
        dates.put("Last Week", cal.getTime());
        cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -1);
        dates.put("Last Month", cal.getTime());
        cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -6);
        dates.put("Last Six Months", cal.getTime());
        cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, -1);
        dates.put("Last Year", cal.getTime());
        cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, -1000);
        dates.put("All Time", cal.getTime());

    }

    /**
     * This method will give the total number of correct and incorrect answers for a given tutor.
     *
     * @param tabId The tab ID of the tutor
     * @return a pair containing two integers. The number of answers correct and the number of
     * incorrect answers.
     */
    public Pair<Integer, Integer> getTutorTotals(String tabId, String timePeriod) {
        ArrayList<TutorRecord> records = getTutorData(tabId);
        Integer correct = 0;
        Integer incorrect = 0;
        for (TutorRecord record : records) {
            Date date = record.getDate();
            Date compare = dates.get(timePeriod);
            if (date.after(compare)) {
                Map<String, Number> stats = record.getStats();
                correct += stats.get("questionsCorrect").intValue();
                incorrect += stats.get("questionsIncorrect").intValue();
            }
        }
        return new Pair<>(correct, incorrect);
    }

    /**
     * This method will give the total number of correct and incorrect answers for a given tutor.
     *
     * @param tabId The tabid of the tutor
     * @return a pair containing two integers. The number of answers correct and the number of
     * incorrect answers.
     */
    public Pair<Integer, Integer> getRecentTutorTotals(String tabId) throws IndexOutOfBoundsException {
        ArrayList<TutorRecord> records = getTutorData(tabId);
        if (records.size() != 0) {
            TutorRecord lastRecord = records.get(records.size() - 1);
            Map<String, Number> stats = lastRecord.getStats();
            Integer correct = stats.get("questionsCorrect").intValue();
            Integer incorrect = stats.get("questionsIncorrect").intValue();
            return new Pair<>(correct, incorrect);
        } else {
            return new Pair<>(0, 0);
        }

    }


    /**
     * This method returns a collection of information about a specific tutor
     *
     * @param id The ID of the tutor to fetch information about
     * @return A collection of information about past tutoring sessions
     */
    public ArrayList<TutorRecord> getTutorData(String id) {
        while (env.getFirebase().getUserSnapshot() == null) {
            continue;
        }

        DataSnapshot tutorSnap = env.getFirebase().getUserSnapshot().child("projects/" +
                env.getUserHandler().getCurrentUser().getProjectHandler().getCurrentProject().projectName + "/" + id);

        tutorSnap.getChildren().forEach((key) -> {
                    TutorRecord record = new TutorRecord();
                    HashMap<String, Object> recordMap = (HashMap<String, Object>) key.getValue();
                    record.setDate((Date) recordMap.get("date"));
                    //record.setFinished();
                }
        );
        return new ArrayList<TutorRecord>();

    }

    /**
     * Used to get information about a specific tutor in a specific time period
     *
     * @param tabID      The tutor to fetch information about
     * @param timePeriod The time period from which we want to fetch tutor information
     * @return A collection of tutor scores and times
     */
    public List<Pair<Date, Float>> getTimeAndScores(String tabID, String timePeriod) {
        ArrayList<TutorRecord> records = getTutorData(tabID);
        List<Pair<Date, Float>> scores = new ArrayList<>();
        for (TutorRecord record : records) {
            Date date = record.getDate();
            Date compare = dates.get(timePeriod);
            if (date.after(compare)) {
                Map<String, Number> scoreMap = record.getStats();
                float score = scoreMap.get("percentageCorrect").floatValue();
                scores.add(new Pair<>(date, score));
            }
        }
        return scores;
    }

    /**
     * Return the total number of questions answered correctly or incorrectly in all tutors.
     *
     * @return Pair consisting of total correct and total incorrect.
     */
    public Pair<Integer, Integer> getTotalsForAllTutors(String timePeriod) {
        Integer totalCorrect = 0;
        Integer totalIncorrect = 0;
        for (String tutor : tutorIds) {
            Pair<Integer, Integer> total = getTutorTotals(tutor, timePeriod);
            totalCorrect += total.getKey();
            totalIncorrect += total.getValue();
        }
        return new Pair<>(totalCorrect, totalIncorrect);
    }


    /**
     * Saves the tutor records to disc.
     */
    public void saveTutorRecordsToFile(String filename, TutorRecord currentRecord) {
        DatabaseReference ref = env.getFirebase().getUserRef().child("projects/" +
                env.getUserHandler().getCurrentUser().getProjectHandler().getCurrentProject().projectName + "/" + filename);
        currentRecord.updateDate();
        ref.push().setValue(currentRecord);
    }


}
