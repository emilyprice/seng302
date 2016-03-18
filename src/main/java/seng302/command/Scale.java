package seng302.command;

import java.util.ArrayList;

import seng302.Environment;
import seng302.data.Note;
import seng302.utility.OctaveUtil;

/**
 * Created by emily on 18/03/16.
 */
public class Scale implements Command {
    String search;
    String type;
    String outputType;
    private boolean octaveSpecified;
    private Note note;


    public Scale(String a, String b, String outputType) {
        this.search = a;
        this.type = b;
        this.outputType = outputType;
    }

    public void execute(Environment env){
        if (type.equals("major")) {
            try {
                if (OctaveUtil.octaveSpecifierFlag(this.search)) {
                    octaveSpecified = true;
                    this.note = Note.lookup(search);
                } else {
                    octaveSpecified = false;
                    this.note = Note.lookup(OctaveUtil.addDefaultOctave(search));
                }
                if (this.outputType.equals("note")) {
                    env.getTranscriptManager().setResult(scaleToString(note.getMajorScale()));
                } else {
                    // Is midi
                    env.getTranscriptManager().setResult(scaleToMidi(note.getMajorScale()));
                }
            } catch (Exception e) {
                env.error("Note is not contained in the MIDI library.");
            }
        } else {
            env.error("Invalid scale type: '" + type + "'.");
        }
    }

    private String scaleToString(ArrayList<Note> scaleNotes){
        String notesAsText = "";
        for (Note note:scaleNotes) {
            if (this.octaveSpecified) {
                notesAsText += note.getNote() + " ";
            } else {
                notesAsText += OctaveUtil.removeOctaveSpecifier(note.getNote()) + " ";
            }
        }
        return notesAsText.trim();

    }

    private String scaleToMidi(ArrayList<Note> scaleNotes) {
        String midiValues = "";
        for (Note note:scaleNotes) {
            midiValues += note.getMidi() + " ";
        }
        return midiValues.trim();
    }
}
