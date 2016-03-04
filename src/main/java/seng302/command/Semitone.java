package seng302.command;

import seng302.Environment;
import seng302.data.Note;
import seng302.utility.OctaveUtil;

/**
 * Created by team5 on 02-Mar-16.
 *  Command used to output a semitone either up or down from a provided note string.
 */
public class Semitone implements Command {
    private Note note;
    Boolean up;
    String searchval;

    public Semitone(String s, Boolean up) { //true for up, false for down.
        this.searchval = s;
        this.up = up;
    }
    public void execute(Environment env) {
        if (OctaveUtil.octaveSpecifierFlag(this.searchval)) {
            try {
                this.note = Note.lookup(searchval);
                if(up) env.getTranscriptManager().setCommand(note.semitone_up().getNote());
                else env.getTranscriptManager().setCommand(note.semitone_down().getNote());
            }
            catch (Exception e){
                env.getTranscriptManager().setCommand("Note is not contained in the midi library");
            }
        }
        else
        {
            this.note = Note.lookup(OctaveUtil.addDefaultOctave(searchval));
            if(up) env.getTranscriptManager().setCommand(OctaveUtil.removeOctaveSpecifier(note.semitone_up().getNote()));
            else env.getTranscriptManager().setCommand(OctaveUtil.removeOctaveSpecifier(note.semitone_down().getNote()));
        }
    }
}