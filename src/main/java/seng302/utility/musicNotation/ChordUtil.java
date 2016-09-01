package seng302.utility.musicNotation;

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import seng302.command.Scale;
import seng302.data.Note;

import static javax.swing.UIManager.put;

/**
 * Created by Jonty on 24-May-16.
 */
public class ChordUtil {

    /**
     * Maps the function (roman numeral) to the quality for diatonic chords.
     */
    private static final Map<String, String> chordFunctionQuality = new HashMap<String, String>() {{
        put("I", "major 7th");
        put("II", "minor 7th");
        put("III", "minor 7th");
        put("IV", "major 7th");
        put("V", "7th");
        put("VI", "minor 7th");
        put("VII", "half-diminished 7th");
    }};

    /**
     * Maps the function(roman numeral) the the integer number that they represent.
     */
    private static final BidiMap<String, Integer> roman = new DualHashBidiMap<String, Integer>() {{
        put("I", 1);
        put("II", 2);
        put("III", 3);
        put("IV", 4);
        put("V", 5);
        put("VI", 6);
        put("VII", 7);
    }};


    /**
     * Returns the name of the chord, with the correct enharmonic letter, with or without an octave
     * specifier.
     *
     * @param notes            notes of the chord.
     * @param octave           was an octave specified in the input?
     * @param enharmonicLetter the letter that the chord should be represented by.
     * @return a string of the chord name including a note and a chord type.
     */
    public static String getChordName(ArrayList<Integer> notes, boolean octave, char enharmonicLetter) {

        String type = getChordType(notes);


        String n = octave ? Note.lookup(notes.get(0).toString()).getEnharmonicWithLetter(enharmonicLetter) :
                OctaveUtil.removeOctaveSpecifier(Note.lookup(notes.get(0).toString())
                        .getEnharmonicWithLetter(enharmonicLetter));

        return n + " " + type;

    }

    /**
     * This method uses the number of semitones between notes to determine the type of chord.
     *
     * @param notes The notes of the chord.
     * @return The chord type.
     */
    public static String getChordType(ArrayList<Integer> notes) {
        if (notes.size() > 3) {
            if (notes.get(1) % 12 == (notes.get(0) + 3) % 12 && notes.get(2) % 12 == (notes.get(0) + 7) % 12
                    && notes.get(3) % 12 == (notes.get(0) + 10) % 12) {
                return "minor 7th";
                //for major 7th chords
            } else if (notes.get(1) % 12 == (notes.get(0) + 4) % 12 && notes.get(2) % 12 == (notes.get(0) + 7) % 12
                    && notes.get(3) % 12 == (notes.get(0) + 11) % 12) {
                return "major 7th";
                //for 7th chords
            } else if (notes.get(1) % 12 == (notes.get(0) + 4) % 12 && notes.get(2) % 12 == (notes.get(0) + 7) % 12
                    && notes.get(3) % 12 == (notes.get(0) + 10) % 12) {
                return "seventh";
                //for half diminished chords
            } else if (notes.get(1) % 12 == (notes.get(0) + 3) % 12 && notes.get(2) % 12 == (notes.get(0) + 6) % 12
                    && notes.get(3) % 12 == (notes.get(0) + 10) % 12) {
                return "half diminished";
                //for diminished chords
            } else if (notes.get(1) % 12 == (notes.get(0) + 3) % 12 && notes.get(2) % 12 == (notes.get(0) + 6) % 12
                    && notes.get(3) % 12 == (notes.get(0) + 9) % 12) {
                return "diminished 7th";
                //for diminished chords
            }
        }
        if (notes.size() > 2) { //Scales

            //for major chords
            if (notes.get(1) % 12 == (notes.get(0) + 4) % 12 && notes.get(2) % 12 == (notes.get(0) + 7) % 12) {
                return "major";
                //for minor chords
            } else if (notes.get(1) % 12 == (notes.get(0) + 3) % 12 && notes.get(2) % 12 == (notes.get(0) + 7) % 12) {
                return "minor";
                //for minor 7th chords
            } else if (notes.get(1) % 12 == (notes.get(0) + 3) % 12 && notes.get(2) % 12 == (notes.get(0) + 6) % 12) {
                return "diminished";
            }

        }
        throw new IllegalArgumentException("Not a chord");
    }


    /**
     * Returns the chord name for the given midi vaue.
     *
     * @param notes  ArrayList of Note values
     * @param octave if true, treats all Notes as their middle octave value.
     * @return Name of the given chord. e.g. C major or C minor.
     */
    public static String getChordName(ArrayList<Integer> notes, Boolean octave) {

        if (notes.size() > 3) {
            String noteDisplay = octave ? Note.lookup(String.valueOf(notes.get(0))).getNote() : OctaveUtil.removeOctaveSpecifier(Note.lookup(String.valueOf(notes.get(0))).getNote()); //Ignore Octave or not?

            if (notes.get(1) % 12 == (notes.get(0) + 3) % 12 && notes.get(2) % 12 == (notes.get(0) + 7) % 12
                    && notes.get(3) % 12 == (notes.get(0) + 10) % 12) {
                return noteDisplay + " minor 7th";
                //for major 7th chords
            } else if (notes.get(1) % 12 == (notes.get(0) + 4) % 12 && notes.get(2) % 12 == (notes.get(0) + 7) % 12
                    && notes.get(3) % 12 == (notes.get(0) + 11) % 12) {
                return noteDisplay + " major 7th";
                //for 7th chords
            } else if (notes.get(1) % 12 == (notes.get(0) + 4) % 12 && notes.get(2) % 12 == (notes.get(0) + 7) % 12
                    && notes.get(3) % 12 == (notes.get(0) + 10) % 12) {
                return noteDisplay + " seventh";
                //for half diminished chords
            } else if (notes.get(1) % 12 == (notes.get(0) + 3) % 12 && notes.get(2) % 12 == (notes.get(0) + 6) % 12
                    && notes.get(3) % 12 == (notes.get(0) + 10) % 12) {
                return noteDisplay + " half diminished";
                //for diminished chords
            } else if (notes.get(1) % 12 == (notes.get(0) + 3) % 12 && notes.get(2) % 12 == (notes.get(0) + 6) % 12
                    && notes.get(3) % 12 == (notes.get(0) + 9) % 12) {
                return noteDisplay + " diminished 7th";
                //for diminished chords
            }
        }


        if (notes.size() > 2) { //Scales
            String noteDisplay = octave ? Note.lookup(String.valueOf(notes.get(0))).getNote() : OctaveUtil.removeOctaveSpecifier(Note.lookup(String.valueOf(notes.get(0))).getNote()); //Ignore Octave or not?

            //for major chords
            if (notes.get(1) % 12 == (notes.get(0) + 4) % 12 && notes.get(2) % 12 == (notes.get(0) + 7) % 12) {
                return noteDisplay + " major";
                //for minor chords
            } else if (notes.get(1) % 12 == (notes.get(0) + 3) % 12 && notes.get(2) % 12 == (notes.get(0) + 7) % 12) {
                return noteDisplay + " minor";
                //for minor 7th chords
            } else if (notes.get(1) % 12 == (notes.get(0) + 3) % 12 && notes.get(2) % 12 == (notes.get(0) + 6) % 12) {
                return noteDisplay + " diminished";
            }


        }
        throw new IllegalArgumentException("Not a chord");

    }

    /**
     * This method returns the correct enharmonic letter for each note of the chord.
     *
     * @param c The current letter
     * @return The next letter to use. (Skips every second letter for chords.)
     */
    public static char nextChordLetterChar(char c) {
        int index = "ABCDEFG".indexOf(c);

        if (index >= 5) index -= 7; //Wraps around

        return "ABCDEFG".charAt(index + 2);
    }

    /**
     * Inverts a chord one time by shifting the first element to the end position.
     *
     * @param chord ArrayList of Notes resembling a chord
     * @return ArrayList of Notes resembling an inverted chord
     */
    public static ArrayList<Note> invertChord(ArrayList<Note> chord) {

        Note first = chord.remove(0); //Pop first element
        if (first.getMidi() + 12 <= 127) {
            chord.add(Note.lookup(String.valueOf(first.getMidi() + 12)));
        } else chord.add(Note.lookup(String.valueOf(first.getMidi() + 12 - 120)));


        return chord;
    }


    /**
     * Retrieves the Chord notes for a specified Note. e.g. major chord for Note C4 will return
     * Notes C4, E4, G4.
     *
     * @param note Note note corresponding to the chord.
     * @param type String type of chord (either major or minor)
     * @return ArrayList of Notes corresponding to the chord.
     */
    public static ArrayList<Note> getChord(Note note, String type) {
        ArrayList<Note> chordNotes = new ArrayList<Note>();

        //for major chords (triads)
        if (type.toLowerCase().equals("major")) {
            Note currentNote = note;
            chordNotes.add(currentNote);
            chordNotes.add(Note.lookup(currentNote.semitoneUp(4).getEnharmonicWithLetter(lettersUp(note.getNote(), 2))));
            chordNotes.add(Note.lookup(currentNote.semitoneUp(7).getEnharmonicWithLetter(lettersUp(note.getNote(), 4))));
            if (chordNotes.contains(null)) {
                return null;
            }

            //for minor chords (triads)
        } else if (type.toLowerCase().equals("minor")) {
            Note currentNote = note;
            chordNotes.add(currentNote);
            chordNotes.add(Note.lookup(currentNote.semitoneUp(3).getEnharmonicWithLetter(lettersUp(note.getNote(), 2))));
            chordNotes.add(Note.lookup(currentNote.semitoneUp(7).getEnharmonicWithLetter(lettersUp(note.getNote(), 4))));
            if (chordNotes.contains(null)) {
                return null;
            }
            //for minor 7th chords (4 note chords)
        } else if (type.toLowerCase().equals("minor 7th") ||
                type.toLowerCase().equals("minor seventh")) {
            Note currentNote = note;
            chordNotes.add(currentNote);
            chordNotes.add(Note.lookup(currentNote.semitoneUp(3).getEnharmonicWithLetter(lettersUp(note.getNote(), 2))));
            chordNotes.add(Note.lookup(currentNote.semitoneUp(7).getEnharmonicWithLetter(lettersUp(note.getNote(), 4))));
            chordNotes.add(Note.lookup(currentNote.semitoneUp(10).getEnharmonicWithLetter(lettersUp(note.getNote(), 6))));
            if (chordNotes.contains(null)) {
                return null;
            }
            //for major 7th chords (4 note chords)
        } else if (type.toLowerCase().equals("major 7th") ||
                type.toLowerCase().equals("major seventh")) {
            Note currentNote = note;
            chordNotes.add(currentNote);
            chordNotes.add(Note.lookup(currentNote.semitoneUp(4).getEnharmonicWithLetter(lettersUp(note.getNote(), 2))));
            chordNotes.add(Note.lookup(currentNote.semitoneUp(7).getEnharmonicWithLetter(lettersUp(note.getNote(), 4))));
            chordNotes.add(Note.lookup(currentNote.semitoneUp(11).getEnharmonicWithLetter(lettersUp(note.getNote(), 6))));
            if (chordNotes.contains(null)) {
                return null;
            }
            //for 7th chords (4-note chords)
        } else if (type.toLowerCase().equals("seventh") ||
                type.toLowerCase().equals("7th") ||
                type.toLowerCase().equals("seven")) {
            Note currentNote = note;
            chordNotes.add(currentNote);
            chordNotes.add(Note.lookup(currentNote.semitoneUp(4).getEnharmonicWithLetter(lettersUp(note.getNote(), 2))));
            chordNotes.add(Note.lookup(currentNote.semitoneUp(7).getEnharmonicWithLetter(lettersUp(note.getNote(), 4))));
            chordNotes.add(Note.lookup(currentNote.semitoneUp(10).getEnharmonicWithLetter(lettersUp(note.getNote(), 6))));
            if (chordNotes.contains(null)) {
                return null;
            }
            //for diminished chords (triad)
        } else if (type.toLowerCase().equals("diminished") ||
                type.toLowerCase().equals("dim")) {
            Note currentNote = note;
            chordNotes.add(currentNote);
            chordNotes.add(Note.lookup(currentNote.semitoneUp(3).getEnharmonicWithLetter(lettersUp(note.getNote(), 2))));
            chordNotes.add(Note.lookup(currentNote.semitoneUp(6).getEnharmonicWithLetter(lettersUp(note.getNote(), 4))));
            if (chordNotes.contains(null)) {
                return null;
            }
            //for half diminished chords (4 note chords)
        } else if (type.toLowerCase().equals("half diminished seventh") ||
                type.toLowerCase().equals("half dim seventh") ||
                type.toLowerCase().equals("half dim 7th") ||
                type.toLowerCase().equals("half diminished 7th") ||
                type.toLowerCase().equals("half dim") ||
                type.toLowerCase().equals("half diminished")) {
            Note currentNote = note;
            chordNotes.add(currentNote);
            chordNotes.add(Note.lookup(currentNote.semitoneUp(3).getEnharmonicWithLetter(lettersUp(note.getNote(), 2))));
            chordNotes.add(Note.lookup(currentNote.semitoneUp(6).getEnharmonicWithLetter(lettersUp(note.getNote(), 4))));
            chordNotes.add(Note.lookup(currentNote.semitoneUp(10).getEnharmonicWithLetter(lettersUp(note.getNote(), 6))));
            if (chordNotes.contains(null)) {
                return null;
            }
            //for diminished chords (4 chords)
        } else if (type.toLowerCase().equals("diminished seventh") ||
                type.toLowerCase().equals("dim seventh") ||
                type.toLowerCase().equals("dim 7th") ||
                type.toLowerCase().equals("diminished 7th")) {
            Note currentNote = note;
            chordNotes.add(currentNote);
            chordNotes.add(Note.lookup(currentNote.semitoneUp(3).getEnharmonicWithLetter(lettersUp(note.getNote(), 2))));
            chordNotes.add(Note.lookup(currentNote.semitoneUp(6).getEnharmonicWithLetter(lettersUp(note.getNote(), 4))));
            chordNotes.add(Note.lookup(currentNote.semitoneUp(9).getEnharmonicWithLetter(lettersUp(note.getNote(), 6))));
            if (chordNotes.contains(null)) {
                return null;
            }
            //if the string does not match a chord type
        } else {
            throw new IllegalArgumentException("Invalid chord type: '" + type + "'.");
        }
        return chordNotes;
    }

    /**
     * Just like the getChord method except it takes an arrayList of midi values rather than Note
     * values.
     *
     * @param type String type of chord (either major or minor)
     * @return ArrayList of Notes corresponding to the chord.
     */
    public static ArrayList<Integer> getChordMidi(int midi, String type) {
        ArrayList<Integer> chordMidiNotes = new ArrayList<Integer>();
        ArrayList<Note> chordNotes = ChordUtil.getChord(Note.lookup(String.valueOf(midi)), type);
        try {
            for (Note note : chordNotes) {
                chordMidiNotes.add(note.getMidi());
            }
        } catch (NullPointerException e) {
            return null;
        }
        return chordMidiNotes;
    }


    /**
     * Returns the diatonic chord quality when given a chord function (roman numeral)
     *
     * @param romanNumeral from I to VII
     * @return the chord quality
     */
    public static String getDiatonicChordQuality(String romanNumeral) {
        return chordFunctionQuality.get(romanNumeral.toUpperCase());
    }

    /**
     * Finds the name of the chord function when given a scale and function. Basically it gets the
     * scale and finds the note that is (romanNumeral) above the starting note of the scale. Then it
     * finds the quality of the scale and adds that to the end.
     *
     * @param romanNumeral the function of the chord
     * @param startingNote the scale starting note
     * @param scaleType    the scale type of the scale
     * @return the chord function
     */
    public static String getChordFunction(String romanNumeral, String startingNote, String scaleType) {
        ArrayList<Note> scale = Note.lookup(OctaveUtil.addDefaultOctave(startingNote)).getOctaveScale(scaleType, 1, true);
        Integer notesUp = romanNumeralToInteger(romanNumeral) - 1;
        Note key = scale.get(notesUp);
        char enharmonicLetter = lettersUp(startingNote, notesUp);
        return OctaveUtil.removeOctaveSpecifier(key.getEnharmonicWithLetter(enharmonicLetter)) +
                " " + getDiatonicChordQuality(romanNumeral);
    }

    /**
     * For the 'function of' command this method looks up the scale and checks if the chord note is
     * in the scale. If it is in the scale, it finds which number note it is and finds the function
     * (roman numeral) for the number note. It then checks that the quality of the chords matches
     * the quality of that chord function.
     *
     * @return If this is all ok, it will return the function. Otherwise it return 'Non Functional'.
     */
    public static String getFunctionOf(String startingNote, String chordNote, String chordType) {
        String result;
        Note noteScaleStart = Note.lookup(OctaveUtil.addDefaultOctave(startingNote));
        ArrayList<Note> scale = noteScaleStart.getScale("major", true);
        ArrayList<String> scaleNoteNames = Scale.scaleNameList(startingNote, scale, true, "");
        if (scaleNoteNames.contains(chordNote)) {
            // The note is in the scale.
            Integer numberOfNote = scaleNoteNames.indexOf(chordNote);
            String romanNumeral = ChordUtil.integerToRomanNumeral(numberOfNote + 1);
            String quality = ChordUtil.getDiatonicChordQuality(romanNumeral);
            if (quality.equals(chordType)) {
                result = romanNumeral;
            } else {
                result = "Non Functional";
            }
        } else {
            result = "Non Functional";
        }
        return result;
    }

    /**
     * Converts the given string of a roman numeral to an integer.
     *
     * @return the integer value of the roman numeral
     */
    public static Integer romanNumeralToInteger(String romanNumeral) {
        return roman.get(romanNumeral.toUpperCase());
    }

    /**
     * Converts the given integer into a roman numeral.
     *
     * @return the string roman numeral
     */
    public static String integerToRomanNumeral(Integer number) {
        return roman.inverseBidiMap().get(number);
    }

    /**
     * This function finds the letter name for the note that is a certain number of notesUp.
     *
     * @param startingNote The note the scale begins on.
     * @param notesUp      the number of letters to go up.
     * @return the letter that the note should be named.
     */
    public static char lettersUp(String startingNote, Integer notesUp) {
        char letter = startingNote.toUpperCase().charAt(0);
        int index = "ABCDEFG".indexOf(letter);
        index = (index + notesUp) % 7;
        return "ABCDEFG".charAt(index);
    }


}
