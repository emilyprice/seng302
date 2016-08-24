package seng302.managers;

import javafx.util.Pair;
import seng302.data.Badge;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by jmw280 on 24/08/16.
 */
public class BadgeManager {


    private ArrayList<Badge> overallBadges;


    private ArrayList<Pair<String, String>> tutorBadgeData;

    private HashMap<String, ArrayList<Badge>> tutorBadgeMap;


    static private ArrayList<String> allTutors = new ArrayList<>();




    public BadgeManager(){
        populateTutorList();
        createTutorBadgeData();
        initalizeAllBadges();

    }

    private void populateTutorList(){
        allTutors.add("pitchTutor");
        allTutors.add("intervalTutor");
        allTutors.add("musicalTermTutor");
        allTutors.add("scaleTutor");
        allTutors.add("chordTutor");
        allTutors.add("chordSpellingTutor");
        allTutors.add("diatonicChordTutor");
        allTutors.add("keySignatureTutor");
    }

    private void createTutorBadgeData(){
        tutorBadgeData.add(new Pair<>("Correct Questions", "number of questions correctly answered"));
        tutorBadgeData.add(new Pair<>("100% sessions", "number of 100% tutor sessions"));
        tutorBadgeData.add(new Pair<>("completed sessions", "number of 100% tutor sessions"));
    }

    private void overallBadgeData(){
        tutorBadgeData.add(new Pair<>("All tutors", "Unlocked all tutors "));

    }




    private void initalizeAllBadges(){
        overallBadges = new ArrayList<>();
        tutorBadgeMap = new HashMap<>();
        for (String tutor:allTutors){
            ArrayList<Badge> badges = new ArrayList<>();
            badges.add(new Badge("Correct Questions", "number of questions correctly answered"));
            badges.add(new Badge("100% sessions", "number of 100% tutor sessions"));
            badges.add(new Badge("completed sessions", "number of 100% tutor sessions"));
            tutorBadgeMap.put(tutor, badges);
        }
    }


    public void updateAllBadges(String tutorType){


        //updat overall badges
        //update tutor badges
//        tutorBadgeMap.get(tutorType).get(0).updateBadgeProgress(tutorrecord);
//        tutorBadgeMap.get(tutorType).get(1).updateBadgeProgress(tutorrecord);
//        tutorBadgeMap.get(tutorType).get(2).updateBadgeProgress(tutorrecord);

    }
}
