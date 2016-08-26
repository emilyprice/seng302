package seng302.managers;

import javafx.util.Pair;
import seng302.Environment;
import seng302.data.Badge;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by jmw280 on 24/08/16.
 */
public class BadgeManager {


    private ArrayList<Badge> overallBadges;


    private ArrayList<Pair<String, String>> tutorBadgeData = new ArrayList<>();

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


        ArrayList<Integer> sessionBadges = new ArrayList<>();
        sessionBadges.add(0);
        sessionBadges.add(5);
        sessionBadges.add(15);
        sessionBadges.add(45);
        sessionBadges.add(100);

        ArrayList<Integer> questionBadges = new ArrayList<>();
        questionBadges.add(0);
        questionBadges.add(10);
        questionBadges.add(45);
        questionBadges.add(100);
        questionBadges.add(500);

        for (String tutor:allTutors){
            ArrayList<Badge> badges = new ArrayList<>();
            badges.add(new Badge("Correct Questions", "number of questions correctly answered", questionBadges));
            badges.add(new Badge("100% sessions", "number of 100% tutor sessions", sessionBadges));
            badges.add(new Badge("completed sessions", "number of 100% tutor sessions", sessionBadges));
            tutorBadgeMap.put(tutor, badges);
        }
    }


    public void updateAllBadges(String tutorType, Integer userScore){

        //updat overall badges
        //update tutor badges
        tutorBadgeMap.get(tutorType).get(0).updateBadgeProgress(userScore);
        tutorBadgeMap.get(tutorType).get(1).updateBadgeProgress(1);
        if(userScore == 10) {
            tutorBadgeMap.get(tutorType).get(2).updateBadgeProgress(1);
        }

    }
}
