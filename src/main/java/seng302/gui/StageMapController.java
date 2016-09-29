package seng302.gui;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.controlsfx.control.Notifications;
import seng302.Environment;
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
    private VBox descriptionVbox;

    @FXML
    private Label unlockHeader;

    @FXML
    private Button keySignaturesTutorButton;

    @FXML
    private AnchorPane stageMap;

    @FXML
    private Button diatonicChordsTutorButton;

    @FXML
    private Button basicScaleRecognitionTutorButton;

    @FXML
    private Button majorModesTutorButton;

    @FXML
    private Button musicalTermsTutorButton;

    @FXML
    private Button microphoneInputTutorButton;

    @FXML
    private Button chordRecognitionTutorButton;

    @FXML
    private Button pitchTutorButton;

    @FXML
    private Button chordSpellingTutorButton;

    @FXML
    private Button scaleSpellingTutorButton;

    @FXML
    private Button basicChordRecognitionTutorButton;

    @FXML
    private Button intervalRecognitionButton;

    private TutorHandler tutorHandler;


    /**
     * Stores the status of each tutor(if the tutor is unlocked)
     */
    public static HashMap<String, Boolean> unlockStatus;


    /**
     * links the tutor to the equivalent FXMl button
     */
    private HashMap<String, Button> tutorAndButton;

    /**
     * stores the unlock order of the tutors
     */
    public static ArrayList<String> tutorOrder; //the order in which the tutors unlock

    /**
     * stores the conversion of tutor name to a shortened tutor string that is used for
     * manipulations
     */
    public static BidiMap<String, String> converted;

    static {
        converted = new DualHashBidiMap<>();
        converted.put("Musical Terms Tutor", "musicalTermsTutor");
        converted.put("Pitch Comparison Tutor", "pitchTutor");
        converted.put("Scale Recognition Tutor (Basic)", "basicScaleTutor");
        converted.put("Chord Recognition Tutor (Basic)", "basicChordTutor");
        converted.put("Interval Recognition Tutor", "intervalTutor");
        converted.put("Scale Recognition Tutor", "scaleTutor");
        converted.put("Chord Recognition Tutor", "chordTutor");
        converted.put("Chord Spelling Tutor", "chordSpellingTutor");
        converted.put("Scale Spelling Tutor", "scaleSpellingTutor");
        converted.put("Key Signature Tutor", "keySignatureTutor");
        converted.put("Diatonic Chord Tutor", "diatonicChordTutor");
        converted.put("Scale Modes Tutor", "scaleModesTutor");
        converted.put("Microphone Input Tutor", "microphoneInputTutor");


        tutorOrder = new ArrayList<>();
        tutorOrder.add("musicalTermsTutor");
        tutorOrder.add("microphoneInputTutor");
        tutorOrder.add("pitchTutor");
        tutorOrder.add("basicScaleTutor");
        tutorOrder.add("basicChordTutor");
        tutorOrder.add("intervalTutor");
        tutorOrder.add("scaleTutor");
        tutorOrder.add("chordTutor");
        tutorOrder.add("chordSpellingTutor");
        tutorOrder.add("scaleSpellingTutor");
        tutorOrder.add("keySignatureTutor");
        tutorOrder.add("diatonicChordTutor");
        tutorOrder.add("scaleModesTutor");
    }

    public HashMap<String, HashMap<String, Boolean>> unlockDescriptions;


    UserPageController userPageController; //reference to user page controller so we can access its methods

    Environment env; //declare the environment

    private String nextUnlockTutor = "basicScaleTutor"; //Is this correct? Should it be scaleTutor?


    /**
     * Initializes the environment and the user page controller instance
     */
    public void setEnvironment(Environment env) {
        this.env = env;
        currentProject = env.getUserHandler().getCurrentUser().getProjectHandler().getCurrentProject();
        tutorHandler = currentProject.getTutorHandler();

        userPageController = env.getUserPageController();
    }

    public void setEnvOnly(Environment env) {
        this.env = env;
    }


    public StageMapController() {

    }

    public HashMap<String, HashMap<String, Boolean>> getUnlockDescriptions() {
        return unlockDescriptions;
    }

    /**
     * Finds the next tutor on the stagemap that is still locked
     */
    private void setNextLockedStage() {
        for (String tutor : tutorOrder) {
            if (!unlockStatus.get(tutor)) {
                nextUnlockTutor = tutor;
                break;
            }
        }
    }

    public void hideNextUnlockDescription(Boolean hide) {
        if (hide) {
            descriptionVbox.setVisible(false);
            descriptionVbox.setManaged(false);
        } else {
            descriptionVbox.setVisible(true);
            descriptionVbox.setManaged(true);
        }
    }


    /**
     * sets the description of what the user needs to do to unlock the next tutor
     */
    public void setDescription() {
        setNextLockedStage();
        String nextTutorName = null;
        for (String t : converted.keySet()) {
            if (converted.get(t).equals(nextUnlockTutor)) {
                nextTutorName = t;
            }
        }

        if(nextUnlockTutor.equals("scaleTutor")) {
            if (checkLast3Records("basicScaleTutor")) {

                unlockDescriptions.get(nextUnlockTutor).put("basicScaleTutor", true);

            }
        }
        else if(nextUnlockTutor.equals("chordTutor")) {
            if (checkLast3Records("basicChordTutor")) {

                unlockDescriptions.get(nextUnlockTutor).put("basicChordTutor", true);

            }
        }



        unlockHeader.setText("To unlock the " + nextTutorName + ":");
        if (descriptionVbox.getChildren().size() > 1) {
            descriptionVbox.getChildren().remove(1, descriptionVbox.getChildren().size());

        }

        for (String key : unlockDescriptions.get(nextUnlockTutor).keySet()) {
            HBox description = new HBox();
            ImageView image = new ImageView();

            String tickPath = "/images/tick.png";

            Image tick = UserSummaryController.imageCache.retrieve(tickPath, 25);

            String crossPath = "/images/redx.png";

            Image cross = UserSummaryController.imageCache.retrieve(crossPath, 25);


            if (unlockDescriptions.get(nextUnlockTutor).get(key)) {
                image.setImage(tick);
            } else {
                image.setImage(cross);
            }

            description.getChildren().add(image);
            String fulltutorName = converted.getKey(key);
            if (unlockDescriptions.get(nextUnlockTutor).keySet().size() > 1 && (key.equals("basicScaleTutor") || key.equals("basicChordTutor"))) {
                description.getChildren().add(new Label("Get 3 consecutive scores of 9 or higher in the " + fulltutorName));
            } else {

                description.getChildren().add(new Label("Get 3 consecutive scores of 7 or higher in the " + fulltutorName));
            }


            description.setAlignment(Pos.CENTER);
            description.setSpacing(10);
            description.setHgrow(description.getChildren().get(0), Priority.ALWAYS);
            description.setHgrow(description.getChildren().get(1), Priority.ALWAYS);

            descriptionVbox.getChildren().add(description);
        }
    }


    /**
     * gets the HashMap that stores what tutors are unlocked
     */
    public HashMap getUnlockStatus() {
        return this.unlockStatus;
    }

    /**
     * Initiates the required information of the stage map (which tutor is associated with which
     * button, the order of the tutors, and whether the tutor is unlocked or locked)
     */
    public void create() {
        unlockStatus = new HashMap<>();

        generateLockingStatus();
        generateTutorAndButtonNames();
        generateDescriptions();
        visualiseLockedTutors();
        if (env.getUserHandler().getCurrentUser().getProjectHandler().getCurrentProject().getIsCompetitiveMode()) {
            hideNextUnlockDescription(false);
        } else {
            hideNextUnlockDescription(true);
        }
    }


    /**
     * Generates the unlockDescriptions HashMap which stores the requirements to unlock each tutor
     * and weather or not they have been completed.
     */
    private void generateDescriptions() {

        unlockDescriptions = new HashMap<>();
        for (String tutor : tutorOrder.subList(3, tutorOrder.size())) {
            HashMap<String, Boolean> temp = new HashMap<>();
            temp.put(tutorOrder.get(tutorOrder.indexOf(tutor) - 1), false);
            if (tutor.equals("scaleTutor") || tutor.equals("chordTutor")) {
                temp.put("basic" + tutor.substring(0, 1).toUpperCase() + tutor.substring(1), false);
            }
            unlockDescriptions.put(tutor, temp);

        }

    }

    /**
     * generates a hashmap that has the name of the tutor and its associative button
     */

    public void generateTutorAndButtonNames() {
        tutorAndButton = new HashMap<>();
        tutorAndButton.put("musicalTermsTutor", musicalTermsTutorButton);
        tutorAndButton.put("microphoneInputTutor" , microphoneInputTutorButton);
        tutorAndButton.put("pitchTutor", pitchTutorButton);
        tutorAndButton.put("basicScaleTutor", basicScaleRecognitionTutorButton);
        tutorAndButton.put("basicChordTutor", basicChordRecognitionTutorButton);
        tutorAndButton.put("intervalTutor", intervalRecognitionButton);
        tutorAndButton.put("scaleTutor", scaleRecognitionTutorButton);
        tutorAndButton.put("chordTutor", chordRecognitionTutorButton);
        tutorAndButton.put("chordSpellingTutor", chordSpellingTutorButton);
        tutorAndButton.put("scaleSpellingTutor", scaleSpellingTutorButton);
        tutorAndButton.put("keySignatureTutor", keySignaturesTutorButton);
        tutorAndButton.put("diatonicChordTutor", diatonicChordsTutorButton);
        tutorAndButton.put("scaleModesTutor", majorModesTutorButton);

    }

    /**
     * Generates the hash map that stores the locking status of each tutor (whether it is unlocked
     * or locked)
     */
    public static void generateLockingStatus() {
        //pitch tutor and musical terms tutor are unlocked by default

        unlockStatus.put("musicalTermsTutor", true);
        unlockStatus.put("microphoneInputTutor", true);
        unlockStatus.put("pitchTutor", true);
        unlockStatus.put("basicScaleTutor", false);
        unlockStatus.put("basicChordTutor", false);
        unlockStatus.put("intervalTutor", false);
        unlockStatus.put("scaleTutor", false);
        unlockStatus.put("chordTutor", false);
        unlockStatus.put("chordSpellingTutor", false);
        unlockStatus.put("scaleSpellingTutor", false);
        unlockStatus.put("keySignatureTutor", false);
        unlockStatus.put("diatonicChordTutor", false);
        unlockStatus.put("scaleModesTutor", false);



    }

    public void unlockTutor(String id) {

        unlockStatus.put(id, true);
        this.env.getUserHandler().getCurrentUser().saveAll();
    }

    /**
     * Iterates through the nodes on the stage map, and asserts whether they are locked (disabled)
     * or unlocked
     */
    public void visualiseLockedTutors() {
        Image padlock = new Image(getClass().getResourceAsStream
                ("/images/lock.png"), 10, 10, true, true);


        //If in competitive mode, relevant stages should be locked
        if (env.getUserHandler().getCurrentUser().getProjectHandler().getCurrentProject().getIsCompetitiveMode()) {
            for (String tutor : unlockStatus.keySet()) {

                    tutorAndButton.get(tutor).setDisable(false);
                    tutorAndButton.get(tutor).setGraphic(null);
                    if (unlockStatus.get(tutor) == false) {
                        tutorAndButton.get(tutor).setDisable(true);
                        ImageView iv1 = new ImageView();
                        iv1.setImage(padlock);
                        tutorAndButton.get(tutor).setGraphic(iv1);

                    }

            }
            //else if in practice mode, all of the levels should be unlocked
        } else {
            for (String tutor : unlockStatus.keySet()) {

                    tutorAndButton.get(tutor).setDisable(false);
                    tutorAndButton.get(tutor).setGraphic(null);


            }

        }

    }


    /**
     * Helper function to check the last three records for a given tutor name
     * and verify if that tutor meets unlock criteria.
     * @param tutor tutor to check unlock criteria for.
     * @return
     */
    private Boolean checkLast3Records(String tutor){
        ArrayList<TutorRecord> records = tutorHandler.getTutorData(tutor);
        boolean unlock = true;
        int requiredScore = 7; //7
        if(nextUnlockTutor.equals("scaleTutor")||nextUnlockTutor.equals("chordTutor")){
            if(tutor.equals("basicScaleTutor")|| tutor.equals("basicChordTutor")){
                requiredScore = 9; //9
            }

        }

        if (records.size() < 3) {
            unlock = false;
            //if there are less than 3 existing files
        } else {
            for (int i = records.size() - 3; i < records.size(); i++) {
                TutorRecord record = records.get(i);
                if (!(record.getStats().get("questionsCorrect").intValue() >= requiredScore)) {
                    unlock = false;
                }


            }
        }
        return unlock;
    }


    /**
     * Fetches 3 most recent tutor score files for tutor of interest and checks scores
     */
    public void fetchTutorFile(String tutorId) {

        for(String tutor: unlockDescriptions.get(nextUnlockTutor).keySet()) {

            if (checkLast3Records(tutor)) {

                unlockDescriptions.get(nextUnlockTutor).put(tutor, true);

            }


        }


        if (!(unlockDescriptions.get(nextUnlockTutor).values().contains(false))
                && env.getUserHandler().getCurrentUser().getProjectHandler().getCurrentProject().getIsCompetitiveMode()
                && !tutorId.equals("Scale Modes Tutor")) {

            unlockTutor(tutorOrder.get(tutorOrder.indexOf(converted.get(tutorId)) + 1));
            visualiseLockedTutors();
            String nextTutorName = null;
            for (String t : converted.keySet()) {
                if (converted.get(t).equals(nextUnlockTutor)) {
                    nextTutorName = t;
                }
            }



            Image unlockImage = new Image(getClass().getResourceAsStream("/images/unlock.png"), 75, 75, true, true);
            Notifications.create()
                    .title("Tutor Unlocked")
                    .text("Well done! \nYou have unlocked the " + nextTutorName)
                    .hideAfter(new Duration(10000))
                    .graphic(new ImageView(unlockImage))
                    .show();

        }

        setDescription();
    }

    /**
     * Load a student map when no unlock data is available
     */
    public void loadStudentMap() {

        Image padlock = new Image(getClass().getResourceAsStream
                ("/images/lock.png"), 10, 10, true, true);

        for (String tutor : tutorOrder) {
            if (tutor.equals("musicalTermsTutor") || tutor.equals("pitchTutor") || tutor.equals("microphoneInputTutor")) {
                tutorAndButton.get(tutor).setDisable(false);
            } else {
                tutorAndButton.get(tutor).setDisable(true);
                ImageView iv1 = new ImageView();
                iv1.setImage(padlock);
                tutorAndButton.get(tutor).setGraphic(iv1);
            }
        }
        unlockHeader.setVisible(false);
        unlockHeader.setManaged(false);

    }

    /**
     * Load a stage map for a specific student, given their unlock data
     *
     * @param unlockStatus A collection of tutors and whether or not the student has unlocked them
     */
    public void loadStudentMap(HashMap<String, Boolean> unlockStatus) {

        Image padlock = new Image(getClass().getResourceAsStream
                ("/images/lock.png"), 10, 10, true, true);

        for (String tutor : unlockStatus.keySet()) {
            tutorAndButton.get(tutor).setDisable(false);
            tutorAndButton.get(tutor).setGraphic(null);
            if (!unlockStatus.get(tutor)) {
                tutorAndButton.get(tutor).setDisable(true);
                ImageView iv1 = new ImageView();
                iv1.setImage(padlock);
                tutorAndButton.get(tutor).setGraphic(iv1);

            }
        }

        unlockHeader.setVisible(false);
        unlockHeader.setManaged(false);
    }


    /**
    *FXML methods below give actions to the buttons on the stage map. When a stage on the stage map is selected,
    *the corresponding tutor will launch
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
    private void launchBasicScaleRecognitionTutor() {
        env.getRootController().getTutorFactory().openTutor("Scale Recognition Tutor (Basic)");
    }

    @FXML
    private void launchBasicChordRecognitionTutor() {
        env.getRootController().getTutorFactory().openTutor("Chord Recognition Tutor (Basic)");
    }

    @FXML
    private void launchIntervalRecognitionTutor() {
        env.getRootController().getTutorFactory().openTutor("Interval Recognition Tutor");
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
    private void launchChordSpellingTutor() {
        env.getRootController().getTutorFactory().openTutor("Chord Spelling Tutor");
    }

    @FXML
    private void launchScaleSpellingTutor() {
        env.getRootController().getTutorFactory().openTutor("Scale Spelling Tutor");
    }

    @FXML
    private void launchKeySignaturesTutor() {
        env.getRootController().getTutorFactory().openTutor("Key Signature Tutor");
    }

    @FXML
    private void launchDiatonicChordsTutor() {
        env.getRootController().getTutorFactory().openTutor("Diatonic Chord Tutor");

    }

    @FXML
    private void launchMajorModesTutor() {
        env.getRootController().getTutorFactory().openTutor("Scale Modes Tutor");
    }

    @FXML
    private void launchMicrophoneInputTutor() {
        env.getRootController().getTutorFactory().openTutor("Microphone Input Tutor");
    }


}
