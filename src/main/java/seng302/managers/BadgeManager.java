package seng302.managers;

import seng302.Environment;
import seng302.data.Badge;

import java.util.*;

/**
 * Created by jmw280 on 24/08/16.
 */
public class BadgeManager {

    private static ArrayList<Badge> overallBadges;
    private HashMap<String, ArrayList<Badge>> tutorBadgeMap;
    private HashMap<String, Boolean> tutor100AllMap;
    static private ArrayList<String> allTutors = new ArrayList<>();
    private Environment env;

    private Set<Integer> instrumentsPlayed;



    public BadgeManager(Environment env){
        this.env = env;
        instrumentsPlayed = new HashSet<>();
        populateTutorList();
        populate100AllTutorMap();
        initalizeAllBadges();
    }

    public void replaceBadges(HashMap<String, ArrayList<Badge>> tutorBadgeMap, ArrayList<Badge> overallBadges){

        this.tutorBadgeMap = tutorBadgeMap;
        this.overallBadges = overallBadges;
    }

    public void replaceTutor100AllMap(HashMap<String, Boolean> map){
        tutor100AllMap = map;
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
        tutor100AllMap = new HashMap<>();
        for (String tutor : allTutors){
            tutor100AllMap.put(tutor, false);
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
            badges.add(new Badge("Correct Questions", "number of questions correctly answered", questionBadges, 0, 0));
            badges.add(new Badge("100% sessions", "number of 100% tutor sessions", sessionBadges, 0, 0));
            badges.add(new Badge("completed sessions", "number of 100% tutor sessions", sessionBadges, 0, 0));

            if( tutor.equals("musicalTermTutor")){
                badges.add(new Badge("terms added", "number of musical terms added", sessionBadges, 0, 0));
            }
            tutorBadgeMap.put(tutor, badges);
        }

        //initalize overall badges
        overallBadges.add(new Badge("All tutors", "Unlocked all tutors", null, 0, 0));
        overallBadges.add(new Badge("All tutors master", "100% in all tutors", null, 0, 0));
        overallBadges.add(new Badge("Musician","number of instruments used", sessionBadges, 0, 0));
        overallBadges.add(new Badge("Speedster", "successfully force set the tempo", null, 0, 0));
        overallBadges.add(new Badge("TwinkleTwinkle", "find hidden secret", null, 0, 0));


    }


    public void updateAllBadges(String tutorType, Integer userScore){
        //updat overall badges
        //update tutor badges
        tutorBadgeMap.get(tutorType).get(0).updateBadgeProgress(userScore);
        tutorBadgeMap.get(tutorType).get(1).updateBadgeProgress(1);
        if(userScore == 10) {
            tutor100AllMap.put(tutorType, true);
            tutorBadgeMap.get(tutorType).get(2).updateBadgeProgress(1);
        }

    }

    /**
     * used once an instrument has been set to update the list of used instruments and if a new instrument has been
     * played then it updates the " musician badge
     * @param id instrument id
     */
    public void updateInstrument(Integer id){
        Integer tempSize = instrumentsPlayed.size();
        instrumentsPlayed.add(id);
        if(instrumentsPlayed.size() > tempSize){
            Badge instrumentBadge = overallBadges.get(2);
            instrumentBadge.updateBadgeProgress(1);
        }

    }

    public void updateOverallBadges(){
//        if(env.getUserHandler().getCurrentUser().getProjectHandler().getCurrentProject().getModeManager().currentUnlocks == 11) {
//            overallBadges.get(0).updateBadgeProgress(1);
//        }

        if(!tutor100AllMap.containsValue(false)){
            overallBadges.get(1).updateBadgeProgress(1);
        }

        //if(env.getMttDataManager().getTerms().size())

    }


    public static ArrayList<Badge> getOverallBadges(){
        return overallBadges;
    }

    public HashMap getTutorBadges(){
        return tutorBadgeMap;
    }

    public HashMap get100TutorBadges(){
        return tutor100AllMap;
    }

}
