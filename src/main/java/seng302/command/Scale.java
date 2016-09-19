package seng302.command;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import seng302.Environment;
import seng302.data.Note;
import seng302.utility.musicNotation.Checker;
import seng302.utility.musicNotation.OctaveUtil;

/**
 * This command is invoked when the user wants to play or list the notes of a scale.
 */
public class Scale implements Command {

    /**
     * Note to begin the scale on.
     */
    String startNote;

    /**
     * Type of scale. e.g major, minor, melodic minor, blues
     */
    private static String type = null;

    /**
     * Way to output scale. e.g note, midi or play
     */
    String outputType;

    /**
     * Indicates whether an octave was specified in the original command. This decides whether
     * octaves will be shown in the output.
     */
    private boolean octaveSpecified;

    /**
     * The note the scale begins on.
     */
    private Note note;

    /**
     * The letter the current note should be. This is used to find the correct enharmonic of a
     * note.
     */
    private char currentLetter;

    /**
     * Used to specify which direction to play a scale. Can be up, updown or down.
     */
    private String direction;

    /**
     * The number of octaves to be played.
     */
    private int octaves;


    public Scale(HashMap<String, String> scale, String outputType) {
        this.startNote = scale.get("note");
        this.type = scale.get("scale_type");
        this.outputType = outputType;
        currentLetter = Character.toUpperCase(startNote.charAt(0));
        if (scale.get("direction") != null) {
            this.direction = scale.get("direction");
        } else {
            direction = "up";
        }
        if (scale.get("octaves") != null) {
            this.octaves = Integer.valueOf(scale.get("octaves"));
        } else {
            this.octaves = 1;
        }
    }

    /**
     * This constructor does not specify a direction so it defaults to 'up'.
     *
     * @param a          The startNote.
     * @param b          The type of scale.
     * @param outputType The way to output the scale.
     */
    public Scale(String a, String b, String outputType) {
        this.startNote = a;
        this.type = b;
        this.outputType = outputType;
        currentLetter = Character.toUpperCase(startNote.charAt(0));
        direction = "up";
        octaves = 1;
    }

    /**
     * This constructor specifies the direction to play the scale.
     *
     * @param a          The start Note.
     * @param b          The scale type.
     * @param outputType The way the scale should be outputted.
     * @param direction  The direction to play the scale.
     */
    public Scale(String a, String b, String outputType, String direction) {
        this(a, b, outputType);
        this.direction = direction;
        octaves = 1;
    }

    /**
     * A constructor that takes in the number of octaves to play.
     *
     * @param a          The start note.
     * @param b          The scale type.
     * @param outputType The way the scale is outputted.
     * @param direction  The direction to play the scale.
     * @param octaves    The number of octaves of the scale to play.
     */
    public Scale(String a, String b, String outputType, String direction, String octaves) {
        this(a, b, outputType, direction);
        this.octaves = Integer.parseInt(octaves);
    }

    /**
     * Moves the current letter to the next letter. If the letter is G, the next letter will be A.
     * This method is used to ensure one of each letter name is in each scale.
     * @param currentLetter The current letter.
     * @param backDown Whether to iterate through the letters forwards or backwards.
     * @return The new letter.
     */
    private static char updateLetter(char currentLetter, boolean backDown) {
        int index = "ABCDEFG".indexOf(currentLetter);
        if (backDown) {
            if (index - 1 < 0) {
                index = 7;
            }
            currentLetter = "ABCDEFG".charAt(index - 1);
        } else {
            if (index + 1 > 6) {
                index = -1;
            }
            currentLetter = "ABCDEFG".charAt(index + 1);
        }
        return currentLetter;
    }

    /**
     * This methods gets a scale in a particular direction. For updown it will get the scale in each
     * direction and then combine them.
     *
     * @param direction the direction of the scale (up, down, updown)
     * @return An arraylist of notes of the scale in the correct direction.
     */
    private ArrayList<Note> getScale(String direction) {
        ArrayList<Note> scale = note.getOctaveScale(type, octaves, true);

        if (direction.equals("down")) {
            scale = note.getOctaveScale(type, octaves, false);
        } else if (direction.equals("updown")) {
            ArrayList<Note> notes = new ArrayList<Note>(scale);
            Collections.reverse(notes);
            scale.addAll(notes);
        }
        return scale;
    }

    /**
     * The command is executed. The beginning note is found and the scale is looked up. The result
     * is outputted or the scale is played.
     *
     * @param env The environment of the program.
     */
    public void execute(Environment env) {
        if (Checker.isDoubleFlat(startNote) || Checker.isDoubleSharp(startNote)) {
            env.error("Invalid scale: '" + startNote + ' ' + type + "'.");
        } else {
            this.note = Note.lookup(OctaveUtil.addDefaultOctave(startNote));
            try {
                ArrayList<Note> scale = getScale(direction);
                if (scale == null) {
                    env.error("This scale goes beyond the MIDI notes available.");
                } else {
                    if (this.outputType.equals("note")) {
                        String stringOfScale = scaleToString(startNote, scale, true, type);
                        env.getTranscriptManager().setResult(stringOfScale);
                    } else if (this.outputType.equals("midi")) {
                        env.getTranscriptManager().setResult(scaleToMidi(scale));
                    } else if (type.equals("blues")) {
                        if (direction.equals("updown")) {
                            env.getPlayer().playBluesNotes(scale);
                            env.getTranscriptManager().setResult(scaleToStringUpDown(startNote, scale, type));
                        } else if (direction.equals("down")) {
                            env.getPlayer().playBluesNotes(scale);
                            env.getTranscriptManager().setResult(scaleToString(startNote, scale, false, type));
                        } else if (direction.equals("up")) {
                            env.getPlayer().playBluesNotes(scale);
                            env.getTranscriptManager().setResult(scaleToString(startNote, scale, true, type));
                        } else {
                            env.error("'" + direction + "' is not a valid scale direction. Try 'up', 'updown' or 'down'.");
                        }

                    } else { // Play scale.
                        if (direction.equals("updown")) {
                            env.getPlayer().playNotes(scale);
                            env.getTranscriptManager().setResult(scaleToStringUpDown(startNote, scale, type));
                        } else if (direction.equals("down")) {
                            env.getPlayer().playNotes(scale);
                            env.getTranscriptManager().setResult(scaleToString(startNote, scale, false, type));
                        } else if (direction.equals("up")) {
                            env.getPlayer().playNotes(scale);
                            env.getTranscriptManager().setResult(scaleToString(startNote, scale, true, type));
                        } else {
                            env.error("'" + direction + "' is not a valid scale direction. Try 'up', 'updown' or 'down'.");
                        }
                    }
                }
            } catch (Exception e) {
                env.error("This scale goes beyond the MIDI notes available.");
            }
        }

    }


    /**
     * Converts an ArrayList of notes into an ArrayList of Strings where the strings are the correct
     * enharmonic name for the each name.
     *
     * @param startNote  The note the scale begins on.
     * @param scaleNotes An ArrayList containing the notes of the scale.
     * @param up         Whether the scale is going up or down.
     * @param type       The type of the scale
     * @return Arraylist of correct note names.
     */
    public static ArrayList<String> scaleNameList(String startNote, ArrayList<Note> scaleNotes, boolean up, String type) {
        ArrayList<String> scale = new ArrayList<>();
        char currentLetter = Character.toUpperCase(startNote.charAt(0));
        int count = 0;
        int whichNote = 0;
        for (Note note : scaleNotes) {
            String currentNote;
            if (type != null) {
                if (type.equals("blues")) {
                    char modifier = ' ';
                    if (startNote.length() > 1) {
                        modifier = startNote.charAt(1);
                    }
                    if ((startNote.toUpperCase().startsWith("B#") || startNote.toUpperCase().startsWith("E#")) && whichNote == 6) {
                        // Make these special cases' first and last notes match up
                        currentNote = note.getEnharmonicWithLetter(startNote.toUpperCase().charAt(0));
                    } else if (modifier == '#' || (startNote.toUpperCase().charAt(0) == 'B' && modifier != 'b')) {
                        // Flat blues scales do not use sharps,
                        // Except for the Bb blues scale for some reason.
                        currentNote = note.getNote();
                    } else if (modifier == 'b' && whichNote == 0) {
                        currentNote = note.getNote();
                    } else if (modifier == 'b' && whichNote == 6) {
                        //Makes the final note match the first note
                        currentNote = note.getEnharmonicWithLetter(startNote.toUpperCase().charAt(0));
                    } else {
                        currentNote = note.getDescendingEnharmonic();
                    }
                } else {
                    currentNote = note.getEnharmonicWithLetter(currentLetter);
                }
            } else {
                currentNote = note.getEnharmonicWithLetter(currentLetter);
            }
            if (OctaveUtil.octaveSpecifierFlag(startNote)) {
                scale.add(currentNote);
            } else {
                scale.add(OctaveUtil.removeOctaveSpecifier(currentNote));
            }
            if (up) {
                currentLetter = updateLetter(currentLetter, false);
                if (type.equals("major pentatonic")) {
                    if (count == 2) {
                        currentLetter = updateLetter(currentLetter, false);
                    } else if (count == 4) {
                        currentLetter = updateLetter(currentLetter, false);
                    }
                    count += 1;
                    if (count == 5) {
                        count = 0;
                    }
                } else if (type.equals("minor pentatonic")) {
                    if (count == 0) {
                        currentLetter = updateLetter(currentLetter, false);
                    } else if (count == 3) {
                        currentLetter = updateLetter(currentLetter, false);
                    }
                    count += 1;
                    if (count == 5) {
                        count = 0;
                    }
                }
            } else {
                currentLetter = updateLetter(currentLetter, true);
                if (type.equals("major pentatonic")) {
                    if (count == 0) {
                        currentLetter = updateLetter(currentLetter, true);
                    } else if (count == 2) {
                        currentLetter = updateLetter(currentLetter, true);
                    }
                    count += 1;
                    if (count == 5) {
                        count = 0;
                    }
                } else if (type.equals("minor pentatonic")) {
                    if (count == 1) {
                        currentLetter = updateLetter(currentLetter, true);
                    } else if (count == 4) {
                        currentLetter = updateLetter(currentLetter, true);
                    }
                    count += 1;
                    if (count == 5) {
                        count = 0;
                    }
                }
            }
            whichNote++;
        }
        return scale;
    }

    /**
     * Converts an ArrayList of Notes into a String of note names.
     *
     * @param startNote The note the scale begins on.
     * @param scaleNotes The notes to display.
     * @param up Whether the scale is ascending or descending.
     * @param type The type of the scale
     * @return The note names as a String.
     */
    private static String scaleToString(String startNote, ArrayList<Note> scaleNotes, boolean up, String type) {
        ArrayList<String> scale = scaleNameList(startNote, scaleNotes, up, type);
        return String.join(" ", scale);
    }

    /**
     * A convenience method put together the string of notes up and the string of notes downs.
     *
     * @param scaleNotes The notes of the scale.
     * @return The string of the scale notes.
     */
    private static String scaleToStringUpDown(String startNote, ArrayList<Note> scaleNotes, String type) {
        int size = scaleNotes.size();
        String up = scaleToString(startNote, new ArrayList<Note>(scaleNotes.subList(0, size / 2)), true, type);
        updateLetter(Character.toUpperCase(startNote.charAt(0)), true);
        String down = scaleToString(startNote, new ArrayList<Note>(scaleNotes.subList(size / 2, size)), false, type);
        return up + " " + down;
    }

    /**
     * Converts an ArrayList of Notes into a String of Midi numbers.
     *
     * @param scaleNotes The notes to display.
     * @return The note Midi numbers as a String.
     */
    private String scaleToMidi(ArrayList<Note> scaleNotes) {
        String midiValues = "";
        for (Note note : scaleNotes) {
            midiValues += note.getMidi() + " ";
        }
        return midiValues.trim();
    }


    public long getLength(Environment env) {
        long milliseconds = 0;

        if (outputType.equals("play")) {
            ArrayList<Note> scale = getScale(direction);
            int tempo = env.getPlayer().getTempo();
            long crotchetLength = 60000 / tempo;
            milliseconds = scale.size() * crotchetLength;
        }

        return milliseconds;
    }

    public String getHelp() {
        switch (outputType) {
            case "note":
                return "When followed by a valid scale (made up of a note and a scale type)" +
                        " the corresponding scale notes will be displayed.";
            case "play":
                return "When followed by a valid scale (made up of a note and a scale type)" +
                        " the corresponding scale will be played. The number of" +
                        " octaves and direction may optionally be given.";

            case "midi":
                return "When followed by a valid scale (made up of a note and a scale type) " +
                        "the corresponding scale midi notes will be displayed. ";

        }
        return null;
    }

    public List<String> getParams() {
        List<String> params = new ArrayList<>();
        params.add("note");
        params.add("type");
        return params;
    }

    public List<String> getOptions() {
        List<String> options = new ArrayList<>();
        if (outputType.equals("play")) {
            options.add("octaves");
            options.add("up|down|updown");
        }

        return options;
    }

    @Override
    public String getCommandText() {
        switch (outputType) {
            case "note":
                return "scale";
            case "play":
                return "play scale";

            case "midi":
                return "midi scale";

        }
        return null;
    }

    @Override
    public String getExample() {
        switch (outputType) {
            case "note":
                return "scale A minor";
            case "play":
                return "play scale C major updown";

            case "midi":
                return "midi scale A minor";

        }
        return null;
    }
}
