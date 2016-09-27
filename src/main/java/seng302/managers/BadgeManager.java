package seng302.managers;

import org.controlsfx.control.Notifications;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
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
    private static HashMap<String, String> tutorImages;
    private static HashMap<String, Boolean> masterTutors;
    private Environment env;

    private Set<Integer> instrumentsPlayed;


    public BadgeManager(Environment env) {
        this.env = env;
        instrumentsPlayed = new HashSet<>();
        populateTutorList();
        populate100AllTutorMap();
        populateImageMap();
        initalizeAllBadges();
        populateMasterMap();
    }

    /**
     * Used for updating all badges after changes to their states have been made
     *
     * @param tutorBadgeMap hashmap containing all tutor related badges
     * @param overallBadges hashmap containing all other badges
     */
    public void replaceBadges(HashMap<String, ArrayList<Badge>> tutorBadgeMap, ArrayList<Badge> overallBadges) {
        this.tutorBadgeMap = tutorBadgeMap;
        this.overallBadges = overallBadges;
    }

    /**
     * Used for updating 100% tutor session badges
     *
     * @param map the hashmap of 100% tutor session badges
     */
    public void replaceTutor100AllMap(HashMap<String, Boolean> map) {
        tutor100AllMap = map;
    }

    /**
     * Used to populate the list of tutors used to create all badges
     */
    private void populateTutorList() {
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

    /**
     * Used to populate the list of images for individual tutor badges
     */
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

    /**
     * Used to initialise the hashmap that stores whether tutor have been "mastered" (for the Tutor
     * Master badge)
     */
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

    /**
     * Used to populate the hashmap that stores the whether each tutor has been achieved with 100%
     */
    private void populate100AllTutorMap() {
        tutor100AllMap = new HashMap<>();
        for (String tutor : allTutors) {
            tutor100AllMap.put(tutor, false);
        }
    }


    /**
     * Used to initialise all badges
     */
    private void initalizeAllBadges() {
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

        ArrayList<Integer> musicianBadges = new ArrayList<>();
        musicianBadges.add(1);
        musicianBadges.add(3);
        musicianBadges.add(10);

        ArrayList<Integer> tutorTotal = new ArrayList<>();
        tutorTotal.add(10);

        //intalize tutor badges
        for (String tutor : allTutors) {
            ArrayList<Badge> badges = new ArrayList<>();
            badges.add(new Badge("Correct Questions", tutor, "Number of questions correctly answered", questionBadges, 0, 0, tutorImages.get(tutor), 10));
            badges.add(new Badge("Completed Sessions", tutor, "Number of tutor sessions completed", sessionBadges, 0, 0, tutorImages.get(tutor), 10));
            badges.add(new Badge("100% Sessions", tutor, "Number of 100% tutor sessions", sessionBadges, 0, 0, tutorImages.get(tutor), 15));

            if (tutor.equals("Musical Terms Tutor")) {
                badges.add(new Badge("Articulate", tutor, "Number of musical terms added", sessionBadges, 0, 0, "open-book", 10));
            }
            tutorBadgeMap.put(tutor, badges);
        }

        //initialise overall badges
        overallBadges.add(new Badge("Completist", null, "Unlock all tutors", tutorTotal, 2, 0, "completist", 400));
        overallBadges.add(new Badge("Tutor master", null, "100% in all tutors", tutorTotal, 0, 0, "gradHat", 350));
        overallBadges.add(new Badge("Instrument master", null, "Use different instruments", musicianBadges, 0, 0, "saxophone-man", 20));
        overallBadges.add(new Badge("Creative", null, "Change your theme", null, 0, 0, "paint-brush", 20));
        overallBadges.add(new Badge("Speedster", null, "Force set the tempo", null, 0, 0, "speedster", 20));
        overallBadges.add(new Badge("TwinkleTwinkle", null, "Find hidden secret", null, 0, 0, "twinkle", 20));

        replaceBadges(tutorBadgeMap, overallBadges);
    }


    /**
     * Used once an instrument has been set to update the list of used instruments and if a new
     * instrument has been played then it updates the " musician badge
     *
     * @param id instrument id
     */
    public void updateInstrument(Integer id) {
        Integer tempSize = instrumentsPlayed.size();
        instrumentsPlayed.add(id);
        if (instrumentsPlayed.size() > tempSize) {
            Badge instrumentBadge = overallBadges.get(2);
            instrumentBadge.updateBadgeProgress(env, 1);
        }

    }

    /**
     * Used to update the states of the non-tutor badges
     */
    public void updateOverallBadges() {
        if (!tutor100AllMap.containsValue(false)) {
            overallBadges.get(1).updateBadgeProgress(env, 1);
        }

    }

    /**
     * Used to update the state of a badge once its unlock criteria has been met
     *
     * @param badgeName the name of the badge to be unlocked
     */
    public void unlockBadge(String badgeName) {
        overallBadges.stream().filter(b -> b.name.equals(badgeName)).forEach(b -> {
            if (b.currentBadgeType == 0) {
                b.currentBadgeType = 1;
                b.badgeProgress = 1;
                updateOverallBadges();
                notification(badgeName);
                env.getUserHandler().getCurrentUser().getProjectHandler().getCurrentProject().addExperience(b.expWorth);
            }
        });
        reloadSummaryPage();
    }

    /**
     * Used to create a notification when a non-tutor badge has been unlocked or levelled up
     *
     * @param badgeName the name of the badge to be updated
     */
    private void notification(String badgeName) {
        Image unlock = new Image(getClass().getResourceAsStream("/images/unlock.png"), 75, 75, true, true);
        Notifications.create()
                .title("Achievement Unlocked")
                .text("Well done! \nYou have unlocked " + badgeName)
                .hideAfter(new Duration(10000))
                .graphic(new ImageView(unlock))
                .show();
    }

    /**
     * Reloads the summary page after a badge has been unlocked
     */
    private void reloadSummaryPage() {
        env.getUserPageController().showSummaryPage();
    }

    /**
     * Updates the badges that belong to a specific tutor
     *
     * @param tutorName the tutor that badges belong to
     * @param correct   the number of questions answered correctly
     * @param answered  the number of questions answered
     */
    public void updateTutorBadges(String tutorName, int correct, int answered) {
        ArrayList badges = tutorBadgeMap.get(tutorName);
        for (Object badg : badges) {
            Badge b = (Badge) badg;
            if (b.name.equals("Correct Questions")) {
                b.updateBadgeProgress(env, correct);
            } else if (b.name.equals("Completed Sessions")) {
                b.updateBadgeProgress(env, 1);
            } else if (b.name.equals("100% Sessions") && (correct == answered)) {
                b.updateBadgeProgress(env, 1);
            }
        }
    }

    /**
     * Used to update the Tutor Master badge after a tutor session is completed with 100%
     */
    public void updateTutorMaster(String tutorName) {
        if (masterTutors.get(tutorName) == false) {
            masterTutors.put(tutorName, true);
            overallBadges.get(1).updateBadgeProgress(env, 1);
        }
    }

    public Badge getBadge(String badgeName) {
        for (Badge b : overallBadges) {
            if (b.name.equals(badgeName)) {
                return b;
            }
        }
        return null;
    }

    public Badge getBadge(String badgeName, String tutorName) {
        for (Badge b : tutorBadgeMap.get(tutorName)) {
            if (b.name.equals(badgeName)) {
                return b;
            }
        }
        return null;
    }

    public static ArrayList<Badge> getOverallBadges() {
        return overallBadges;
    }

    public static HashMap getTutorBadges() {
        return tutorBadgeMap;
    }

    public static HashMap get100TutorBadges() {
        return tutor100AllMap;
    }
}
