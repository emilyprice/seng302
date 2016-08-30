package seng302.Users;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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
 * Created by jmw280 on 25/07/16.
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
    }};

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
        System.out.println(dates);

    }

    /**
     * This method will give the total number of correct and incorrect answers for a given tutor.
     *
     * @param tabId The tabid of the tutor
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
     * This method will give the number of correct and incorrect answers for a given tutor for it's
     * most recent attempt.
     *
     * @param tabId The tabid of the tutor
     * @return a pair containing two integers. The number of answers correct and the number of
     * incorrect answers.
     */
    public Pair<Integer, Integer> getRecentTutorTotals(String tabId) {
        ArrayList<TutorRecord> records = getTutorData(tabId);
        TutorRecord lastRecord = records.get(records.size() - 1);
        Map<String, Number> stats = lastRecord.getStats();
        Integer correct = stats.get("questionsCorrect").intValue();
        Integer incorrect = stats.get("questionsIncorrect").intValue();
        return new Pair<>(correct, incorrect);
    }


    public ArrayList<TutorRecord> getTutorData(String id) {
        String projectAddress = env.getUserHandler().getCurrentUser().getProjectHandler().getCurrentProject().currentProjectPath;
        String filename = "";
        if (id.equals("pitchTutor")) {
            filename = projectAddress + "/PitchComparisonTutor.json";
        } else if (id.equals("scaleTutor")) {
            filename = projectAddress + "/ScaleRecognitionTutor.json";
        } else if (id.equals("intervalTutor")) {
            filename = projectAddress + "/IntervalRecognitionTutor.json";
        } else if (id.equals("musicalTermTutor")) {
            filename = projectAddress + "/MusicalTermsTutor.json";
        } else if (id.equals("chordTutor")) {
            filename = projectAddress + "/ChordRecognitionTutor.json";
        } else if (id.equals("chordSpellingTutor")) {
            filename = projectAddress + "/ChordSpellingTutor.json";
        } else if (id.equals("keySignatureTutor")) {
            filename = projectAddress + "/KeySignatureTutor.json";
        } else if (id.equals("diatonicChordTutor")) {
            filename = projectAddress + "/DiatonicChordTutor.json";
        }
        Gson gson = new Gson();
        ArrayList<TutorRecord> records = new ArrayList<>();
        try {
            JsonReader jsonReader = new JsonReader(new FileReader(filename));
            records = gson.fromJson(jsonReader, new TypeToken<ArrayList<TutorRecord>>() {
            }.getType());

        } catch (FileNotFoundException e) {
            System.err.println("File not found.");
        } catch (JsonSyntaxException e) {
            System.err.println("File was not of the correct type.");
        }
        return records;

    }

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
     * When finished a tutor session. Or when save is clicked part way through.
     */
    public void saveTutorRecordsToFile(String projectAddress) {
        if (env.getRootController().tabSaveCheck("pitchTutor")) {
            saveTutorRecordsToFile(projectAddress + "/PitchComparisonTutor.json", env.getRootController().PitchComparisonTabController.record);
        }
        if (env.getRootController().tabSaveCheck("intervalTutor")) {

            saveTutorRecordsToFile(projectAddress + "/IntervalRecognitionTutor.json", env.getRootController().IntervalRecognitionTabController.record);
        }
        if (env.getRootController().tabSaveCheck("musicalTermTutor")) {
            saveTutorRecordsToFile(projectAddress + "/MusicalTermsTutor.json", env.getRootController().MusicalTermsTabController.record);
        }
        if (env.getRootController().tabSaveCheck("scaleTutor")) {
            saveTutorRecordsToFile(projectAddress + "/ScaleRecognitionTutor.json", env.getRootController().ScaleRecognitionTabController.record);
        }
        if (env.getRootController().tabSaveCheck("chordTutor")) {
            saveTutorRecordsToFile(projectAddress + "/ChordRecognitionTutor.json", env.getRootController().ChordRecognitionTabController.record);
        }
        if (env.getRootController().tabSaveCheck("chordSpellingTutor")) {
            saveTutorRecordsToFile(projectAddress + "/ChordSpellingTutor.json", env.getRootController().ChordSpellingTabController.record);
        }
        if (env.getRootController().tabSaveCheck("keySignatureTutor")) {
            saveTutorRecordsToFile(projectAddress + "/KeySignatureTutor.json", env.getRootController().KeySignaturesTabController.record);
        }
        if (env.getRootController().tabSaveCheck("diatonicChordTutor")) {
            saveTutorRecordsToFile(projectAddress + "/DiatonicChordTutor.json", env.getRootController().DiatonicChordsController.record);
        }
    }

    /**
     * Saves the tutor records to disc.
     */
    public void saveTutorRecordsToFile(String filename, TutorRecord currentRecord) {
        Gson gson = new Gson();
        try {
            ArrayList<TutorRecord> records;
            try {
                JsonReader jsonReader = new JsonReader(new FileReader(filename));
                records = gson.fromJson(jsonReader, new TypeToken<ArrayList<TutorRecord>>() {
                }.getType());

                TutorRecord latest = records.get(records.size() - 1);
                if (!latest.isFinished()) {
                    records.remove(records.size() - 1);
                }
            } catch (FileNotFoundException e) {
                records = new ArrayList<>();
            } catch (JsonSyntaxException e) {
                System.err.println("File was not of the correct type. Overwriting.");
                records = new ArrayList<>();
            }
            currentRecord.setDate();
            records.add(currentRecord);

            String json = gson.toJson(records);
            try {
                FileWriter writer = new FileWriter(filename, false);
                writer.write(json);
                writer.flush();
                writer.close();
            } catch (IOException ex) {
                System.err.println("Problem writing to the selected file " + ex.getMessage());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
