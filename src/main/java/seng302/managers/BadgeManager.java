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
    private HashMap<String, ArrayList<Badge>> tutorBadgeMap;
    private HashMap<String, Boolean> tutor100AllMAp;
    static private ArrayList<String> allTutors = new ArrayList<>();
    private Environment env;


    public BadgeManager(Environment env){
        this.env = env;
        populateTutorList();
        populate100AllTutorMap();
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


    private void populate100AllTutorMap(){
        tutor100AllMAp = new HashMap<>();
        for (String tutor : allTutors){
            tutor100AllMAp.put(tutor, false);
        }
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

        //intalize tutor badges
        for (String tutor:allTutors){
            ArrayList<Badge> badges = new ArrayList<>();
            badges.add(new Badge("Correct Questions", "number of questions correctly answered", questionBadges));
            badges.add(new Badge("100% sessions", "number of 100% tutor sessions", sessionBadges));
            badges.add(new Badge("completed sessions", "number of 100% tutor sessions", sessionBadges));

            if( tutor.equals("musicalTermTutor")){
                badges.add(new Badge("terms added", "number of musical terms added", sessionBadges));
            }
            tutorBadgeMap.put(tutor, badges);
        }

        //initalize overall badges
        overallBadges.add(new Badge("All tutors", "Unlocked all tutors", null));
        overallBadges.add(new Badge("All tutors master", "100% in all tutors", null));
        overallBadges.add(new Badge("Musician","number of instruments used", sessionBadges));
        overallBadges.add(new Badge("Speedster", "successfully force set the tempo", null));
        overallBadges.add(new Badge("TwinkleTwinkle", "find hidden secret", null));


    }


    public void updateAllBadges(String tutorType, Integer userScore){
        //updat overall badges
        //update tutor badges
        tutorBadgeMap.get(tutorType).get(0).updateBadgeProgress(userScore);
        tutorBadgeMap.get(tutorType).get(1).updateBadgeProgress(1);
        if(userScore == 10) {
            tutor100AllMAp.put(tutorType, true);
            tutorBadgeMap.get(tutorType).get(2).updateBadgeProgress(1);
        }

    }


    public void updateOverallBadges(){
//        if(env.getUserHandler().getCurrentUser().getProjectHandler().getCurrentProject().getModeManager().currentUnlocks == 11) {
//            overallBadges.get(0).updateBadgeProgress(1);
//        }

        if(!tutor100AllMAp.containsValue(false)){
            overallBadges.get(1).updateBadgeProgress(1);
        }

        //if(env.getMttDataManager().getTerms().size())

    }



}
