package seng302.gui;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import seng302.Environment;

/**
 * Factory class for generating tutor instances.
 */
public class TutorFactory {




    Environment env;
    AnchorPane parentNode;

    /**
     * opens the scale tutor when the scale menu option is pressed If there is already an open tutor
     * of the same form then it sets focus to the already open tutor
     */


    public TutorFactory(Environment env, AnchorPane parentNode) {
        this.env = env;
        this.parentNode = parentNode;
    }

    /**
     * opens a tutor given a valid tutor name.
     * @param tutorName name of the tutor to open
     */
    public void openTutor(String tutorName) {


        env.getRootController().showUserBar(true);
        env.getRootController().setHeader(tutorName);
        switch (tutorName) {
            case "Pitch Comparison Tutor":
                openPitchTutor();
                break;
            case "Interval Recognition Tutor":
                openIntervalTutor();
                break;
            case "Scale Recognition Tutor":
                openScaleTutor();
                break;
            case "Musical Terms Tutor":
                openMusicalTermTutor();
                break;
            case "Chord Recognition Tutor":
                openChordTutor();
                break;
            case "Chord Spelling Tutor":
                openSpellingTutor();
                break;
            case "Key Signature Tutor":
                openKeySignatureTutor();
                break;
            case "Diatonic Chord Tutor":
                openDiatonicChordTutor();
                break;
            case "Scale Modes Tutor":
                openScaleModesTutor();
                break;
            case "Scale Spelling Tutor":
                openScaleSpellingTutor();
                break;
        }
    }

    /**
     * Expands the subject node to fill the parent.
     *
     * @param node node to expand
     */
    private void fillParentAnchors(Node node) {
        AnchorPane.setRightAnchor(node, 0.0);
        AnchorPane.setLeftAnchor(node, 0.0);
        AnchorPane.setBottomAnchor(node, 0.0);
        AnchorPane.setTopAnchor(node, 0.0);

    }


    /**
     * loads an fxml node.
     * @param loader FXMLloader to load.
     */
    private void loadNode(FXMLLoader loader) {
        try {
            Node node = loader.load();
            parentNode.getChildren().setAll(node);
            fillParentAnchors(node);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * opens the pitch tutor when the pitch tutor menu option is pressed If there is already an open
     * tutor of the same form then it sets focus to the already open tutor
     */
    private void openPitchTutor() {

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/Views/PitchComparisonPane.fxml"));

        loadNode(loader);


        PitchComparisonTutorController pitchComparisonTutorController = loader.getController();
        pitchComparisonTutorController.create(env);


    }

    /**
     * opens the interval tutor when the interval menu option is pressed If there is already an open
     * tutor of the same form then it sets focus to the already open tutor
     */
    private void openIntervalTutor() {

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/Views/IntervalRecognitionPane.fxml"));
        loadNode(loader);
        IntervalRecognitionTutorController intervalRecognitionTabController = loader.getController();
        intervalRecognitionTabController.create(env);


    }

    /**
     * opens the musical terms tutor when the musical term tutor menu option is pressed If there is
     * already an open tutor of the same form then it sets focus to the already open tutor
     */
    private void openMusicalTermTutor() {

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/Views/MusicalTermsPane.fxml"));
        loadNode(loader);
        MusicalTermsTutorController musicalTermsTabController = loader.getController();
        musicalTermsTabController.create(env);


    }


    /***
     * Opens the scale tutor
     */
    private void openScaleTutor() {

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/Views/ScaleRecognitionPane.fxml"));
        loadNode(loader);
        ScaleRecognitionTutorController scaleRecognitionTutorController = loader.getController();
        scaleRecognitionTutorController.create(env);


    }

    /**
     * opens the chord tutor when the chord tutor menu option is pressed If there is already an open
     * tutor of the same form then it sets focus to the already open tutor
     */
    @FXML
    private void openChordTutor() {


        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/Views/ChordRecognitionPane.fxml"));

        loadNode(loader);

        ChordRecognitionTutorController chordRecognitionTabController = loader.getController();
        chordRecognitionTabController.create(env);

    }

    /**
     * Opens the diatonic chord tutor when the diatonic chord tutor menu option is pressed. If there
     * is already an open tutor of the same form then it sets focus to the already open tutor.
     */
    @FXML
    private void openDiatonicChordTutor() {


        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/Views/DiatonicChordPane.fxml"));

        loadNode(loader);

        DiatonicChordsTutorController diatonicChordsController = loader.getController();
        diatonicChordsController.create(env);


    }

    /**
     * opens the keySignatures tutor when the key signatures tutor menu option is pressed If there
     * is already an open tutor of the same form then it sets focus to the already open tutor
     */
    @FXML
    private void openKeySignatureTutor() {


        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/Views/KeySignaturesPane.fxml"));

        loadNode(loader);

        KeySignaturesTutorController keySignaturesTabController = loader.getController();
        keySignaturesTabController.create(env);


    }

    /**
     * opens the scale modes tutor when the scale modes tutor menu option is pressed If there is
     * already an open tutor of the same form then it sets focus to the already open tutor
     */
    @FXML
    private void openScaleModesTutor() {


        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/Views/ScaleModesPane.fxml"));

        loadNode(loader);

        ScaleModesTutorController scaleModesTutorController = loader.getController();
        scaleModesTutorController.create(env);


    }

    @FXML
    private void openScaleSpellingTutor() {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/Views/ScaleSpellingPane.fxml"));

        loadNode(loader);

        ScaleSpellingTutorController scaleSpellingTutorController = loader.getController();
        scaleSpellingTutorController.create(env);
    }

    /**
     * Opens the chord spelling tutor. If this tutor is already open, focus is transferred to it.
     */
    @FXML
    private void openSpellingTutor() {

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/Views/ChordSpellingPane.fxml"));
        loadNode(loader);
        ChordSpellingTutorController chordSpellingTutorController = loader.getController();
        chordSpellingTutorController.create(env);

    }
}
