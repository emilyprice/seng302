package seng302.command;


import java.util.ArrayList;
import java.util.HashMap;

import seng302.Environment;
import seng302.data.Note;
import seng302.utility.Checker;
import seng302.MusicPlayer;
import seng302.command.Scale;
import seng302.utility.OctaveUtil;

/**
 * Created by Sarah on 11/05/2016.
 * Chord is used to obtain the notes required to make up a chord
 */
public class Chord implements Command {
    private ArrayList<Note> chord = new ArrayList<Note>();
    private int octaves; //number of octaves to be played
    private char currentLetter; //the letter the current note should be
    String type; //where it is major or minor
    String outputType; //whether it wants to be played or printed
    String startNote; //the root note
    Note note; //the note the current note should be



    public Chord(HashMap<String, String> chord, String outputType) {
        /**this is a total mess. Trying to model off scale and note and I have no idea what I
         * am doing at all
         */
        this.startNote = chord.get("note");
        this.type = chord.get("scale_type");
        this.outputType = outputType;
        currentLetter = Character.toUpperCase(startNote.charAt(0));
        note = Note.lookup(OctaveUtil.addDefaultOctave(startNote));
        this.chord = note.getChord(type);

    }



    public float getLength(Environment env) {
        return 0;
    };



    public void execute(Environment env) {

        if (Checker.isDoubleFlat(startNote) || Checker.isDoubleSharp(startNote)) {
            //Disregards double sharps/double flats
            env.error("Invalid chord: '" + startNote + ' ' + type + "'.");
        } else {
            String chordString = "";

            for (Note i : chord) {
                String j = i.getNote();
                chordString += j + ' ';
            }

            env.getTranscriptManager().setResult(chordString);
        }

    }
}
