package seng302.gui;

import org.controlsfx.control.Notifications;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
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
    private VBox descriptionVbox;

    @FXML
    private Label unlockDescription;

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
    public HashMap<String, Boolean> unlockStatus;


    /**
     * links the tutor to the equivalent FXMl button
     */
    private HashMap<String, Button> tutorAndButton;

    /**
     * stores the unlock order of the tutors
     */
    private ArrayList<String> tutorOrder; //the order in which the tutors unlock

    /**
     * stores the convertion of tutor name to a shortened tutor string that is used for manipulations
     */
    public HashMap<String, String> converted;

    public HashMap<String, HashMap<String, Boolean>>  unlockDescriptions;



    UserPageController userPageController; //creates an insance of user page controller so we can access its methods

    Environment env; //declare the environment

    private String nextUnlockTutor = "scaleTutor";

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

    public void setDescription(){


        for(String key: unlockDescriptions.get(nextUnlockTutor).keySet()){
            HBox description = new HBox();
            description.getChildren().add(new Label(key));
            ImageView image = new ImageView();

            if(unlockDescriptions.get(nextUnlockTutor).get(key)){
                image.setImage(new Image(getClass().getResourceAsStream("/images/tick.png"), 25, 25, false, false));
            }else{
                image.setImage(new Image(getClass().getResourceAsStream("/images/redx.png"), 25, 25, false, false));
            }
            description.getChildren().add(image);


            descriptionVbox.getChildren().add(description);
        }
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
        generateDescriptions();
        visualiseLockedTutors();
        System.out.println(unlockStatus);
    }

    /**
     * Creates the converted strings
     *
     **/
    private void generateConverted(){
        converted.put("Musical Terms Tutor", "musicalTermsTutor");
        converted.put("Pitch Comparison Tutor", "pitchTutor" );
        converted.put("Scale Recognition Tutor (Basic)","basicScaleTutor");
        converted.put("Chord Recognition Tutor (Basic)", "basicChordTutor");
        converted.put("Interval Recognition Tutor", "intervalTutor");
        converted.put("Scale Recognition Tutor", "scaleTutor");
        converted.put("Chord Recognition Tutor", "chordTutor");
        converted.put("Chord Spelling Tutor", "chordSpellingTutor");
        converted.put("Scale Spelling Tutor", "scaleSpellingTutor");
        converted.put("Key Signature Tutor", "keySignatureTutor");
        converted.put("Diatonic Chord Tutor", "diatonicChordTutor");
        converted.put("Scale Modes Tutor", "scaleModesTutor");
    }


    private void generateDescriptions(){

        unlockDescriptions = new HashMap<>();
        for (String tutor : tutorOrder.subList(2,tutorOrder.size())){
            HashMap<String, Boolean> temp = new HashMap<>();
            temp.put("To unlock: the last 3 tutor records of "+ tutorOrder.get(tutorOrder.indexOf(tutor) - 1) + " need to be >=7", false);
            if(tutor.equals("scaleTutor")||tutor.equals("chordTutor")){
                temp.put("To unlock: the last 3 tutor records of "+ tutor  + " (Basic) need to be >=9", false);
            }
            unlockDescriptions.put(tutor, temp);

        }
        System.out.println(unlockDescriptions);

    }


    /**
     * generates an array list with the chronological order of the tutors in the stage map.
     * This will be the order the tutors unlock in
     */
    private void generateTutorOrder() {
        //pitch tutor and musical terms tutor are unlocked by default
        tutorOrder.add("musicalTermsTutor");
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

    /**
     * generates a hashmap that has the name of the tutor and its associative button
     */

    private void generateTutorAndButtonNames() {
        tutorAndButton.put("musicalTermsTutor", musicalTermsTutorButton);
        tutorAndButton.put("pitchTutor", pitchTutorButton);
        tutorAndButton.put("basicScaleTutor", basicScaleRecognitionTutorButton);
        tutorAndButton.put("basicChordTutor", basicChordRecognitionTutorButton);
        tutorAndButton.put("intervalTutor", intervalRecognitionButton);
        tutorAndButton.put("scaleTutor", scaleRecognitionTutorButton);
        tutorAndButton.put("chordTutor", chordRecognitionTutorButton);
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

    /**
     * Iterates through the nodes on the stage map, and asserts whether they are locked (disabled) or unlocked
     */
    public void visualiseLockedTutors() {
        Image padlock = new Image(getClass().getResourceAsStream
                ("/images/lock.png"), 10, 10, true, true);


        //If in competitive mode, relevant stages should be locked
        if (env.getUserHandler().getCurrentUser().getProjectHandler().getCurrentProject().getIsCompetitiveMode()) {
            for (String tutor: unlockStatus.keySet()) {
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
            for (String tutor: unlockStatus.keySet()) {
                tutorAndButton.get(tutor).setDisable(false);
                tutorAndButton.get(tutor).setGraphic(null);
            }

        }

    }



    /**
     * Fetches 3 most recent tutor score files for tutor of interest and checks scores
     */
    public void fetchTutorFile(String tutorId) {
        boolean unlock = true;


        if(tutorId.equals("basicScaleTutor")) {
            ArrayList<TutorRecord> basicRecords = tutorHandler.getTutorData(converted.get("intervalTutor"));

            if (basicRecords.size() < 3) {
                //if there are less than 3 existing files
            } else {
                for (int i = basicRecords.size() - 3; i < basicRecords.size(); i++) {
                    TutorRecord record = basicRecords.get(i);
                    if (!(record.getStats().get("questionsCorrect").intValue() >= 7)) {
                        unlock = false;
                    }
                }

            }
        }
        ArrayList<TutorRecord> records = tutorHandler.getTutorData(converted.get(tutorId));

        if (records.size() < 3) {
            //if there are less than 3 existing files
        } else {
            for (int i = records.size() - 3; i < records.size(); i++) {
                TutorRecord record = records.get(i);
                if (!(record.getStats().get("questionsCorrect").intValue() >= 7)) {
                    unlock = false;
                }
            }

            if(tutorId.equals("intervalTutor")) {
                ArrayList<TutorRecord> basicRecords = tutorHandler.getTutorData(converted.get("basicScaleTutor"));

                if (basicRecords.size() < 3) {
                    //if there are less than 3 existing files
                } else {
                    for (int i = basicRecords.size() - 3; i < basicRecords.size(); i++) {
                        TutorRecord record = basicRecords.get(i);
                        if (!(record.getStats().get("questionsCorrect").intValue() >= 9)) {
                            unlock = false;
                        }
                    }

                }
            }

            if(tutorId.equals("scaleTutor")) {
                ArrayList<TutorRecord> basicRecords = tutorHandler.getTutorData(converted.get("basicChordTutor"));

                if (basicRecords.size() < 3) {
                    //if there are less than 3 existing files
                } else {
                    for (int i = basicRecords.size() - 3; i < basicRecords.size(); i++) {
                        TutorRecord record = basicRecords.get(i);
                        if (!(record.getStats().get("questionsCorrect").intValue() >= 9)) {
                            unlock = false;
                        }
                    }

                }
            }

            if(tutorId.equals("basicScaleTutor")) {
                ArrayList<TutorRecord> basicRecords = tutorHandler.getTutorData(converted.get("intervalTutor"));

                if (basicRecords.size() < 3) {
                    //if there are less than 3 existing files
                } else {
                    for (int i = basicRecords.size() - 3; i < basicRecords.size(); i++) {
                        TutorRecord record = basicRecords.get(i);
                        if (!(record.getStats().get("questionsCorrect").intValue() >= 7)) {
                            unlock = false;
                        }
                    }

                }
            }

            if (unlock) {
                //set the tutor status to be unlocked
                unlockStatus.put(tutorOrder.get((tutorOrder.indexOf(converted.get(tutorId)) + 1)), true);
                visualiseLockedTutors();

                String nextTutorName = null;
                for (String t : converted.keySet()) {
                    if (converted.get(t).equals(tutorOrder.get((tutorOrder.indexOf(tutorId) + 3)))) {
                        nextTutorName = t;
                    }
                }
                Image unlockImage = new Image(getClass().getResourceAsStream("/images/unlock.png"), 75, 75, true, true);
                Notifications.create()
                        .title("Tutor Unlocked")
                        .text("Well done! \nYou have unlocked " + nextTutorName)
                        .hideAfter(new Duration(10000))
                        .graphic(new ImageView(unlockImage))
                        .show();
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



}
