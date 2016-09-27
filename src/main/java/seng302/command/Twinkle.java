package seng302.command;

import java.util.ArrayList;

import seng302.Environment;
import seng302.data.Note;

/**
 * Created by isabelle on 4/05/16.
 */
public class Twinkle implements Command {


    public long getLength(Environment env) {
        int tempo = env.getPlayer().getTempo();
        long crotchetLength = 60000 / tempo;
        return 14 * crotchetLength;
    }

    /**
     * Called when the Twinkle command is submitted to the transcript
     *
     * @param env the current environment.
     */
    public void execute(Environment env) {
        ArrayList<Note> song = new ArrayList<Note>();
        song.add(Note.lookup("C4"));
        song.add(Note.lookup("C4"));
        song.add(Note.lookup("G4"));
        song.add(Note.lookup("G4"));
        song.add(Note.lookup("A4"));
        song.add(Note.lookup("A4"));
        song.add(Note.lookup("G4"));
        song.add(Note.lookup("F4"));
        song.add(Note.lookup("F4"));
        song.add(Note.lookup("E4"));
        song.add(Note.lookup("E4"));
        song.add(Note.lookup("D4"));
        song.add(Note.lookup("D4"));
        song.add(Note.lookup("C4"));
        env.getPlayer().playNotes(song);
        env.getTranscriptManager().setResult("Playing...");
        env.getUserHandler().getCurrentUser().getProjectHandler().getCurrentProject().getBadgeManager().unlockBadge("TwinkleTwinkle");
    }

    @Override
    public String getHelp() {
        return "A mystery command...";
    }

    @Override
    public String getCommandText() {
        return "twinkle";
    }

    @Override
    public String getExample() {
        return getCommandText();
    }


}
