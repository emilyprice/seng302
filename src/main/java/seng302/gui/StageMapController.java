package seng302.gui;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import jdk.nashorn.internal.parser.JSONParser;
import seng302.Environment;
import javafx.scene.control.Button;
import seng302.Users.Project;
import seng302.Users.TutorHandler;
import seng302.utility.TutorRecord;


import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by svj13 on 2/09/16.
 */
public class StageMapController {

    public Project currentProject;


    @FXML
    private Button scaleRecognitionTutorButton;

    @FXML
    private Button keySignaturesTutorButton;

    @FXML
    private AnchorPane stageMap;

    @FXML
    private Button diatonicChordsTutorButton;

    @FXML
    private Button scaleRecognitionAdvancedTutorButton;

    @FXML
    private Button majorModesTutorButton;

    @FXML
    private Button musicalTermsTutorButton;

    @FXML
    private Button chordRecognitionTutorButton;

    @FXML
    private Button pitchTutorButton;

    @FXML
    private Button chordSpellingTutorButton;

    @FXML
    private Button scaleSpellingTutorButton;

    @FXML
    private Button chordRecognitionAdvancedTutorButton;

    @FXML
    private Button intervalRecognitionButton;

    private TutorHandler tutorHandler;


    public HashMap<String, Boolean> unlockStatus;

    private HashMap<String, Button> tutorAndButton; //associates tutor name with its corresponding button

    private ArrayList<String> tutorOrder; //the order in which the tutors unlock
    public HashMap<String, String> converted;



    UserPageController userPageController; //creates an insance of user page controller so we can access its methods

    Environment env; //declare the environment

    private JSONParser parser; //used to parser the tutor records


    /**
     * Initializes the environment and the user page controller instance
     *
     * @param env
     */
    public void setEnvironment(Environment env) {
        this.env = env;
        currentProject = env.getUserHandler().getCurrentUser().getProjectHandler().getCurrentProject();
        tutorHandler = currentProject.getTutorHandler();

        userPageController = env.getUserPageController();
    }


    public StageMapController() {

    }

    public void updateUnlock(){
        env.getStageMapController().getUnlockStatus();
    }

    public HashMap getUnlockStatus(){
        return this.unlockStatus;
    }

    /**
     *Initiates the required information of the stage map (which tutor is associated with which button,
     * the order of the tutors, and whether the tutor is unlocked or locked)
     */
    public void create() {
        tutorAndButton = new HashMap<>();
        tutorOrder = new ArrayList<>();
        unlockStatus = new HashMap<>();
        converted = new HashMap<>();

        generateLockingStatus();
        generateTutorOrder();
        generateTutorAndButtonNames();
        generateConverted();
        visualiseLockedTutors();
    }

    /**
     * Creates the converted strings
     *
     **/
    private void generateConverted(){
        converted.put("Musical Terms Tutor", "musicalTermsTutor");
        converted.put("Pitch Comparison Tutor", "pitchTutor" );
        converted.put("Scale Recognition Tutor","scaleTutor");
        converted.put("Chord Recognition Tutor", "chordTutor");
        converted.put("Interval Recognition Tutor", "intervalTutor");
        converted.put("Scale Recognition TutorA", "scaleTutorAdvanced");
        converted.put("Chord Recognition Tutor", "chordTutorAdvanced");
        converted.put("Chord Spelling Tutor", "chordSpellingTutor");
        converted.put("Scale Spelling Tutor", "scaleSpellingTutor");
        converted.put("Key Signature Tutor", "keySignatureTutor");
        converted.put("Diatonic Chord Tutor", "diatonicChordTutor");
        converted.put("Scale Modes Tutor", "scaleModesTutor");
    }

    /**
     * generates an array list with the chronological order of the tutors in the stage map.
     * This will be the order the tutors unlock in
     */
    private void generateTutorOrder() {
        //pitch tutor and musical terms tutor are unlocked by default
        tutorOrder.add("musicalTermsTutor");
        tutorOrder.add("pitchTutor");
        tutorOrder.add("scaleTutor");
        tutorOrder.add("chordTutor");
        tutorOrder.add("intervalTutor");
        tutorOrder.add("scaleTutorAdvanced");
        tutorOrder.add("chordTutorAdvanced");
        tutorOrder.add("chordSpellingTutor");
        tutorOrder.add("scaleSpellingTutor");
        tutorOrder.add("keySignatureTutor");
        tutorOrder.add("diatonicChordTutor");
        tutorOrder.add("scaleModesTutor");
    }

    /**
     * generates a hashmap that has the name of the tutor and its associative button
     */

    private void generateTutorAndButtonNames() {
        tutorAndButton.put("musicalTermsTutor", musicalTermsTutorButton);
        tutorAndButton.put("pitchTutor", pitchTutorButton);
        tutorAndButton.put("scaleTutor", scaleRecognitionTutorButton);
        tutorAndButton.put("chordTutor", chordRecognitionTutorButton);
        tutorAndButton.put("intervalTutor", intervalRecognitionButton);
        tutorAndButton.put("scaleTutorAdvanced", scaleRecognitionAdvancedTutorButton);
        tutorAndButton.put("chordTutorAdvanced", chordRecognitionAdvancedTutorButton);
        tutorAndButton.put("chordSpellingTutor", chordSpellingTutorButton);
        tutorAndButton.put("scaleSpellingTutor", scaleSpellingTutorButton);
        tutorAndButton.put("keySignatureTutor",keySignaturesTutorButton);
        tutorAndButton.put("diatonicChordTutor", diatonicChordsTutorButton);
        tutorAndButton.put("scaleModesTutor", majorModesTutorButton);

    }

    /** Generates the hash map that stores the locking status of each tutor (whether it is unlocked or locked)
     *
     */
    private void generateLockingStatus() {
        //pitch tutor and musical terms tutor are unlocked by default
        unlockStatus.put("musicalTermsTutor", true);
        unlockStatus.put("pitchTutor", true);
        unlockStatus.put("scaleTutor", false);
        unlockStatus.put("chordTutor", false);
        unlockStatus.put("intervalTutor", false);
        unlockStatus.put("scaleTutorAdvanced", false);
        unlockStatus.put("chordTutorAdvanced", false);
        unlockStatus.put("chordSpellingTutor", false);
        unlockStatus.put("scaleSpellingTutor", false);
        unlockStatus.put("keySignatureTutor", false);
        unlockStatus.put("diatonicChordTutor", false);
        unlockStatus.put("scaleModesTutor", false);
    }

    /**
     * Iterates through the nodes on the stage map, and asserts whether they are locked (disabled) or unlocked
     */
    public void visualiseLockedTutors() {
//        Image padlock = new Image(getClass().getResourceAsStream
//                ("/images/lock.png"), 10, 10, true, true);


        for (String tutor: unlockStatus.keySet()) {
            tutorAndButton.get(tutor).setDisable(false);
            if (unlockStatus.get(tutor) == false) {
                tutorAndButton.get(tutor).setDisable(true);
                //tutorAndButton.get(tutor).setGraphic(new ImageView(padlock));
//            } else {
//                unlockStatus.get(tutor).put(true);
            }
        }
    }

    /**
     * Fetches 3 most recent tutor score files for tutor of interest and checks scores
     */
    public void fetchTutorFile(String tutorId) {
//        boolean enoughEntries = false; //must be at least 3 entries for their to be a valid entry
        boolean unlock = true;
        ArrayList<TutorRecord> records = tutorHandler.getTutorData(converted.get(tutorId));

        if (records.size() < 3) {
            //if there are less than 3 existing files
        } else {
            for (int i = records.size() - 3; i < records.size(); i++) {
                TutorRecord record = records.get(i);
                if (!(record.getStats().get("questionsCorrect").toString()).equals("10")) {
                    unlock = false;
                }
            }
            if (unlock) {
                //set the tutor status to be unlocked
                unlockStatus.put(tutorOrder.get((tutorOrder.indexOf(converted.get(tutorId)) + 1)), true);
                visualiseLockedTutors();


            }
        }
    }


    /*
    //FXML methods below give actions to the buttons on the stage map. When a stage on the stage map is selected,
    //the corresponding tutor will launch
     */
    @FXML
    private void launchMusicalTermsTutor() {
        env.getRootController().getTutorFactory().openTutor("Musical Terms Tutor");
    }

    @FXML
    private void launchPitchTutor() {
        env.getRootController().getTutorFactory().openTutor("Pitch Comparison Tutor");
    }

    @FXML
    private void launchScaleRecognitionTutor() {
        env.getRootController().getTutorFactory().openTutor("Scale Recognition Tutor");
    }

    @FXML
    private void launchChordRecognitionTutor() {
        env.getRootController().getTutorFactory().openTutor("Chord Recognition Tutor");
    }

    @FXML
    private void launchIntervalRecognitionTutor() {
        env.getRootController().getTutorFactory().openTutor("Interval Recognition Tutor");
    }

    @FXML
    private void launchScaleRecognitionAdvancedTutor() {
        System.out.println("Advanced scale tutor");
        env.getRootController().getTutorFactory().openTutor("Scale Recognition Tutor");
    }

    @FXML
    private void launchChordRecognitionAdvancedTutor() {

        System.out.println("Chord Recognition Advanced Tutor");
        env.getRootController().getTutorFactory().openTutor("Chord Recognition Tutor");
    }

    @FXML
    private void launchChordSpellingTutor() {
        env.getRootController().getTutorFactory().openTutor("Chord Spelling Tutor");
    }

    @FXML
    private void launchScaleSpellingTutor() {
        env.getRootController().getTutorFactory().openTutor("Scale Spelling Tutor");
    }

    @FXML
    private void launchKeySignaturesTutor() {
        env.getRootController().getTutorFactory().openTutor("Key Signatures Tutor");
    }
    @FXML
    private void launchDiatonicChordsTutor() {
        env.getRootController().getTutorFactory().openTutor("Diatonic Chords Tutor");

    }
    @FXML
    private void launchMajorModesTutor() {
        env.getRootController().getTutorFactory().openTutor("Major Modes Tutor");
    }



}
