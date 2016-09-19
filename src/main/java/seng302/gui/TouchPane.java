package seng302.gui;

import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.TouchEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Pair;
import seng302.Environment;
import seng302.data.Note;
import seng302.utility.musicNotation.OctaveUtil;


/**
 * This class is used by each key on the virtual keyboard.
 * Each key is a single touch pane, and registers click/touch events.
 * These events are then dealt with be the KeyboardPaneController.
 */

public class TouchPane extends StackPane {
    private long touchId = -1;
    TouchPane me;
    private String keyLabel;
    private boolean displayLabel = false;
    private boolean displayLabelOnAction = false;
    Note noteToPlay;
    private boolean isblackKey;


    /**
     * Constructor for a single touch pane
     *
     * @param note The note that the touch pane is associated with
     * @param env  The environment in which the touch pane will be located
     * @param kpc  The keyboard pane controller that the touch pane belongs to
     */
    public TouchPane(Integer note, Environment env, KeyboardPaneController kpc) {
        super();
        noteToPlay = Note.lookup(String.valueOf(note));
        final Environment environment = env;
        final KeyboardPaneController keyboardPaneController = kpc;
        me = this;
        setHighlightOff();
        this.setFocusTraversable(false);
        this.setAlignment(Pos.BOTTOM_CENTER);
        this.keyLabel = noteToPlay.getNote();
        this.isblackKey = false;

        // Event handler for on touch
        EventHandler<TouchEvent> touchPress = event -> {
            if (touchId == -1) {
                touchId = event.getTouchPoint().getId();
                if (kpc.playMode().equals("play")) {
                    environment.getPlayer().noteOn(noteToPlay);
                    setHighlightOn();
                } else {
                    if (kpc.playMode().equals("transcript")) {
                        String prev = env.getRootController().getTranscriptController().txtCommand.getText();
                        String newText = prev + " " + this.getNoteValue().getNote();
                        env.getRootController().getTranscriptController().txtCommand.setText(newText);
                    } else if (!(Boolean) env.getCurrentFocussed().getValue() && !((TextField) ((Pair) env.getCurrentFocussed().getKey()).getKey()).isDisabled()) {
                        ((TextField) ((Pair) env.getCurrentFocussed().getKey()).getKey()).setText(OctaveUtil.removeOctaveSpecifier(this.getNoteValue().getNote()));
                        if (((Pair) env.getCurrentFocussed().getKey()).getValue() instanceof TextField) {
                            ((TextField) ((Pair) env.getCurrentFocussed().getKey()).getValue()).requestFocus();
                        } else {
                            ((Button) ((Pair) env.getCurrentFocussed().getKey()).getValue()).fire();
                        }
                    }

                }
            }
            event.consume();
        };

        // Event handler for release of touch
        EventHandler<TouchEvent> touchRelease = event -> {
            if (event.getTouchPoint().getId() == touchId) {
                touchId = -1;
                if (kpc.playMode().equals("play")) {
                    environment.getPlayer().noteOff(noteToPlay);
                    setHighlightOff();
                } else if (kpc.playMode().equals("transcript")) {
                    env.getRootController().getTranscriptController().giveFocus();
                }
            }
            event.consume();
        };

        setOnTouchReleased(touchRelease);
        setOnTouchPressed(touchPress);

        // Event handler for when mouse is released
        setOnMouseReleased(event -> {
            if (kpc.playMode().equals("play")) {
                if (!environment.isShiftPressed()) {
                    environment.getPlayer().noteOff(noteToPlay);
                    setHighlightOff();
                    if (displayLabelOnAction) {
                        getChildren().clear();
                    }
                }
            } else if (kpc.playMode().equals("transcript")) {
                env.getRootController().getTranscriptController().giveFocus();
            }
        });

        // Event handler for when mouse is clicked
        setOnMousePressed(event -> {
            if (!event.isSynthesized()) {
                if (kpc.playMode().equals("play")) {
                    if (environment.isShiftPressed()) {
                        keyboardPaneController.addMultiNote(noteToPlay, me);
                        setHighlightOn();
                    } else {
                        environment.getPlayer().noteOn(noteToPlay);
                        setHighlightOn();
                    }
                    if (displayLabelOnAction) {
                        getChildren().add(new Text(keyLabel));
                    }
                } else {

                    if (kpc.playMode().equals("transcript")) {
                        String prev = env.getRootController().getTranscriptController().txtCommand.getText();
                        String newText = prev + " " + this.getNoteValue().getNote();
                        env.getRootController().getTranscriptController().txtCommand.setText(newText);
                    } else if (!(Boolean) env.getCurrentFocussed().getValue() && !((TextField) ((Pair) env.getCurrentFocussed().getKey()).getKey()).isDisabled()) {
                        ((TextField) ((Pair) env.getCurrentFocussed().getKey()).getKey()).setText(OctaveUtil.removeOctaveSpecifier(this.getNoteValue().getNote()));
                        if (((Pair) env.getCurrentFocussed().getKey()).getValue() instanceof TextField) {
                            ((TextField) ((Pair) env.getCurrentFocussed().getKey()).getValue()).requestFocus();
                        } else {
                            ((Button) ((Pair) env.getCurrentFocussed().getKey()).getValue()).fire();
                        }
                    }
                }
            }
        });

    }

    //public pinchZoom (KeyboardPaneController kpc, Environment env, TouchEvent touch1, TouchEvent touch2) {




//        //Event handler for pinch zooming the size of the keyboard
//        key.setOnZoom(event -> {
//            System.out.println(event.getZoomFactor());
//
//            Integer newTop = Double.valueOf(topNote * event.getZoomFactor()).intValue();
//            if (topNote.equals(0) && event.getZoomFactor() > 1) {
//                newTop = 2;
//            }
//            System.out.println("newTop: " + newTop.toString());
//            Integer newBottom = Double.valueOf(bottomNote * event.getZoomFactor()).intValue();
//            if (bottomNote.equals(0) && event.getZoomFactor() < 1) {
//                newBottom = 2;
//            }
//            System.out.println("newBottom: " + newBottom.toString());
//
//            topNote = Integer.min(newTop, 127);
//            bottomNote = Integer.max(0, newBottom);
//            System.out.println(topNote);
//            System.out.println(bottomNote);
//
//            resetKeyboard();
//            event.consume();
//        });
    //}



    /**
     * Turn click highlight on.
     */
    public void setHighlightOn() {
        this.setStyle("-fx-border-color: black; -fx-border-width: 1px; -fx-background-color: #0093ff");
    }

    /**
     * Turn click highlight off.
     */
    public void setHighlightOff() {
        if (this.isblackKey) {
            this.setStyle("-fx-border-color: black; -fx-border-width: 1px; -fx-background-color: black");
        } else {
            this.setStyle("-fx-border-color: black; -fx-border-width: 1px; -fx-background-color: white");
        }
        if (displayLabelOnAction) {
            getChildren().clear();
        }
    }

    /**
     * displays the keynote name on the keyboard when display label is selected
     */
    public void toggleDisplayLabel() {
        if (displayLabelOnAction) {
            toggleDisplayLabelOnAction();
        }
        if (displayLabel) {
            displayLabel = false;
            this.getChildren().clear();
        } else {
            displayLabel = true;
            this.getChildren().add(new Text(keyLabel));
        }

    }

    /**
     * displays a shape corresponding to a particular scale on the keys. Used by
     * toggleScaleKeys() in KeyBoardPaneController
     * @param imageType: Detemines whether it is a circle or rectangle
     * @param imageId: gives the image an ID so it can be associated with a scale/startnote/other note
     */
    public void toggleScaleNotes(String imageType, String imageId) {
        Circle blueCircle = new Circle();
        blueCircle.setRadius(5.0f);
        blueCircle.setFill(Color.BLUE);

        Circle greenCircle = new Circle();
        greenCircle.setRadius(5.0f);
        greenCircle.setFill(Color.GREEN);
        greenCircle.setTranslateY(-20);

        Rectangle blueRectangle = new Rectangle();
        blueRectangle.setWidth(10);
        blueRectangle.setHeight(10);
        blueRectangle.setFill(Color.BLUE);

        Rectangle greenRectangle = new Rectangle();
        greenRectangle.setWidth(10);
        greenRectangle.setHeight(10);
        greenRectangle.setFill(Color.GREEN);
        greenRectangle.setTranslateY(-20);


        //checks the image type and matches it to the corresponding shape
        if (imageType.equals("blueCircle")) {
            blueCircle.setId(imageId);
            this.getChildren().add(blueCircle);
        } else if (imageType.equals("greenCircle")) {
            greenCircle.setId(imageId);
            this.getChildren().add(greenCircle);
        } else if (imageType.equals("blueRectangle")) {
            blueRectangle.setId(imageId);
            this.getChildren().add(blueRectangle);
        } else if (imageType.equals("greenRectangle")) {
            greenRectangle.setId(imageId);
            this.getChildren().add(greenRectangle);
        }

    }

    /**
     * Searches for and removes all children with the given ID This ID belongs to scale indicator
     * images
     *
     * @param id Either "firstScale" or "secondScale" - the scale indicators to be removed.
     */
    public void removeScaleImage(String id) {
        while (this.getChildren().contains(this.lookup("#" + id))) {
            this.getChildren().removeAll(this.lookup("#" + id));
        }
    }

    /**
     * displays the label of the key on click when show on click is selected
     */

    public void toggleDisplayLabelOnAction() {
        if (displayLabel) {
            toggleDisplayLabel();
        }
        if (displayLabelOnAction) {
            displayLabelOnAction = false;
            this.getChildren().clear();
        } else {
            displayLabelOnAction = true;
        }
    }

    public void stopDisplayNotes() {
        if (displayLabel) {
            toggleDisplayLabel();
        }
        if (displayLabelOnAction) {
            toggleDisplayLabelOnAction();
        }

    }

    public Note getNoteValue() {
        return noteToPlay;
    }

    public void setBlackKey(boolean value) {
        this.isblackKey = value;
    }

}
