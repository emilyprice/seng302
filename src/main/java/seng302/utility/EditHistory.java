package seng302.utility;

import java.util.ArrayList;

import seng302.Environment;
import seng302.command.InstrumentCommand;
import seng302.command.MusicalTerm;
import seng302.command.Rhythm;
import seng302.command.Tempo;

/**
 * Undo/redo history manager for the application Created by team 5 on 24/04/16.
 */
public class EditHistory {
    // Stack of commands to undo/redo operations
    private ArrayList<ArrayList<String>> commandStack = new ArrayList<ArrayList<String>>();
    private int location = 0;
    // Set true when there are actions that can be undone
    private boolean canUndo = false;
    // Set true when there are actions that can be redone
    private boolean canRedo = false;
    // Set true when something should be added
    private boolean ath = true;

    // Environment that the edit history is bound to. Essential for running the operations
    // necessary for undoing/redoing commands.
    private Environment env;

    /**
     * Constructor for the edit history. Takes the environment as a parameter which allows it to run
     * commands that have previously been executed.
     */
    public EditHistory(Environment env) {
        this.env = env;
    }

    /**
     * Function that allows the addition of commands into the edit history.
     *
     * @param type   Type of command that is being inserted into the history.
     * @param effect Contains information that can be used to replicate effect - either undo or
     *               redo.
     */
    public void addToHistory(String type, ArrayList<String> effect) {
        if (location > 0 && ath) {
            for (int i = 0; i < location; i++) {
                commandStack.remove(0);
            }
            ArrayList<String> toAdd = new ArrayList<String>();
            toAdd.add(type);
            for (String val : effect) {
                toAdd.add(val);
            }
            commandStack.add(0, toAdd);
            canUndo = true;
            canRedo = false;
            location = 0;
        } else if (ath) {
            ArrayList<String> toAdd = new ArrayList<String>();
            toAdd.add(type);
            for (String val : effect) {
                toAdd.add(val);
            }
            commandStack.add(0, toAdd);
            canUndo = true;
            canRedo = false;
            location = 0;
        }
    }


    /**
     * Called to undo a command. Checks the stack of commands to see which one it is required to
     * execute and then performs the required operation. Prints an error using the environment's
     * error system if it is unable to print an error.
     */
    public void undoCommand() {
        if (canUndo) {
            ath = false;
            int type = Integer.parseInt(commandStack.get(location).get(0));
            switch (type) {
                case 0:
                    changeTempo(commandStack.get(location).get(1));
                    break;
                case 1:
                    deleteMusicalTerm(commandStack.get(location).get(1));
                    break;

                case 2:
                    changeRhythm(commandStack.get(location).get(1));
                    break;
                case 3:
                    undoTranscriptClear();
                    break;
                case 4:
                    //undo change instrument
                    changeInstrument(commandStack.get(location).get(1));
                    break;
            }
            ath = true;
            location += 1;
            if (location > commandStack.size() - 1) {
                canUndo = false;
            }
            canRedo = true;
        } else {
            env.error("No command to undo.");
        }
    }

    /**
     * Called to redo a command. Checks the stack of commands to see which one it is required to
     * execute and then performs the required operation. Prints an error using the environment's
     * error system if it is unable to print an error.
     */
    public void redoCommand() {
        if (canRedo) {
            int type = Integer.parseInt(commandStack.get(location - 1).get(0));
            ath = false;
            switch (type) {
                case 0:
                    changeTempo(commandStack.get(location - 1).get(2));
                    break;
                case 1:
                    addMusicalTerm(commandStack.get(location - 1));
                    break;

                case 2:
                    changeRhythm(commandStack.get(location - 1).get(2));
                    break;

                case 3:
                    redoTranscriptClear();
                    break;
                case 4:
                    changeInstrument(commandStack.get(location - 1).get(2));
                    break;
            }
            ath = true;
            location -= 1;
            if (location == 0) {
                canRedo = false;
            }
            canUndo = true;
        } else {
            env.error("No command to redo.");
        }
    }

    /**
     * Helper function called internally by both undoCommand and redoCommand to change the tempo to
     * the required tempo - either previous or next.
     *
     * @param newTempo Tempo to change to.
     */
    private void changeTempo(String newTempo) {
        new Tempo(newTempo, true).execute(env);
    }

    /**
     * Helper function called internally by both undoCommand and redoCommand to change the rhythm to
     * the required rhythm - either previous or next.
     *
     * @param newRhythm Rhythm timings to change to.
     */
    private void changeRhythm(String newRhythm) {
        new Rhythm(newRhythm, false).execute(env);
    }

    /**
     * Convenience method for re-executing the "clear transcript" command
     */
    private void redoTranscriptClear() {
        env.getRootController().clearTranscript();
    }

    /**
     * Undoes the "clear transcript" command. Returns all text that was deleted from the transcript,
     * back into the transcript.
     */
    private void undoTranscriptClear() {
        env.getTranscriptManager().setTranscriptContent(env.getTranscriptManager().getBackUpTranscript());
        env.getTranscriptManager().setBackupTranscript(new ArrayList<OutputTuple>());
        env.getRootController().setTranscriptPaneText(env.getTranscriptManager().convertToText());
    }

    /**
     * Helper function called by undoCommand to delete a musical term that has been added.
     */
    private void deleteMusicalTerm(String termToDelete) {
        env.getMttDataManager().removeTerm(termToDelete);
        env.getTranscriptManager().setResult(String.format("Musical Term %s has been deleted.",
                termToDelete));
    }

    /**
     * Helper function called by the redoCommand function to create a musical term that has been
     * deleted by the undo command function.
     */
    private void addMusicalTerm(ArrayList<String> termToAdd) {
        ArrayList<String> termArgs = new ArrayList<String>();
        termArgs.add(termToAdd.get(1));
        termArgs.add(termToAdd.get(2));
        termArgs.add(termToAdd.get(3));
        termArgs.add(termToAdd.get(4));

        new MusicalTerm(termArgs).execute(env);
    }

    /**
     * Helper function called by undo or redo change of instrument. Changes the instrument, either
     * to the previous or to an undone instrument
     *
     * @param newInstrument The instrument which will be set
     */
    private void changeInstrument(String newInstrument) {
        ArrayList<String> instrumentName = new ArrayList<>();
        instrumentName.add(newInstrument);
        new InstrumentCommand(true, instrumentName).execute(env);
    }

}
