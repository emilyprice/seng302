package seng302.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

import seng302.utility.musicNotation.Checker;
import seng302.utility.musicNotation.OctaveUtil;

/**
 * This Note class contains a static HashMap of all notes as well as the lookup method and
 * functionality for individual note objects.
 */
public class Note {
    private int midi;
    private String note;
    private HashMap<String, String> enharmonics;
    public static final int noteCount = 128;

    public static HashMap<String, Note> notes;
    private static List<String> noteNames = new ArrayList<String>(Arrays.asList("C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"));

    protected Note(int midi, String note, HashMap<String, String> enharmonics) {
        this.midi = midi;
        this.note = note;
        this.enharmonics = enharmonics;
    }

    /**
     * This is how the HashMap of notes is populated.
     */
    static {
        int current_octave = -1;
        notes = new HashMap<String, Note>();
        for (int i = 0; i < noteCount; i++) {
            HashMap<String, String> tempEnharmonics;
            HashMap<String, String> tempEnharmonics2;
            HashMap<String, String> tempEnharmonics3;

            String noteName = noteNames.get(i % 12).concat(Integer.toString(current_octave));
            int index = i % 12;
            switch (index) {
                case 0: // C
                    tempEnharmonics = generateEnharmonics(current_octave, "C", "Dbb", "B#", "B#", "");
                    notes.put(noteName, new Note(i, noteName, tempEnharmonics));
                    notes.put(Integer.toString(i), new Note(i, noteName, tempEnharmonics));
                    tempEnharmonics2 = generateEnharmonics(current_octave, "C", "C", "", "C", "Dbb"); //B#
                    notes.put("B#".concat(Integer.toString(current_octave)), new Note(i, "B#".concat(Integer.toString(current_octave)), tempEnharmonics2));
                    tempEnharmonics3 = generateEnharmonics(current_octave, "C", "", "C", "C", "B#"); //Dbb
                    notes.put("Dbb".concat(Integer.toString(current_octave)), new Note(i, "Dbb".concat(Integer.toString(current_octave)), tempEnharmonics3));
                    break;
                case 1: // C#
                    tempEnharmonics = generateEnharmonics(current_octave, "Db", "Db", "Bx", "Db", "");
                    notes.put(noteName, new Note(i, noteName, tempEnharmonics));
                    notes.put(Integer.toString(i), new Note(i, noteName, tempEnharmonics));
                    tempEnharmonics2 = generateEnharmonics(current_octave, "Db", "", "C#", "C#", "Bx"); // Db
                    notes.put("Db".concat(Integer.toString(current_octave)), new Note(i, "Db".concat(Integer.toString(current_octave)), tempEnharmonics2));
                    tempEnharmonics3 = generateEnharmonics(current_octave, "Db", "C#", "", "C#", ""); // Bx
                    notes.put("Bx".concat(Integer.toString(current_octave)), new Note(i, "Bx".concat(Integer.toString(current_octave)), tempEnharmonics3));
                    break;
                case 2: // D
                    tempEnharmonics = generateEnharmonics(current_octave, "D", "Ebb", "Cx", "", "");
                    notes.put(noteName, new Note(i, noteName, tempEnharmonics));
                    notes.put(Integer.toString(i), new Note(i, noteName, tempEnharmonics));
                    tempEnharmonics2 = generateEnharmonics(current_octave, "D", "", "D", "D", "Cx"); // Ebb
                    notes.put("Ebb".concat(Integer.toString(current_octave)), new Note(i, "Ebb".concat(Integer.toString(current_octave)), tempEnharmonics2));
                    tempEnharmonics3 = generateEnharmonics(current_octave, "D", "D", "", "D", "Ebb"); // Cx
                    notes.put("Cx".concat(Integer.toString(current_octave)), new Note(i, "Cx".concat(Integer.toString(current_octave)), tempEnharmonics3));
                    break;
                case 3: // D#
                    tempEnharmonics = generateEnharmonics(current_octave, "Eb", "Eb", "", "Eb", "Fbb");
                    notes.put(noteName, new Note(i, noteName, tempEnharmonics));
                    notes.put(Integer.toString(i), new Note(i, noteName, tempEnharmonics));
                    tempEnharmonics2 = generateEnharmonics(current_octave, "Eb", "Fbb", "D#", "D#", ""); // Eb
                    notes.put("Eb".concat(Integer.toString(current_octave)), new Note(i, "Eb".concat(Integer.toString(current_octave)), tempEnharmonics2));
                    tempEnharmonics3 = generateEnharmonics(current_octave, "Eb", "", "Eb", "D#", ""); //Fbb
                    notes.put("Fbb".concat(Integer.toString(current_octave)), new Note(i, "Fbb".concat(Integer.toString(current_octave)), tempEnharmonics3));
                    break;
                case 4: // E
                    tempEnharmonics = generateEnharmonics(current_octave, "E", "Fb", "Dx", "Fb", "");
                    notes.put(noteName, new Note(i, noteName, tempEnharmonics));
                    notes.put(Integer.toString(i), new Note(i, noteName, tempEnharmonics));
                    tempEnharmonics2 = generateEnharmonics(current_octave, "E", "", "E", "E", "Dx"); // Fb
                    notes.put("Fb".concat(Integer.toString(current_octave)), new Note(i, "Fb".concat(Integer.toString(current_octave)), tempEnharmonics2));
                    tempEnharmonics3 = generateEnharmonics(current_octave, "E", "E", "", "E", "Fb"); // Dx
                    notes.put("Dx".concat(Integer.toString(current_octave)), new Note(i, "Dx".concat(Integer.toString(current_octave)), tempEnharmonics3));
                    break;
                case 5: // F
                    tempEnharmonics = generateEnharmonics(current_octave, "F", "Gbb", "E#", "E#", "");
                    notes.put(noteName, new Note(i, noteName, tempEnharmonics));
                    notes.put(Integer.toString(i), new Note(i, noteName, tempEnharmonics));
                    tempEnharmonics2 = generateEnharmonics(current_octave, "F", "F", "", "F", "Gbb"); // E#
                    notes.put("E#".concat(Integer.toString(current_octave)), new Note(i, "E#".concat(Integer.toString(current_octave)), tempEnharmonics2));
                    tempEnharmonics3 = generateEnharmonics(current_octave, "F", "", "F", "F", "E#"); // Gbb
                    notes.put("Gbb".concat(Integer.toString(current_octave)), new Note(i, "Gbb".concat(Integer.toString(current_octave)), tempEnharmonics3));
                    break;
                case 6: // F#
                    tempEnharmonics = generateEnharmonics(current_octave, "Gb", "Gb", "Ex", "Gb", "");
                    notes.put(noteName, new Note(i, noteName, tempEnharmonics));
                    notes.put(Integer.toString(i), new Note(i, noteName, tempEnharmonics));
                    tempEnharmonics2 = generateEnharmonics(current_octave, "Gb", "", "F#", "F#", "Ex"); // Gb
                    notes.put("Gb".concat(Integer.toString(current_octave)), new Note(i, "Gb".concat(Integer.toString(current_octave)), tempEnharmonics2));
                    tempEnharmonics3 = generateEnharmonics(current_octave, "Gb", "F#", "", "F#", ""); // Ex
                    notes.put("Ex".concat(Integer.toString(current_octave)), new Note(i, "Ex".concat(Integer.toString(current_octave)), tempEnharmonics3));
                    break;
                case 7: // G
                    tempEnharmonics = generateEnharmonics(current_octave, "G", "Abb", "Fx", "", "");
                    notes.put(noteName, new Note(i, noteName, tempEnharmonics));
                    notes.put(Integer.toString(i), new Note(i, noteName, tempEnharmonics));
                    tempEnharmonics2 = generateEnharmonics(current_octave, "G", "", "G", "G", "Fx"); //Abb
                    notes.put("Abb".concat(Integer.toString(current_octave)), new Note(i, "Abb".concat(Integer.toString(current_octave)), tempEnharmonics2));
                    tempEnharmonics3 = generateEnharmonics(current_octave, "G", "G", "", "G", "Abb"); // Fx
                    notes.put("Fx".concat(Integer.toString(current_octave)), new Note(i, "Fx".concat(Integer.toString(current_octave)), tempEnharmonics3));
                    break;
                case 8: // G#
                    tempEnharmonics = generateEnharmonics(current_octave, "Ab", "Ab", "", "Ab", "");
                    notes.put(noteName, new Note(i, noteName, tempEnharmonics));
                    notes.put(Integer.toString(i), new Note(i, noteName, tempEnharmonics));
                    tempEnharmonics2 = generateEnharmonics(current_octave, "Ab", "", "G#", "G#", ""); // Ab
                    notes.put("Ab".concat(Integer.toString(current_octave)), new Note(i, "Ab".concat(Integer.toString(current_octave)), tempEnharmonics2));
                    break;
                case 9: // A
                    tempEnharmonics = generateEnharmonics(current_octave, "A", "Bbb", "Gx", "", "");
                    notes.put(noteName, new Note(i, noteName, tempEnharmonics));
                    notes.put(Integer.toString(i), new Note(i, noteName, tempEnharmonics));
                    tempEnharmonics2 = generateEnharmonics(current_octave, "A", "", "A", "A", "Gx"); //Bbb
                    notes.put("Bbb".concat(Integer.toString(current_octave)), new Note(i, "Bbb".concat(Integer.toString(current_octave)), tempEnharmonics2));
                    tempEnharmonics3 = generateEnharmonics(current_octave, "A", "A", "", "A", "Bbb"); // Gx
                    notes.put("Gx".concat(Integer.toString(current_octave)), new Note(i, "Gx".concat(Integer.toString(current_octave)), tempEnharmonics3));
                    break;
                case 10: // A#
                    tempEnharmonics = generateEnharmonics(current_octave, "Bb", "Bb", "", "Bb", "Cbb");
                    notes.put(noteName, new Note(i, noteName, tempEnharmonics));
                    notes.put(Integer.toString(i), new Note(i, noteName, tempEnharmonics));
                    tempEnharmonics2 = generateEnharmonics(current_octave, "Bb", "Cbb", "A#", "A#", ""); // Bb
                    notes.put("Bb".concat(Integer.toString(current_octave)), new Note(i, "Bb".concat(Integer.toString(current_octave)), tempEnharmonics2));
                    tempEnharmonics2 = generateEnharmonics(current_octave, "Bb", "", "Bb", "A#", ""); // Cbb
                    notes.put("Cbb".concat(Integer.toString(current_octave)), new Note(i, "Cbb".concat(Integer.toString(current_octave)), tempEnharmonics2));//cbb
                    break;
                case 11: // B
                    tempEnharmonics = generateEnharmonics(current_octave, "B", "Cb", "Ax", "Cb", "");
                    notes.put(noteName, new Note(i, noteName, tempEnharmonics));
                    notes.put(Integer.toString(i), new Note(i, noteName, tempEnharmonics));
                    tempEnharmonics2 = generateEnharmonics(current_octave, "B", "", "B", "B", "Ax"); // Cb
                    notes.put("Cb".concat(Integer.toString(current_octave)), new Note(i, "Cb".concat(Integer.toString(current_octave)), tempEnharmonics2));
                    tempEnharmonics3 = generateEnharmonics(current_octave, "B", "B", "", "B", "Cb"); // Ax
                    notes.put("Ax".concat(Integer.toString(current_octave)), new Note(i, "Ax".concat(Integer.toString(current_octave)), tempEnharmonics3));
                    break;
                default:
                    break;
            }
            if ((i + 1) % 12 == 0) {
                current_octave += 1;
            }
        }
    }

    private static HashMap<String, String> generateEnharmonics(Integer octave, String desc, String above, String below, String simple, String other) {
        HashMap<String, String> notesEnharmonics = new HashMap<String, String>();

        // Descending enharmonic
        if (Checker.isValidNormalNote(desc)) {
            notesEnharmonics.put("descending", desc.concat(Integer.toString(octave)));
        } else {
            notesEnharmonics.put("descending", desc);
        }

        //Enharmonic above
        if (Checker.isValidNormalNote(above)) {
            notesEnharmonics.put("above", above.concat(Integer.toString(octave)));
        } else {
            notesEnharmonics.put("above", above);
        }


        // Enharmonic Below
        if (Checker.isValidNormalNote(below)) {
            notesEnharmonics.put("below", below.concat(Integer.toString(octave)));
        } else {
            notesEnharmonics.put("below", below);
        }


        // Simple enharmonics
        if (Checker.isValidNormalNote(simple)) {
            notesEnharmonics.put("simple", simple.concat(Integer.toString(octave)));
        } else {
            notesEnharmonics.put("simple", simple);
        }

        // Other enharmonics
        // notesEnharmonics.put("other", other.concat(Integer.toString(octave)));
        if (Checker.isValidNormalNote(other)) {
            notesEnharmonics.put("other", other.concat(Integer.toString(octave)));
        } else {
            notesEnharmonics.put("other", other);
        }


        return notesEnharmonics;
    }


    /**
     * Returns the note object from the HashMap of notes
     *
     * @param s The note or MIDI value to lookup.
     * @return Note object that matches the String supplied.
     */
    static public Note lookup(String s) {
        s = OctaveUtil.capitalise(s);
        Note note = notes.get(s);
        if (note == null) {
            System.err.println(s + " is not a note.");
        }
        return note;
    }

    /**
     * @return the note name from a string.
     */
    public String getNote() {
        return this.note;
    }


    /**
     * Returns the descending enharmonic name. So the b value instead of the # value.
     *
     * @return descending enharmonic name.
     */
    public String getDescendingEnharmonic() {
        return this.enharmonics.get("descending");
    }

    /**
     * Finds the note x semitones higher.
     *
     * @param semitones The number of semitones to increase by
     * @return the Note object x semitones higher than the current note.
     */
    public Note semitoneUp(int semitones) {
        return Note.lookup(Integer.toString(this.getMidi() + semitones));
    }

    /**
     * Finds the note x semitones lower.
     *
     * @param semitones The number of semitones to decrease by
     * @return the Note object x semitones lower than the current note.
     */
    public Note semitoneDown(int semitones) {
        return Note.lookup(Integer.toString(this.getMidi() - semitones));
    }


    /**
     * Finds the preferred equivalent sharp note.
     *
     * @return the sharp name for the original note.
     */
    public String sharpName() {
        return this.enharmonics.get("above");
    }


    public String flatName() {
        return this.enharmonics.get("below");
    }

    public String simpleEnharmonic() {
        return this.enharmonics.get("simple");
    }

    /**
     * Makes an array list of the major scale starting on the note.
     *
     * @return array list of notes in the scale. If any notes are null, the scale returned will be
     * null.
     */
    public ArrayList<Note>
    getOctaveScale(String type, int octaves, boolean up) {
        ArrayList<Note> scaleNotes = new ArrayList<>();
        List<Integer> semitones;
        Note currentNote = this;
        scaleNotes.add(currentNote);
        if(type.toLowerCase().equals("melodic minor mode")) {
            List<String> melodicMinorModes = new ArrayList<String>(Arrays.asList("minormajor", "dorian b2", "lydian #5", "lydian dominant", "mixolydian b6", "locrian #2", "altered"));
            Random rand = new Random();
            type = (melodicMinorModes.get(rand.nextInt(melodicMinorModes.size())));
        }
        if(type.toLowerCase().equals("major mode")) {
            Random rand = new Random();
            List<String> majorModes = new ArrayList<String>(Arrays.asList("ionian", "dorian", "phrygian", "lydian", "mixolydian", "aeolian", "locrian"));
            type = (majorModes.get(rand.nextInt(majorModes.size())));
        }

        if (up) {
            switch (type.toLowerCase()) {
                case "major":
                    semitones = Arrays.asList(2, 4, 5, 7, 9, 11, 12);
                    break;
                case "minor":
                    semitones = Arrays.asList(2, 3, 5, 7, 8, 10, 12);
                    break;
                case "melodic minor":
                    semitones = Arrays.asList(2, 3, 5, 7, 9, 11, 12);
                    break;
                case "ionian":
                    //D E F G A B C
                    semitones = Arrays.asList(2, 4, 5, 7, 9, 11, 12);
                    break;
                case "dorian":
                    semitones = Arrays.asList(2, 3, 5, 7, 9, 10, 12);
                    break;
                case "phrygian":
                    semitones = Arrays.asList(1, 3, 5, 7, 8, 10, 12);
                    break;
                case "lydian":
                    semitones = Arrays.asList(2, 4, 6, 7, 9, 11, 12);
                    break;
                case "mixolydian":
                    semitones = Arrays.asList(2, 4, 5, 7, 9, 10, 12);
                    break;
                case "aeolian":
                    semitones = Arrays.asList(2, 3, 5, 7, 8, 10, 12);
                    break;
                case "locrian":
                    semitones = Arrays.asList(1, 3, 5, 6, 8, 10, 12);
                    break;
                case "minormajor":
                    // Melodic Minor Modes I
                    semitones = Arrays.asList(2, 3, 5, 7, 9, 11, 12);
                    break;
                case "dorian b2":
                    // Melodic Minor Modes II
                    semitones = Arrays.asList(1, 3, 5, 7, 9, 10, 12);
                    break;
                case "lydian #5":
                    // Melodic Minor Modes III
                    semitones = Arrays.asList(2, 4, 6, 8, 9, 11, 12);
                    break;
                case "lydian dominant":
                    // Melodic Minor Modes IV
                    semitones = Arrays.asList(2, 4, 6, 7, 9, 10, 12);
                    break;
                case "mixolydian b6":
                    // Melodic Minor Modes V
                    semitones = Arrays.asList(2, 4, 5, 7, 8, 10, 12);
                    break;
                case "locrian #2":
                    // Melodic Minor Modes VI
                    semitones = Arrays.asList(2, 3, 5, 6, 8, 10, 12);
                    break;
                case "altered":
                    // Melodic Minor Modes VII
                    semitones = Arrays.asList(1, 3, 4 ,6, 8, 10, 12);
                    break;
                case "blues":
                    semitones = Arrays.asList(3, 5, 6, 7, 10, 12);
                    break;
                case "major pentatonic":
                    semitones = Arrays.asList(2, 4, 7, 9, 12);
                    break;
                case "minor pentatonic":
                    semitones = Arrays.asList(3, 5, 7, 10, 12);
                    break;
                case "harmonic minor":
                    semitones = Arrays.asList(2, 3, 5, 7, 8, 11, 12);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid scale type: '" + type + "'.");
            }
            for (int i = 0; i < octaves; i++) {
                for (int j = 0; j < semitones.size(); j++) {
                    scaleNotes.add(currentNote.semitoneUp(semitones.get(j)));
                }
                currentNote = currentNote.semitoneUp(semitones.get(semitones.size()-1));
            }
        } else {
            switch (type.toLowerCase()) {
                case "major":
                    semitones = Arrays.asList(1, 3, 5, 7, 8, 10, 12);
                    break;
                case "minor":
                    semitones = Arrays.asList(2, 4, 5, 7, 9, 10, 12);
                    break;
                case "melodic minor":
                    semitones = Arrays.asList(2, 4, 5, 7, 9, 10, 12);
                    break;
                case "ionian":
                    semitones = Arrays.asList(1, 3, 5, 7, 8, 10, 12);
                    break;
                case "dorian":
                    semitones = Arrays.asList(2, 3, 5, 7, 9, 10, 12);
                    break;
                case "phrygian":
                    semitones = Arrays.asList(2, 4, 5, 7, 9, 11, 12);
                    break;
                case "lydian":
                    semitones = Arrays.asList(1, 3, 5, 6, 8, 10, 12);
                    break;
                case "mixolydian":
                    semitones = Arrays.asList(2, 3, 5, 7, 8, 10, 12);
                    break;
                case "aeolian":
                    semitones = Arrays.asList(2, 4, 5, 7, 9, 10, 12);
                    break;
                case "locrian":
                    semitones = Arrays.asList(2, 4, 6, 7, 9, 11, 12);
                    break;
                case "blues":
                    semitones = Arrays.asList(2, 5, 6, 7, 9, 12);
                    break;
                case "major pentatonic":
                    semitones = Arrays.asList(3, 5, 8, 10, 12);
                    break;
                case "minor pentatonic":
                    semitones = Arrays.asList(2, 5, 7, 9, 12);
                    break;
                case "harmonic minor":
                    semitones = Arrays.asList(1, 4, 5, 7, 9, 10, 12);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid scale type: '" + type + "'.");
            }
            for (int i = 0; i < octaves; i++) {
                for (int j = 0; j < semitones.size(); j++) {
                    scaleNotes.add(currentNote.semitoneDown(semitones.get(j)));
                }
                currentNote = currentNote.semitoneDown(semitones.get(semitones.size() - 1));
            }
        }
        if (scaleNotes.contains(null)) {
            return null;
        }
        return scaleNotes;
    }


    /**
     * Convenience method for when you only want one octave.
     *
     * @param type Type of scale. major, minor etc.
     * @param up   Whether the scale goes up or down.
     * @return The Arraylist of notes that make up the scale. Or null if invalid scale.
     */
    public ArrayList<Note> getScale(String type, boolean up) {
        return this.getOctaveScale(type, 1, up);
    }

    public ArrayList<String> getAllEnharmonics() {
        Set<String> allEnharmonicsSet = new HashSet<String>();
        // Check that the enharmonics are actual notes and not blank space
        for (String enharmonic : enharmonics.values()) {
            if (!enharmonic.equals(getNote())) {
                if ((enharmonic.length() != 0 && enharmonic.length() != 1)) {
                    allEnharmonicsSet.add(enharmonic);
                }
            }
        }
        ArrayList<String> allEnharmonics = new ArrayList<String>(allEnharmonicsSet);
        Collections.sort(allEnharmonics);
        return allEnharmonics;
    }

    /**
     * Returns the note name for the current note that uses the given letter. If there is no
     * enharmonic using that letter for the current note it will return null.
     *
     * @param letter The letter to include in note name.
     * @return Note name using the given letter.
     */
    public String getEnharmonicWithLetter(char letter) {
        for (String value : enharmonics.values()) {
            if (value.startsWith(String.valueOf(letter))) {
                return value;
            }
        }
        if (getNote().startsWith(String.valueOf(letter))) {
            return getNote();
        }
        return null;
    }


    /**
     * @return the MIDI value.
     */
    public Integer getMidi() {
        return this.midi;
    }

    /**
     * Generates a note in the octave of middle C
     *
     * @return the random note
     */
    public static Note getRandomNote() {
        Random rand = new Random();
        return Note.lookup(Integer.toString(rand.nextInt(11) + 60));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Note note1 = (Note) o;
        return midi == note1.midi &&
                Objects.equals(note, note1.note);
    }


}
