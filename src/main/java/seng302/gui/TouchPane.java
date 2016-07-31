package seng302.gui;

import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.input.TouchEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import seng302.Environment;
import seng302.data.Note;

/**
 * Created by isabelle on 13/05/16.
 */

public class TouchPane extends StackPane {
    private long touchId = -1;
    TouchPane me;
    private String keyLabel;
    private boolean displayLabel = false;
    private boolean displayLabelOnAction = false;
    Note noteToPlay;
    private boolean isblackKey;

    // Used to avoid the bug where both a touch and a click is registered
    private boolean isTouch;


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
            isTouch = true;
            if (touchId == -1) {
                touchId = event.getTouchPoint().getId();
                if (kpc.isPlayMode()) {
                    environment.getPlayer().noteOn(noteToPlay);
                    setHighlightOn();
                } else {
                    String prev = env.getRootController().getTranscriptController().txtCommand.getText();
                    String newText = prev + " " + this.getNoteValue().getNote();
                    env.getRootController().getTranscriptController().txtCommand.setText(newText);
                }
            }
            event.consume();
        };

        // Event handler for release of touch
        EventHandler<TouchEvent> touchRelease = event -> {
            isTouch = false;
            if (event.getTouchPoint().getId() == touchId) {
                touchId = -1;
                if (kpc.isPlayMode()) {
                    environment.getPlayer().noteOff(noteToPlay);
                    setHighlightOff();
                } else {
                    env.getRootController().getTranscriptController().giveFocus();
                }
            }
            event.consume();
        };

        setOnTouchReleased(touchRelease);
        setOnTouchPressed(touchPress);

        // Event handler for when mouse is released
        setOnMouseReleased(event -> {
            if (kpc.isPlayMode()) {
                if (!environment.isShiftPressed()) {
                    environment.getPlayer().noteOff(noteToPlay);
                    setHighlightOff();
                    if (displayLabelOnAction) {
                        getChildren().clear();
                    }
                }
            } else {
                env.getRootController().getTranscriptController().giveFocus();
            }
        });

        // Event handler for when mouse is clicked
        setOnMousePressed(event -> {
            if (!isTouch) {
                if (kpc.isPlayMode()) {
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
                    String prev = env.getRootController().getTranscriptController().txtCommand.getText();
                    String newText = prev + " " + this.getNoteValue().getNote();
                    env.getRootController().getTranscriptController().txtCommand.setText(newText);
                }
            }
        });

    }

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
