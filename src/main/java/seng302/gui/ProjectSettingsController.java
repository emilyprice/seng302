package seng302.gui;

import com.jfoenix.controls.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.sound.midi.Instrument;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import seng302.Environment;
import seng302.Users.Project;
import seng302.Users.ProjectHandler;
import seng302.utility.InstrumentUtility;
import seng302.gui.KeyboardPaneController;

/**
 * Controller class for the GUI used to change project-based settings.
 */
public class ProjectSettingsController {

    @FXML
    private JFXSlider tempoSlider;

    @FXML
    private Label tempoText;

    @FXML
    private JFXToggleButton modeToggle;

    @FXML
    private JFXToggleButton visualiserToggle;

    @FXML
    private Label visualiserLabel;

    @FXML
    private JFXComboBox instrumentSelector;

    @FXML
    private JFXComboBox rhythmSelector;

    private Environment env;

    private ProjectHandler projectHandler;

    @FXML
    private JFXButton btnDeleteProject;

    @FXML
    private AnchorPane settingsPane;

    @FXML
    private void initialize(){

    }


    /**
     * Links the project settings controller to the environment, so it has access to the current
     * project and settings.
     */
    public void create(Environment env) {
        this.env = env;
        env.getRootController().setHeader("Project Settings");
        projectHandler = env.getUserHandler().getCurrentUser().getProjectHandler();


        setupToggles();


        setupTempoSlider();


        setupInstrumentSelector();

        setupRhythmSelector();
    }

    /**
     * Used by the modes toggle to switch modes
     */
    @FXML
    private void toggleBetweenModes() {

        Project currentProject = env.getUserHandler().getCurrentUser().getProjectHandler().getCurrentProject();
        if (modeToggle.isSelected()) {
            // Competition Mode
            currentProject.setIsCompetitiveMode(true);

        } else {
            // Practice mode
            currentProject.setIsCompetitiveMode(false);

        }
    }

    /**
     * Creates the combo box that allows the user to change their instrument. Adds all available
     * instruments as possible selections, and creates a listener.
     */
    private void setupInstrumentSelector() {
        Instrument[] instruments = env.getPlayer().getAvailableInstruments();

        List<String> textInstruments = new ArrayList<>();

        for (Instrument instrument : instruments) {
            textInstruments.add(instrument.getName());

        }

        instrumentSelector.getItems().setAll(textInstruments);
        instrumentSelector.getSelectionModel().select(env.getPlayer().getInstrument().getName());

        instrumentSelector.valueProperty().addListener((observable, oldValue, newValue) -> {
            env.getPlayer().setInstrument(InstrumentUtility.getInstrumentByName((String) newValue, env));
            projectHandler.getCurrentProject().checkChanges("instrument");
            env.getUserHandler().getCurrentUser().getProjectHandler().getCurrentProject().getBadgeManager().getBadge("Instrument master").updateBadgeProgress(env, 1);
        });
    }

    /**
     * Initialises the rhythm selector combo box. This allows the user to select from one of the
     * four preset rhythm types.
     */
    private void setupRhythmSelector() {
        String currentDivisions = env.getPlayer().getRhythmHandler().toString();

        String[] rhythmOptions = {"Straight", "Light", "Medium", "Heavy"};
        rhythmSelector.getItems().setAll(rhythmOptions);

        switch (currentDivisions) {
            case "Rhythm beat divisions: 3/4 1/4":
                rhythmSelector.setValue("Heavy");
                break;
            case "Rhythm beat divisions: 2/3 1/3":
                rhythmSelector.setValue("Medium");
                break;
            case "Rhythm beat divisions: 5/8 3/8":
                rhythmSelector.setValue("Light");
                break;
            case "Rhythm beat divisions: 1/2":
                rhythmSelector.setValue("Straight");
                break;
        }


        rhythmSelector.valueProperty().addListener((observable, oldValue, newValue) -> {
            String rhythmStyle = newValue.toString();
            float[] divisions;
            if (rhythmStyle.equals("Heavy")) {
                divisions = new float[]{3.0f / 4.0f, 1.0f / 4.0f};
            } else if (rhythmStyle.equals("Medium")) {
                divisions = new float[]{2.0f / 3.0f, 1.0f / 3.0f};
            } else if (rhythmStyle.equals("Light")) {
                divisions = new float[]{5.0f / 8.0f, 3.0f / 8.0f};
            } else {
                //selection was "straight"
                divisions = new float[]{0.5f};
            }
            env.getPlayer().getRhythmHandler().setRhythmTimings(divisions);
            projectHandler.getCurrentProject().checkChanges("rhythm");

        });
    }

    /**
     * Creates the slider object for changing the tempo. A listener on this updates the tempo.
     */
    private void setupTempoSlider() {
        tempoText.setText(Integer.toString(env.getPlayer().getTempo()));
        tempoSlider.setValue(env.getPlayer().getTempo());

        // The listener for the number of questions selected
        tempoSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            tempoText.setText(Integer.toString(newValue.intValue()));

            if (newValue.intValue() >= 20 && newValue.intValue() <= 300) {
                env.getPlayer().setTempo(newValue.intValue());
                env.getUserPageController().updateCurrentTempo(newValue);
                projectHandler.getCurrentProject().checkChanges("tempo");

            }
        });
    }

    /**
     * Sets up the mode and visualiser toggles. As these are closely linked, they are set up
     * together.
     */
    private void setupToggles() {
        modeToggle.getStyleClass().remove(0);
        visualiserToggle.getStyleClass().remove(0);

        try {
            modeToggle.setSelected(projectHandler.getCurrentProject().getIsCompetitiveMode());
        } catch (Exception e) {
            // Default to competition mode
            modeToggle.setSelected(true);
        }


        if (projectHandler.getCurrentProject().getIsCompetitiveMode()) {
            // do not show the visualiser options
            toggleShowHideVisualiser(false);
        } else {
            try {
                boolean visualiserOn = projectHandler.getCurrentProject().getVisualiserOn();
                visualiserToggle.setSelected(visualiserOn);
                if (visualiserOn) {
                    visualiserLabel.setText("Keyboard Visualiser ON");
                } else {
                    visualiserLabel.setText("Keyboard Visualiser OFF");
                }
            } catch (Exception e) {
                // Default to off
                visualiserToggle.setSelected(false);
                visualiserLabel.setText("Keyboard Visualiser OFF");
            }
        }

        visualiserToggle.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                projectHandler.getCurrentProject().setVisualiserOn(true);
                env.getUserHandler().getCurrentUser().updateProperties();
                env.getUserHandler().getCurrentUser().saveProperties();
                visualiserLabel.setText("Keyboard Visualiser ON");

            } else {
                env.getUserHandler().getCurrentUser().getProjectHandler().getCurrentProject().setVisualiserOn(false);
                env.getUserHandler().getCurrentUser().updateProperties();
                env.getUserHandler().getCurrentUser().saveProperties();
                visualiserLabel.setText("Keyboard Visualiser OFF");
            }
        });

        modeToggle.selectedProperty().addListener((observable, oldValue, newValue) -> {
            Project currentProject = env.getUserHandler().getCurrentUser().getProjectHandler().getCurrentProject();
            if (newValue) {
                // Competition Mode
                currentProject.setIsCompetitiveMode(true);
                toggleShowHideVisualiser(false);
            } else {
                // Practice mode
                currentProject.setIsCompetitiveMode(false);
                toggleShowHideVisualiser(true);
            }
        });

    }

    /**
     * Updates the GUI to show or hide the visualiser toggle and label.
     *
     * @param isShow If false, hide the visualiser toggle and its label. Else, show the visualiser
     *               and its toggle.
     */
    private void toggleShowHideVisualiser(boolean isShow) {
        visualiserToggle.setVisible(isShow);
        visualiserLabel.setVisible(isShow);
        visualiserToggle.setManaged(isShow);
        visualiserLabel.setManaged(isShow);
    }

    /**
    * Shows a delete project confirmation dialog, and deletes the current project if suitable.
    */
    @FXML
    private void deleteProject() {

        FXMLLoader popupLoader = new FXMLLoader(getClass().getResource("/Views/PopUpModal.fxml"));
        try {
            BorderPane modal = (BorderPane) popupLoader.load();
            JFXPopup popup = new JFXPopup();
            popup.setContent(modal);

            popup.setPopupContainer(settingsPane);
            popup.setSource(btnDeleteProject);

            popup.show(JFXPopup.PopupVPosition.TOP, JFXPopup.PopupHPosition.LEFT);
            Label header = (Label) modal.lookup("#lblHeader");

            JFXButton btnCancel = (JFXButton) modal.lookup("#btnCancel");
            btnCancel.setOnAction((e) -> popup.close());

            ((JFXButton) modal.lookup("#btnDelete")).
                    setOnAction((event) -> {
                        env.getUserHandler().getCurrentUser().getProjectHandler().deleteProject(env.getUserHandler().getCurrentUser().getProjectHandler().getCurrentProject().projectName);
                        popup.close();
                    });


            header.setText("Are you sure you wish to delete this user?");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
