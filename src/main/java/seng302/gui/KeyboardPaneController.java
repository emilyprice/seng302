package seng302.gui;

import com.jfoenix.controls.JFXButton;

import org.controlsfx.control.PopOver;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import seng302.Environment;
import seng302.data.Note;
import seng302.utility.NoteRangeSlider;
import seng302.utility.musicNotation.OctaveUtil;

import static seng302.utility.musicNotation.Checker.isValidNormalNote;

/**
 * Created by team 5 on 13/05/16.
 */
public class KeyboardPaneController {

    /**
     * Contains the keyboardBox (white keys) and the blackKeys AnchorPane.
     */
    @FXML
    private StackPane keyboardStack;

    /**
     * Contains the white keys (TouchPanes).
     */
    @FXML
    private HBox keyboardBox;


    /**
     * Contains the black keys (TouchPanes).
     */
    @FXML
    private AnchorPane blackKeys;

    /**
     * Button to show settings pop over.
     */
    @FXML
    private Button settingsButton;

    /**
     * Button to show display scales pop over.
     */
    @FXML
    private Button displayScalesButton;


    @FXML
    private StackPane rightStack;

    @FXML
    private StackPane scalesStackPane;

    /**
     * Current Environment (to access music player etc.).
     */
    private Environment env;

    /**
     * Settings pop over.
     */
    private PopOver pop;

    /**
     * Display Scales pop over
     */
    private PopOver displayScalesPop;

    /**
     * Bottom note of keyboard.
     */
    private Integer bottomNote;

    /**
     * Top note of keyboard.
     */
    private Integer topNote;


    /**
     * Radio button to always show note labels.
     */
    private RadioButton noteLabelsAlways;

    /**
     * Radio button to show note labels on click.
     */
    private RadioButton noteLabelsClick;

    /**
     * Radio button to turn off note labels.
     */
    private RadioButton noteLabelsOff;


    /**
     * Current notes that have been clicked, since shift key was held.
     */
    private List<Note> multiNotes;

    /**
     * Current keys that have been clicked, since the shift key was held.
     */
    private List<TouchPane> clicked;

    @FXML
    private TitledPane keyPane;

    private String playMode;


    /**
     * Set up keyboard, by creating settings popOver and TouchPanes(keys) for the keyboard.
     */
    @FXML
    private void initialize() {
        keyboardBox.setMaxHeight(200);
        keyboardBox.setMinHeight(200);
        blackKeys.setMaxHeight(130);
        playMode = "play";
        HBox.setHgrow(rightStack, Priority.ALWAYS);

        // Picking is computed by intersecting with the geometric
        // shape of the node, instead of the bounds.
        keyboardStack.setPickOnBounds(false);

        // Default note range when keyboard opens.
        bottomNote = 48;
        topNote = 72;


        createSettingsPop();
        createDisplayScalesPop();

        keyPane.expandedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                positionBlackKeys();
            }
        });

    }

    /**
     * Setup the settings pop over - with title, keyboard range slider, note labels radio buttons.
     */
    private void createSettingsPop() {
        // VBox goes inside settings pop over.
        VBox settings = new VBox();

        // HBox for the title and the dynamic keyboard range label.
        HBox rangeHeading = new HBox();
        rangeHeading.setSpacing(5);
        Label keyboardRange = new Label("Keyboard Range:");
        rangeHeading.getChildren().add(keyboardRange);

        // Add to the settings VBox.
        settings.getChildren().add(rangeHeading);

        // Dynamic label for range of notes currently showing.
        Label notes = new Label("");

        // Slider to select note range to show.
        NoteRangeSlider slider = new NoteRangeSlider(notes, 12, bottomNote, topNote);

        // Add to the settings VBox
        settings.getChildren().add(slider);


        // Style settings button.
        Image cog = new Image(getClass().getResourceAsStream
                ("/images/gear-1119298_960_720.png"), 10, 10, true, true);
        settingsButton.setGraphic(new ImageView(cog));
        settingsButton.setText(null);


        // Select whether to show note names.
        settings.getChildren().add(new Label("Note names:"));
        ToggleGroup notenames = new ToggleGroup();
        noteLabelsOff = new RadioButton("Never show");
        noteLabelsOff.setOnAction(event -> stopShowingNotesOnKeyboard());
        noteLabelsOff.setToggleGroup(notenames);
        noteLabelsOff.setSelected(true);
        noteLabelsClick = new RadioButton("Show on click");
        noteLabelsClick.setToggleGroup(notenames);
        noteLabelsClick.setOnAction(event -> toggleShowKeyboardNotesAction());
        noteLabelsAlways = new RadioButton("Always show");
        noteLabelsAlways.setToggleGroup(notenames);
        noteLabelsAlways.setOnAction(event -> toggleShowKeyboardNotesAlways());

        // Generate Keyboard notes based on value of range slider.
        slider.lowValueProperty().addListener(
                (observable, oldValue, newValue) -> {
                    bottomNote = newValue.intValue();
                    resetKeyboard();
                }
        );

        slider.highValueProperty().addListener(
                (observable, oldValue, newValue) -> {
                    topNote = newValue.intValue();
                    resetKeyboard();
                }
        );

        rangeHeading.getChildren().add(notes);

        settings.setSpacing(5);
        settings.setPadding(new Insets(10));


        pop = new PopOver(settings);
        pop.setTitle("Keyboard Settings");


        settings.getChildren().add(noteLabelsOff);
        settings.getChildren().add(noteLabelsClick);
        settings.getChildren().add(noteLabelsAlways);

        final ToggleGroup group = new ToggleGroup();
        HBox modes = new HBox();
        ToggleButton play = new ToggleButton("Play");
        play.setUserData("play");
        play.setToggleGroup(group);
        play.setSelected(true);

        ToggleButton transcriptInp = new ToggleButton("Transcript Input");
        transcriptInp.setUserData("transcript");
        transcriptInp.setToggleGroup(group);

        ToggleButton tutorInp = new ToggleButton("Tutor Input");
        tutorInp.setUserData("tutor");
        tutorInp.setToggleGroup(group);

        group.selectedToggleProperty().addListener((observable, newValue, oldValue) -> {
            if (group.getSelectedToggle() == null) {
                play.setSelected(true);
            } else {
                playMode = (String) group.getSelectedToggle().getUserData();
            }
        });


        settings.getChildren().add(new Label("Keyboard Mode:"));
        modes.getChildren().add(play);
        modes.getChildren().add(tutorInp);
        modes.getChildren().add(transcriptInp);
        settings.getChildren().add(modes);

    }



    /**
     * display scales pop up option on keyboard. Will enable user to display 1 or 2 scales. They can select
     * note of scale, what type of scale and its octave. Clear buttons for each scale to clear fields.
     * OK button to confirm and execute and close window. Cancel button to cancel and close window.
     * Error handling for invalid inputs
     */
    private void createDisplayScalesPop() {

        // Vbox goes inside display scales pop over
        VBox displayScales = new VBox();
        // Hbox for label of scale 1
        HBox scale1Label = new HBox();
        scale1Label.setSpacing(5);
        Label selectScale1Label = new Label("Select scale:");
        scale1Label.getChildren().add(selectScale1Label);

        // Hbox for scale 1
        HBox scale1 = new HBox();
        scale1.setSpacing(5);

        // Hbox for label of scale 2
        HBox scale2Label = new HBox();
        scale2Label.setSpacing(5);
        Label selectScale2Label = new Label("Select 2nd scale (optional):");
        scale2Label.getChildren().add(selectScale2Label);

        // Hbox for scale 2
        HBox scale2 = new HBox();
        scale2.setSpacing(5);


        //text input field so user can type in the note for the scale of interest.
        //for scale 1
        TextField scale1NoteInput = new TextField();
        scale1NoteInput.setPrefColumnCount(3); //setting col size (user can input 3 characters)

        //for optional scale 2
        TextField scale2NoteInput = new TextField();
        scale2NoteInput.setPrefColumnCount(3); // setting column size (user can input 3 characters)

        //The scales available
        ObservableList<String> typeOptions =
                FXCollections.observableArrayList(
                        "Major",
                        "Minor",
                        "Melodic Minor",
                        "Major Pentatonic",
                        "minor Pentatonic"


                );

        // HBox for the options of the first scale
        HBox scaleOneTypeOptions = new HBox();

        //drop down for type of first scale
        ComboBox<String> typeScale1 = new ComboBox(typeOptions);
        typeScale1.setValue("Major"); //setting major as the default value
        scaleOneTypeOptions.getChildren().add(typeScale1);


        //HBox for the options of the optional second scale
        HBox scaleTwoTypeOptions = new HBox();


        //drop down for type of first scale
        ComboBox<String> typeScale2 = new ComboBox(typeOptions);
        typeScale2.setValue("Major"); //setting major as the default value
        scaleTwoTypeOptions.getChildren().add(typeScale2);

        // Hbox for label of start note key
        HBox startNoteKey = new HBox();
        Label startKey = new Label("  Start note of scale");
        Rectangle startNoteKeySymbol = new Rectangle();
        startNoteKeySymbol.setWidth(15);
        startNoteKeySymbol.setHeight(15);
        startNoteKeySymbol.setFill(Color.BLACK);
        startNoteKey.getChildren().add(startNoteKeySymbol);
        startNoteKey.getChildren().add(startKey);


        //Hbox for label of other note key
        HBox otherNoteKey = new HBox();
        Circle otherNoteKeySymbol = new Circle();
        otherNoteKeySymbol.setCenterX(100.0f);
        otherNoteKeySymbol.setCenterY(100.0f);
        otherNoteKeySymbol.setRadius(7.0f);
        otherNoteKeySymbol.setFill(Color.BLACK);
        Label otherKey = new Label("  Other notes of scale");
        otherNoteKey.getChildren().add(otherNoteKeySymbol);
        otherNoteKey.getChildren().add(otherKey);




        //OK button for display scale 1. Contains all of the error handling and toggles to Hide when clicked.
        //OK displays the given scale on the keyboard. Hide removes them but doesnt clear the input fields
        JFXButton okScale1 = new JFXButton("OK");
        okScale1.getStyleClass().add("primary");
        okScale1.setOnAction(event-> {
            if (okScale1.getText().equals("OK")) {
                String scale1Note = scale1NoteInput.getText();
                String scale2Note = scale2NoteInput.getText();
                boolean isValidNote1 = isValidNormalNote(scale1Note);
                boolean isUnique = scaleIsUnique(scale1Note, typeScale1.getValue(), scale2Note, typeScale2.getValue());


                if (scale1Note != null && !scale1Note.equals("") && isValidNote1 && isUnique) {
                    ArrayList<Note> scale1Notes = fetchScaleNotes(scale1NoteInput.getText(), typeScale1.getValue());
                    toggleScaleKeys(scale1Notes, true); //displays pic on first key of scale
                    scale1NoteInput.setStyle("-fx-border-color: lightgray;"); //defaults border colour incase it was red
                    scale2NoteInput.setStyle("-fx-border-color: lightgray;");
                    okScale1.setText("Hide");
                } else if (scale1Note.equals(scale2Note)) {
                    //if scale 1 input matches scale 2 input
                    scale1NoteInput.setStyle("-fx-border-color: red;");
                    scale2NoteInput.setStyle("-fx-border-color: red;");
                } else {
                    //if scale 1 was given no input
                    scale1NoteInput.setStyle("-fx-border-color: red;");
                }
            } else if (okScale1.getText().equals("Hide")) {
                scale1NoteInput.setStyle("-fx-border-color: lightgray;");
                clearScaleIndicators("firstScale");
                okScale1.setText("OK");

            }

                });


        //OK button for display scale 2. Contains all of the error handling and toggles to Hide when clicked.
        //OK displays the given scale on the keyboard. Hide removes them but doesnt clear the input fields
        JFXButton okScale2 = new JFXButton("OK");
        okScale2.getStyleClass().add("primary");
        okScale2.setOnAction(event-> {
            if (okScale2.getText().equals("OK")) {
                String scale1Note = scale1NoteInput.getText();
                String scale2Note = scale2NoteInput.getText();
                boolean isValidNote2 = isValidNormalNote(scale2Note);
                boolean isUnique = scaleIsUnique(scale1Note, typeScale1.getValue(), scale2Note, typeScale2.getValue());


                if (scale2Note != null && !scale2Note.equals("") && isValidNote2 && isUnique) {
                    ArrayList<Note> scale2Notes = fetchScaleNotes(scale2NoteInput.getText(), typeScale2.getValue());
                    toggleScaleKeys(scale2Notes, false); //displays pic on first key of scale
                    scale2NoteInput.setStyle("-fx-border-color: lightgray;"); //defaults border colour incase it was red
                    scale1NoteInput.setStyle("-fx-border-color: lightgray;");
                    okScale2.setText("Hide");
                } else if (scale2Note.equals(scale1Note)) {
                    //if scale 1 input matches scale 2 input
                    scale1NoteInput.setStyle("-fx-border-color: red;");
                    scale2NoteInput.setStyle("-fx-border-color: red;");


                } else {
                    //if scale 2 was given no input
                    scale2NoteInput.setStyle("-fx-border-color: red;");
                }
            } else if (okScale2.getText().equals("Hide")) {
                scale2NoteInput.setStyle("-fx-border-color: lightgray;");
                clearScaleIndicators("secondScale");
                okScale2.setText("OK");

        }
        });


        //Clears all inputs and removes all scale indicators back to default. resets the input text borders to default
        //and resets the button back to the OK state
        Button cancelButton = new Button("Reset Scales");
        cancelButton.setOnAction(event->{
            scale1NoteInput.clear();
            typeScale1.setValue("Major");
            scale1NoteInput.setStyle("-fx-border-color: lightgray;");


            scale2NoteInput.clear();
            typeScale2.setValue("Major");
            scale2NoteInput.setStyle("-fx-border-color: lightgray;");


            okScale1.setText("OK");
            okScale2.setText("OK");

            clearScaleIndicators("firstScale");
            clearScaleIndicators("secondScale");

        });
        cancelButton.getStyleClass().add("secondary");


        //Symbol to indicate the colour for scale 1
        Circle scale1Key = new Circle();
        scale1Key.setCenterX(100.0f);
        scale1Key.setCenterY(100.0f);
        scale1Key.setRadius(5.0f);
        scale1Key.setFill(Color.BLUE);

        //Symbol to inidicate the colour for scale 2
        Circle scale2Key = new Circle();
        scale2Key.setCenterX(100.0f);
        scale2Key.setCenterY(200.0f);
        scale2Key.setRadius(5.0f);
        scale2Key.setFill(Color.GREEN);


        //HBox for the OK and Cancel button
        HBox actionButtonBox = new HBox();
        actionButtonBox.getChildren().add(cancelButton);

        // add note input field, type drop down and clear button to scale 1 HBox
        scale1.getChildren().add(scale1Key);
        scale1.getChildren().add(scale1NoteInput);
        scale1.getChildren().add(scaleOneTypeOptions);
        scale1.getChildren().add(okScale1);

        // add note input field, type drop down and clear button to scale 1 HBox
        scale2.getChildren().add(scale2Key);
        scale2.getChildren().add(scale2NoteInput);
        scale2.getChildren().add(scaleTwoTypeOptions);
        scale2.getChildren().add(okScale2);


        // Add Hboxes to the display scales vbox
        displayScales.getChildren().add(actionButtonBox);
        displayScales.getChildren().add(scale1Label);
        displayScales.getChildren().add(scale1);
        displayScales.getChildren().add(scale2Label);
        displayScales.getChildren().add(scale2);
        displayScales.getChildren().add(startNoteKey);
        displayScales.getChildren().add(otherNoteKey);




        // used the spacing etc from settings to see if it will come out nicely. Subject to change
        displayScales.setSpacing(10);
        displayScales.setPadding(new Insets(10));

        //Declaring the popover
        displayScalesPop = new PopOver(displayScales);
        displayScalesPop.setTitle("Display Scales");
        displayScalesPop.setOnHiding(event -> {
            displayScalesButton.setText("Display Scales");
        });


    }

    /**
     * Clear scale images of either the first or second scale from the keyboard
     *
     * @param scaleId Whether we are clearing the first or second scale
     */
    private void clearScaleIndicators(String scaleId) {
        //clear the images off the keys
        ObservableList<Node> keys = keyboardBox.getChildren();
        for (Node key : keys) {
            if (key instanceof TouchPane) {
                ((TouchPane) key).removeScaleImage(scaleId);
            }
        }

        ObservableList<Node> bKeys = blackKeys.getChildren();
        for (Node key : bKeys) {
            if (key instanceof TouchPane) {
                ((TouchPane) key).removeScaleImage(scaleId);
            }
        }
    }

    /**
     * Takes the scale note specified in the text input field and option from drop down menu in Display Scales
     * and fetches the notes of the relevant scale
     */
    private ArrayList<Note> fetchScaleNotes(String scaleNote, String scaleType) {
        scaleNote = OctaveUtil.addDefaultOctave(scaleNote);
        Note scaleStartNote = Note.lookup(scaleNote);
        ArrayList<Note> scaleNotes = scaleStartNote.getScale(scaleType, true);
        return scaleNotes;

    }

    /**
     * Compares one scale against another to check to see if they are identical
     * @param scale1Note
     * @param scale1Type
     * @param scale2Note
     * @param scale2Type
     * @return boolean
     */

    private boolean scaleIsUnique(String scale1Note, String scale1Type, String scale2Note, String scale2Type) {
        if (scale1Note.equals(scale2Note) && scale1Type.equals(scale2Type)) {
            return false;
        } else {
            return true;
        }


    }


    /**
     * Each time the keyboard notes are change the keyboard needs to be remade, black keys
     * repositioned and labels added if the setting is on.
     */
    private void resetKeyboard() {
        setUpKeyboard();
        positionBlackKeys();
        checkLabelStatusForNewNotes();
    }

    /**
     * When a note is created, check if it should be showing a label and toggle as needed.
     */
    private void checkLabelStatusForNewNotes() {
        if (noteLabelsAlways.isSelected()) {
            toggleShowKeyboardNotesAlways();
        } else if (noteLabelsClick.isSelected()) {
            toggleShowKeyboardNotesAction();
        }
    }

    /**
     * Hides and shows the settings pop over when the settings Button is pressed.
     */
    @FXML
    private void toggleSettings() {
        if (pop.isShowing()) {
            pop.hide();
        } else {
            pop.show(settingsButton);
        }
    }

    /**
     * Hides and shows the display scales pop over when the display scales Button is pressed.
     */
    @FXML
    private void toggleDisplayScales() {
        if (displayScalesPop.isShowing()) {
            displayScalesPop.hide();
            displayScalesButton.setText("Display Scales");
        } else {
            displayScalesPop.show(displayScalesButton);
            displayScalesButton.setText("Hide Display Scales");
        }
    }

    /**
     * Sets environment and initialises things.
     *
     * @param env Environment of the application.
     */
    public void create(Environment env) {
        this.env = env;
        toggleHideKeyboard();
        multiNotes = new ArrayList<>();
        clicked = new ArrayList<>();
        Platform.runLater(() -> {
            setUpKeyboard();
            keyboardStack.requestFocus();
            positionBlackKeys();
        });


        // This change listener is registered to the width property of the application.
        // The task and timer scheduling is required because calling positionBlackKeys() for every
        // change to the widthProperty caused the resizing to lag, and be inaccurate.
        // Now it only repositions the black keys if the width has not changed in the last 50
        // milliseconds.
        final ChangeListener<Number> listener = new ChangeListener<Number>() {
            final Timer timer = new Timer(); // uses a timer to call the resize method
            TimerTask task = null; // task to execute after defined delay
            final long delayTime = 50; // delay that has to pass in order to consider an operation done

            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, final Number newValue) {
                if (task != null) { // there was already a task scheduled from the previous operation ...
                    task.cancel(); // cancel it, we have a new size to consider
                }

                task = new TimerTask() // create new task that calls the resize operation
                {
                    @Override
                    public void run() {
                        positionBlackKeys();
                    }
                };
                // schedule new task
                timer.schedule(task, delayTime);
            }
        };

        // Register the listener.
        this.env.getRootController().paneMain.widthProperty().addListener(listener);
        this.env.shiftPressedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                env.getPlayer().playSimultaneousNotes(multiNotes);
                multiNotes.clear();
                for (TouchPane clickedPane : clicked) {
                    clickedPane.setHighlightOff();
                }
                clicked.clear();
            }
        });

    }

    /**
     * Create each key from bottomNote to topNote.
     */
    private void setUpKeyboard() {
        keyboardBox.getChildren().clear();
        keyboardBox.setFocusTraversable(false);
        blackKeys.getChildren().clear();
        blackKeys.setFocusTraversable(false);

        for (Integer i = bottomNote; i <= topNote; i++) {
            TouchPane key = new TouchPane(i, env, this);
            key.setMaxWidth(Double.MAX_VALUE);
            key.setPrefSize(100, 200);
            if (Note.lookup(i.toString()).getNote().contains("#")) {
                key.setBlackKey(true);
                key.setHighlightOff();
                key.setPrefSize(100, 130);
                AnchorPane.setLeftAnchor(key, 0.0);
                blackKeys.getChildren().add(key);
            } else {
                HBox.setHgrow(key, Priority.ALWAYS);
                keyboardBox.getChildren().add(key);
            }

        }

    }


    /**
     * Position black keys dynamically according to the width of the white keys.
     */
    private void positionBlackKeys() {
        Double width = keyboardBox.getWidth();
        ObservableList<Node> keys = blackKeys.getChildren();
        Integer numWhiteNotes = topNote - bottomNote - keys.size() + 1;
        Double whiteWidth = width / (double) numWhiteNotes;
        Double blackWidth = whiteWidth / 2.0;
        Integer prevBlackKey = -1;
        Double currentPos = 0.0;
        for (Node key : keys) {
            TouchPane keyPane = (TouchPane) key;
            //keyPane.getStyleClass().add("blackkey");
            keyPane.setPrefWidth(blackWidth);
            keyPane.setMinWidth(blackWidth);
            keyPane.setMaxWidth(blackWidth);
            if (prevBlackKey == -1) {
                prevBlackKey = keyPane.getNoteValue().getMidi();
                if (prevBlackKey - bottomNote == 1) {
                    AnchorPane.setLeftAnchor(key, whiteWidth * 0.75);
                    currentPos = whiteWidth * 0.75 + blackWidth;
                } else if (prevBlackKey - bottomNote == 2) {
                    AnchorPane.setLeftAnchor(key, whiteWidth * 1.75);
                    currentPos = whiteWidth * 1.75 + blackWidth;
                } else {
                    AnchorPane.setLeftAnchor(key, -blackWidth * 0.5);
                    currentPos = blackWidth * 0.5;
                }
            } else {
                if (keyPane.getNoteValue().getMidi() - prevBlackKey == 2) {
                    AnchorPane.setLeftAnchor(key, currentPos + whiteWidth * 0.5);
                    currentPos = currentPos + whiteWidth * 0.5 + blackWidth;
                } else if (keyPane.getNoteValue().getMidi() - prevBlackKey == 3) {
                    AnchorPane.setLeftAnchor(key, currentPos + whiteWidth * 1.5);
                    currentPos = currentPos + whiteWidth * 1.5 + blackWidth;
                }
                prevBlackKey = keyPane.getNoteValue().getMidi();
            }

        }

    }


    /**
     * Add a note to the current set of notes pressed, while shift is pressed.
     *
     * @param note The note
     * @param pane The key
     */
    public void addMultiNote(Note note, TouchPane pane) {
        multiNotes.add(note);
        clicked.add(pane);
    }

    /**
     * Hide or show the keyboard.
     */
    public void toggleHideKeyboard() {
        if (keyPane.isExpanded()) {
            positionBlackKeys();
            keyPane.setExpanded(false);
            keyboardBox.requestFocus();
        } else {
            positionBlackKeys();
            keyPane.setExpanded(true);
            env.getRootController().getTranscriptController().giveFocus();
        }
    }

    /**
     * Show/hide labels on keyboard notes.
     */
    public void toggleShowKeyboardNotesAlways() {
        ObservableList<Node> keys = keyboardBox.getChildren();
        for (Node key : keys) {
            if (key instanceof TouchPane) {
                ((TouchPane) key).toggleDisplayLabel();
            }
        }
    }

    /**
     * Adds a relevant image to a key, if it is equal to the given note.
     * @param key The key we are potentially altering
     * @param currentNoteString The note we are looking for
     * @param isFirstScale Whether this is scale 1 or scale 2
     * @param isStartNote whether or not "currentNoteString" is the first note in the scale
     */
    private void addVisual(Node key, String currentNoteString, boolean isFirstScale, boolean isStartNote) {
        String firstScaleImage = "";
        String secondScaleImage = "";
        if (key instanceof TouchPane) {
            String keyString = ((TouchPane) key).getNoteValue().getNote();
            keyString = OctaveUtil.removeOctaveSpecifier(keyString);

            if (keyString.equals(currentNoteString)) {
                if (isFirstScale) {
                    if (isStartNote) {
                        firstScaleImage = "blueRectangle";
                    } else {
                        firstScaleImage = "blueCircle";
                    }
                    ((TouchPane) key).toggleScaleNotes(firstScaleImage, "firstScale");
                } else {
                    if (isStartNote) {
                        secondScaleImage = "greenRectangle";
                    } else {
                        secondScaleImage = "greenCircle";
                    }
                    ((TouchPane) key).toggleScaleNotes(secondScaleImage, "secondScale");
                }
            }
        }
    }
    /**
     * Show/Hide scale visualizations on the keyboard
     * @param scaleNotes: an array that contains all of the notes of a scale
     * @param isFirstScale: determines whether the scale is scale 1 or scale 2
     */
    public void toggleScaleKeys(ArrayList<Note> scaleNotes, Boolean isFirstScale) {
        ObservableList<Node> whiteKeys = keyboardBox.getChildren();
        ObservableList<Node> bKeys = blackKeys.getChildren();
        Boolean isStartNote;


        for (int i = 0; i < scaleNotes.size() - 1; i++) {
            //Gets all of the notes from the scale and turns them into strings to be compared
            Note currentNote = scaleNotes.get(i);
            String currentNoteString = currentNote.getNote();
            currentNoteString = OctaveUtil.removeOctaveSpecifier(currentNoteString);

            //Determines whether or not we are working with the 'start note' of a scale
            if (i == 0) {
                isStartNote = true;
            } else {
                isStartNote = false;
            }

            //iterates through the white keys for the rest of the scale notes
            for (Node whiteKey : whiteKeys) {
                addVisual(whiteKey, currentNoteString, isFirstScale, isStartNote);
            }

            //iterates through the black keys for the rest of the scale notes
            for (Node blackKey : bKeys) {
                addVisual(blackKey, currentNoteString, isFirstScale, isStartNote);
            }

        }
    }

    /**
     * Show/hide labels on keyboard notes when clicked.
     */
    public void toggleShowKeyboardNotesAction() {
        ObservableList<Node> keys = keyboardBox.getChildren();
        for (Node key : keys) {
            if (key instanceof TouchPane) {
                ((TouchPane) key).toggleDisplayLabelOnAction();
            }
        }
    }

    /**
     * Stop showing labels on notes.
     */
    public void stopShowingNotesOnKeyboard() {
        ObservableList<Node> keys = keyboardBox.getChildren();
        for (Node key : keys) {
            if (key instanceof TouchPane) {
                ((TouchPane) key).stopDisplayNotes();
            }
        }
    }

    public String playMode() {
        return playMode;
    }

    /**
     * Used for visualisation. Finds the key representing the specified midi number, and turns it
     * blue.
     *
     * @param midiValue The value for which key will be turned blue
     */
    public void highlightKey(int midiValue) {
        String colourName;
        try {
            colourName = env.getThemeHandler().getPrimaryColour();
        } catch (Exception e) {
            colourName = "blue";
        }
        String whiteStyle = "-fx-border-color: black; -fx-border-width: 1px; -fx-background-color: " + colourName;
        String blackStyle = "-fx-border-color: black; -fx-border-width: 1px; -fx-background-color: " + colourName;
        ObservableList<Node> keys = keyboardBox.getChildren();
        for (Node key : keys) {
            if (key instanceof TouchPane && ((TouchPane) key).getNoteValue().getMidi() == midiValue) {
                key.setStyle(whiteStyle);
            }
        }

        ObservableList<Node> bKeys = blackKeys.getChildren();
        for (Node key : bKeys) {
            if (key instanceof TouchPane && ((TouchPane) key).getNoteValue().getMidi() == midiValue) {
                key.setStyle(blackStyle);
            }
        }

    }

    /**
     * Used for visualisation. Finds the key representing the specified midi number,
     * and turns the visualiser highlight off.
     * @param midiValue The value for which key will be turned blue
     */
    public void removeHighlight(int midiValue) {
        String whiteStyle = "-fx-border-color: black; -fx-border-width: 1px; -fx-background-color: white";
        String blackStyle = "-fx-border-color: black; -fx-border-width: 1px; -fx-background-color: black";
        ObservableList<Node> keys = keyboardBox.getChildren();
        for (Node key : keys) {
            if (key instanceof TouchPane && ((TouchPane) key).getNoteValue().getMidi() == midiValue) {
                key.setStyle(whiteStyle);
            }
        }

        ObservableList<Node> bKeys = blackKeys.getChildren();
        for (Node key : bKeys) {
            if (key instanceof TouchPane && ((TouchPane) key).getNoteValue().getMidi() == midiValue) {
                key.setStyle(blackStyle);
            }
        }

    }
}
