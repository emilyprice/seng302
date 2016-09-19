package seng302.managers;

import seng302.Environment;
import seng302.data.Badge;

import java.util.*;

/**
 * Created by jmw280 on 24/08/16.
 */
public class BadgeManager {

    private static ArrayList<Badge> overallBadges;
    private static HashMap<String, ArrayList<Badge>> tutorBadgeMap;
    private static HashMap<String, Boolean> tutor100AllMap;
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
        allTutors.add("Pitch Comparison Tutor");
        allTutors.add("Interval Recognition Tutor");
        allTutors.add("Musical Terms Tutor");
        allTutors.add("Scale Recognition Tutor");
        allTutors.add("Chord Recognition Tutor");
        allTutors.add("Chord Spelling Tutor");
        allTutors.add("Diatonic Chord Tutor");
        allTutors.add("Key Signature Tutor");
        allTutors.add("Scale Modes Tutor");
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
//        sessionBadges.add(0);
        sessionBadges.add(1);
        sessionBadges.add(10);
        sessionBadges.add(25);
        sessionBadges.add(100);

        ArrayList<Integer> questionBadges = new ArrayList<>();
        questionBadges.add(1);
        questionBadges.add(10);
        questionBadges.add(50);
        questionBadges.add(120);
        questionBadges.add(600);

        //intalize tutor badges
        for (String tutor:allTutors){
            ArrayList<Badge> badges = new ArrayList<>();
            badges.add(new Badge("Correct Questions", tutor, "Number of questions correctly answered", questionBadges, 0, 0, "tuning-fork"));
            badges.add(new Badge("Completed Sessions", tutor, "Number of tutor sessions completed", sessionBadges, 0, 0, "tuning-fork"));
            badges.add(new Badge("100% Sessions", tutor, "Number of 100% tutor sessions", sessionBadges, 0, 0, "tuning-fork"));

            if( tutor.equals("Musical Terms Tutor")){
                badges.add(new Badge("terms added", tutor, "Number of musical terms added", sessionBadges, 0, 0, "open-book"));
            }
            tutorBadgeMap.put(tutor, badges);
        }

        //initialise overall badges
        overallBadges.add(new Badge("All tutors", null, "Unlock all tutors", null, 0, 0, "gradHat"));
        overallBadges.add(new Badge("Tutor master", null, "100% in all tutors", null, 0, 0, "gradHat"));
        overallBadges.add(new Badge("Musician", null, "Number of instruments used", sessionBadges, 0, 0, "gradHat"));
        overallBadges.add(new Badge("Eggs in baskets", null, "Create three projects", null, 0, 0, "gradHat"));
        overallBadges.add(new Badge("Speedster", null, "Force set the tempo", null, 0, 0, "gradHat"));
        overallBadges.add(new Badge("TwinkleTwinkle", null, "Find hidden secret", null, 0, 0, "twinkle"));

        replaceBadges(tutorBadgeMap, overallBadges);
    }


    public void updateAllBadges(String tutorType, Integer userScore){
        //update overall badges
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

    public static HashMap getTutorBadges(){
        return tutorBadgeMap;
    }

    public static HashMap get100TutorBadges(){
        return tutor100AllMap;
    }

    public void unlockBadge(String badgeName) {
        overallBadges.stream().filter(b -> b.name.equals(badgeName)).forEach(b -> {
            System.out.println("Unlocked badge woohoo");
            b.currentBadgeType = 1;
            b.badgeProgress = 1;
            updateOverallBadges();
        });
    }

    public void updateTutorBadges(String tutorName, int correct, int answered) {
        ArrayList badges = tutorBadgeMap.get(tutorName);
//        double progress = ((double)correct / (double) answered);
        for (Object badg : badges) {
            Badge b = (Badge) badg;
            if (b.name.equals("Correct Questions")) {
                b.updateBadgeProgress(correct);
            } else if (b.name.equals("Completed Sessions")) {
                b.updateBadgeProgress(1);
            } else if (b.name.equals("100% Sessions") && (correct == answered)) {
                b.updateBadgeProgress(1);
            }
        }
    }
}
