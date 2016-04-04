package seng302.command;


import javax.sound.midi.MidiSystem;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Synthesizer;

import seng302.Environment;
import seng302.data.Note;
import seng302.utility.OctaveUtil;

/**
 * Plays a note using the midi library
 */
public class PlayNote implements Command {

    private Note note;
    private Integer duration;

    public PlayNote(String note) {
        this.note = Note.lookup(OctaveUtil.addDefaultOctave(note));
    }

    public PlayNote(String note, int duration) {
        this.note = Note.lookup(OctaveUtil.addDefaultOctave(note));
        this.duration = duration;
    }


    public void execute(Environment env) {
        if (this.duration == null) {
            note.playNote(env.getTempo());
        } else {
            note.playNote(this.duration);
        }
    }
}
