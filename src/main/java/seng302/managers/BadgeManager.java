package seng302.managers;

import org.controlsfx.control.Notifications;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import seng302.Environment;
import seng302.data.Badge;

import java.io.IOException;
import java.util.*;

/**
 * Created by jmw280 on 24/08/16.
 */
public class BadgeManager {

    private static ArrayList<Badge> overallBadges;
    private static HashMap<String, ArrayList<Badge>> tutorBadgeMap;
    private static HashMap<String, Boolean> tutor100AllMap;
    static private ArrayList<String> allTutors = new ArrayList<>();
    private static HashMap<String, String> tutorImages;
    private static HashMap<String, Boolean> masterTutors;
    private Environment env;

    private Set<Integer> instrumentsPlayed;



    public BadgeManager(Environment env){
        this.env = env;
        instrumentsPlayed = new HashSet<>();
        populateTutorList();
        populate100AllTutorMap();
        populateImageMap();
        initalizeAllBadges();
        populateMasterMap();
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

    private void populateImageMap() {
        tutorImages = new HashMap<>();
        tutorImages.put("Pitch Comparison Tutor", "tuning-fork");
        tutorImages.put("Interval Recognition Tutor", "rest-note");
        tutorImages.put("Musical Terms Tutor", "open-book");
        tutorImages.put("Scale Recognition Tutor", "scale");
        tutorImages.put("Chord Recognition Tutor", "chord");
        tutorImages.put("Chord Spelling Tutor", "chord-spelling");
        tutorImages.put("Diatonic Chord Tutor", "sound-wave");
        tutorImages.put("Key Signature Tutor", "key-sig");
        tutorImages.put("Scale Modes Tutor", "sound-level");
    }

    private void populateMasterMap() {
        masterTutors = new HashMap<>();
        masterTutors.put("Pitch Comparison Tutor", false);
        masterTutors.put("Interval Recognition Tutor", false);
        masterTutors.put("Musical Terms Tutor", false);
        masterTutors.put("Scale Recognition Tutor", false);
        masterTutors.put("Chord Recognition Tutor", false);
        masterTutors.put("Chord Spelling Tutor", false);
        masterTutors.put("Diatonic Chord Tutor", false);
        masterTutors.put("Key Signature Tutor", false);
        masterTutors.put("Scale Modes Tutor", false);
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

        ArrayList<Integer> tutorTotal = new ArrayList<>();
        tutorTotal.add(10);

        //intalize tutor badges
        for (String tutor:allTutors){
            ArrayList<Badge> badges = new ArrayList<>();
            badges.add(new Badge("Correct Questions", tutor, "Number of questions correctly answered", questionBadges, 0, 0, tutorImages.get(tutor)));
            badges.add(new Badge("Completed Sessions", tutor, "Number of tutor sessions completed", sessionBadges, 0, 0, tutorImages.get(tutor)));
            badges.add(new Badge("100% Sessions", tutor, "Number of 100% tutor sessions", sessionBadges, 0, 0, tutorImages.get(tutor)));

            if( tutor.equals("Musical Terms Tutor")){
                badges.add(new Badge("Articulate", tutor, "Number of musical terms added", sessionBadges, 0, 0, "open-book"));
            }
            tutorBadgeMap.put(tutor, badges);
        }

        //initialise overall badges
        overallBadges.add(new Badge("Completist", null, "Unlock all tutors", tutorTotal, 0, 0, "gradHat"));
        overallBadges.add(new Badge("Tutor master", null, "100% in all tutors", tutorTotal, 0, 0, "gradHat"));
        overallBadges.add(new Badge("Musician", null, "Number of instruments used", sessionBadges, 0, 0, "gradHat"));
        overallBadges.add(new Badge("Eggs in baskets", null, "Create three projects", null, 0, 0, "gradHat"));
        overallBadges.add(new Badge("Speedster", null, "Force set the tempo", null, 0, 0, "speedster"));
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
//        env.getRootController().getStage().getScene().setRoot(FXMLLoader.load());
        overallBadges.stream().filter(b -> b.name.equals(badgeName)).forEach(b -> {
            b.currentBadgeType = 1;
            b.badgeProgress = 1;
            updateOverallBadges();
            notification(badgeName, b.currentBadgeType, b.tutorName);
        });
        reloadPage();
    }

    private void notification(String badgeName, int currentBadgeType, String tutorName) {
        Image unlock = new Image(getClass().getResourceAsStream("/images/unlock.png"), 75, 75, true, true);
        Notifications.create()
                .title("Achievement Unlocked")
                .text("Well done! \nYou have unlocked " + badgeName)
                .hideAfter(new Duration(10000))
                .graphic(new ImageView(unlock))
                .show();
    }

    private void reloadPage() {
//        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/Views/newGui.fxml"));
//        Parent root = null;
//        try {
//            root = fxmlLoader.load();
//        } catch (IOException e) {
//            System.out.println("Failed to reload page");
//            e.printStackTrace();
//        }
//        env.getRootController().getStage().getScene().
    }

    public void updateTutorBadges(String tutorName, int correct, int answered) {
        ArrayList badges = tutorBadgeMap.get(tutorName);
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

    public void updateTutorMaster(String tutorName) {
        if (masterTutors.get(tutorName) == false) {
            masterTutors.put(tutorName, true);
            overallBadges.get(1).updateBadgeProgress(1);
        }
    }

    public Badge getBadge(String badgeName) {
        for (Badge b : overallBadges) {
            System.out.println(b.name);
            if (b.name.equals(badgeName)) {
                return b;
            }
        }
        return null;
    }

    public Badge getBadge(String badgeName, String tutorName) {
        for (Badge b : tutorBadgeMap.get(tutorName)) {
            System.out.println(b.name);
            if (b.name.equals(badgeName)) {
                return b;
            }
        }
        return null;
    }

}
