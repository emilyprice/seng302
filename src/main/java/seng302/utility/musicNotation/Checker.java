package seng302.utility.musicNotation;


import java.util.Arrays;

/**
 * Created by isabelle on 2/03/16.
 */
public class Checker {

    /**
     * This method checks that a note is valid. It just checks for the 128 true note names at this
     * point and will not allow enharmonics.
     */
    public static boolean isValidNormalNote(String note) {
        return note.matches("^[A-G|a-g]([#|b|x]|(bb))?[0-8]?$|^[D-G|d-g]([#|b|x]|(bb))?[-1]?$|^[A-F|a-f]([#|b|x]|(bb))?[9]?$|^[C|c][#|x]?(-1)?$|^[G|g](b|bb)?[9]?$");
    }

    /**
     * This method will return true if it is a note without and octave. E.g. G#
     *
     * @return true if valid note, false otherwise.
     */
    public static boolean isValidNoteNoOctave(String note) {
        return note.matches("^[A-G|a-g]([#|b|x]|(bb))?$");
    }

    /**
     * @return true if valid MIDI note in range 0-127, false otherwise.
     */
    public static boolean isValidMidiNote(String note) {
        return note.matches("^(0?[0-9]?[0-9]|1[01][0-9]|12[0-7])$");
    }

    /**
     * Checks if the string is the name of a command.
     *
     * @return true if it is a command, false otherwise.
     */
    public static boolean isCommand(String c) {

        String[] helpCommands = {"midi", "note", "semitone up", "semitone down", "help"};
        return Arrays.asList(helpCommands).contains(c.toLowerCase());

    }

    public static boolean isDoubleSharp(String name) {
        return name.matches("^.x$");
    }

    public static boolean isDoubleFlat(String name) {
        return name.matches("^.bb$");
    }
}
