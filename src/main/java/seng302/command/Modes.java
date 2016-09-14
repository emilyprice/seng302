package seng302.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import seng302.Environment;
import seng302.data.ModeHelper;
import seng302.data.Note;
import seng302.utility.musicNotation.OctaveUtil;

/**
 * The Major Modes command class deals with two command types. The first is the "mode of" command.
 * This takes a tonic and degree, and displays the corresponding major mode. The second is the
 * "parent of" command. This takes a note and major mode, and displays the major scale of which this
 * is a mode.
 */
public class Modes implements Command {

    private String typedTonic;
    private Note tonic;
    private boolean octaveSpecified;
    private Integer degree;
    private String startNote;
    private String scaleType;

    private String commandType;

    private static ArrayList<String> melodicMinorModes = new ArrayList<>(Arrays.asList("minormajor", "dorian b2", "lydian #5", "lydian dominant", "mixolydian b6", "locrian #2", "altered"));

    private static ArrayList<String> majorNotes = new ArrayList<>(Arrays.asList("C", "C#", "Db", "D", "D", "D#", "Eb", "E", "Fb", "E#", "F", "F#", "Gb", "G", "G#", "Ab", "A", "A#", "Bb", "B", "B#", "Cb"));


    /**
     * This constructor is used for the "mode of" command.
     *
     * @param scale  The scale we are finding the mode for
     * @param degree The degree of the mode we are looking for - 1 through 7 are valid
     */
    public Modes(HashMap<String, String> scale, String degree) {
        String tonic = scale.get("note");
        this.scaleType = scale.get("scale_type").toLowerCase();
        this.commandType = "modeOf";
        octaveSpecified = OctaveUtil.octaveSpecifierFlag(tonic);
        this.typedTonic = tonic;
        this.tonic = Note.lookup(OctaveUtil.addDefaultOctave(tonic));
        this.degree = Integer.valueOf(degree);

    }

    /**
     * This constructor is used for the "parent of" command.
     *
     * @param scale The major mode scale to find the parent of.
     */
    public Modes(HashMap<String, String> scale) {
        this.commandType = "parentOf";
        this.startNote = scale.get("note");
        this.scaleType = scale.get("scale_type").toLowerCase();
        this.tonic = Note.lookup(OctaveUtil.addDefaultOctave(startNote));
    }

    /**
     * Takes a parent scale note and a degree, and returns the mode of the corresponding parent
     * scale and degree as a string
     *
     * @return result
     */
    public static String getCorrespondingScaleString(String typedTonic, Integer degree, String scaleType) {
        Note tonic = Note.lookup(OctaveUtil.addDefaultOctave(typedTonic));

        if (degree >= 1 && degree <= 7) {
            ArrayList<String> modeScale = Scale.scaleNameList(typedTonic, tonic.getScale(scaleType, true), true, scaleType);
            String parentNote = modeScale.get(degree - 1);
            String mode = null;
            if (scaleType.equals("major")) {
                mode = ModeHelper.getMajorValueModes().get(degree);
            } else {
                mode = ModeHelper.getMelodicMinorValueModes().get(degree);
            }
            return parentNote + " " + mode;
        } else {
            return "Invalid degree: " + degree + ". Please use degree in range 1-7.";
        }

    }

    /**
     * Takes a mode note of a scale and its type (e.g Dorian) and returns the parent scale it
     * belongs to as a string
     *
     * @return parentScale
     */
    public static String getMajorParentScaleString(String startNote, String scaleType) {
        Integer degree = ModeHelper.getMajorKeyModes().get(scaleType);
        for (String note : majorNotes) {
            Note tonic = Note.lookup(OctaveUtil.addDefaultOctave(note));
            ArrayList<String> majorScale = Scale.scaleNameList(note, tonic.getScale("major", true), true, scaleType);
            if (majorScale.get(degree - 1).equals(startNote)) {
                return note + " major";
            }
        }

        return "Start note did not match";
    }


    /**
     * The logic for the "parent of" command. Gets the name of the parent scale given a major mode
     * scale for the transcript manager
     */
    public void getMajorParentScale(Environment env) {
        degree = ModeHelper.getMajorKeyModes().get(scaleType);
        for (String note : majorNotes) {
            Note tonic = Note.lookup(OctaveUtil.addDefaultOctave(note));
            ArrayList<String> majorScale = Scale.scaleNameList(note, tonic.getScale("major", true), true, scaleType);
            if (majorScale.get(degree - 1).equalsIgnoreCase(startNote)) {
                env.getTranscriptManager().setResult(note + " major");
                return;
            }
        }
        env.error("Invalid note/scale type. Please check they are spelt correctly");


    }

    /**
     * Takes a mode note of a scale and its type (e.g Dorian) and returns the parent scale it
     * belongs to as a string
     *
     * @return parentScale
     */
    public static String getMelodicMinorParentScaleString(String startNote, String scaleType) {
        Integer degree = ModeHelper.getMelodicMinorKeyModes().get(scaleType);
        for (String note : majorNotes) {
            Note tonic = Note.lookup(OctaveUtil.addDefaultOctave(note));
            ArrayList<String> modeScale = Scale.scaleNameList(note, tonic.getScale("melodic minor", true), true, scaleType);
            if (modeScale.get(degree - 1).equals(startNote)) {
                return note + " melodic minor";
            }
        }

        return "Start note did not match";
    }


    /**
     * The logic for the "parent of" command. Gets the name of the parent scale given a major mode
     * scale for the transcript manager
     */
    public void getMelodicMinorParentScale(Environment env) {
        degree = ModeHelper.getMelodicMinorKeyModes().get(scaleType);
        for (String note : majorNotes) {
            Note tonic = Note.lookup(OctaveUtil.addDefaultOctave(note));
            ArrayList<String> melodicMinorScale = Scale.scaleNameList(note, tonic.getScale("melodic minor", true), true, scaleType);
            if (melodicMinorScale.get(degree - 1).equalsIgnoreCase(startNote)) {
                env.getTranscriptManager().setResult(note + " melodic minor");
                return;
            }
        }
        env.error("Invalid note/scale type. Please check they are spelt correctly");

    }


    /**
     * The logic for the "mode of" command. Gets the name of the major mode, given a tonic and mode
     * degree for the transcript manager. Displays this information in the transcript. An error is
     * shown if the provided degree is outside the accepted range
     *
     * @param env The environment in which the result will be shown.
     */
    public void getCorrespondingMajorScale(Environment env) {
        if (degree >= 1 && degree <= 7) {
            ArrayList<String> majorScale = Scale.scaleNameList(typedTonic, tonic.getScale("major", true), true, "major");
            String parentNote = majorScale.get(degree - 1);
            String mode = ModeHelper.getMajorValueModes().get(degree);
            env.getTranscriptManager().setResult(parentNote + " " + mode);
        } else {
            env.error("Invalid degree: " + degree + ". Please use degree in range 1-7.");
        }

    }

    /**
     * The logic for the "mode of" command. Gets the name of the major mode, given a tonic and mode
     * degree for the transcript manager. Displays this information in the transcript. An error is
     * shown if the provided degree is outside the accepted range
     *
     * @param env The environment in which the result will be shown.
     */
    public void getCorrespondingMelodicMinorModeScale(Environment env) {
        if (degree >= 1 && degree <= 7) {
            ArrayList<String> mmScale = Scale.scaleNameList(typedTonic, tonic.getScale("melodic minor", true), true, "melodic minor");
            String parentNote = mmScale.get(degree - 1);
            String mode = ModeHelper.getMelodicMinorValueModes().get(degree);
            env.getTranscriptManager().setResult(parentNote + " " + mode);
        } else {
            env.error("Invalid degree: " + degree + ". Please use degree in range 1-7.");
        }

    }

    @Override
    public void execute(Environment env) {
        if (commandType.equals("modeOf") && scaleType.equals("major")) {
            getCorrespondingMajorScale(env);
        } else if (commandType.equals("modeOf")) {
            getCorrespondingMelodicMinorModeScale(env);
        } else if (commandType.equals("parentOf")) {
            if (melodicMinorModes.contains(scaleType)) {
                getMelodicMinorParentScale(env);
            } else {
                getMajorParentScale(env);
            }
        }
    }

    @Override
    public String getHelp() {
        if (commandType.equals("modeOf")) {
            return "Given a starting note and a degree, provides the corresponding scale" +
                    " of the given note and the major mode of the given degree.";
        } else {
            return "Given a note and a major mode name, displays the name of the corresponding " +
                    "major scale.";
        }
    }

    @Override
    public String getCommandText() {
        if (commandType.equals("modeOf")) {
            return "mode of";
        } else {
            return "parent of";
        }
    }

    @Override
    public String getExample() {
        if (commandType.equals("modeOf")) {
            return "mode of C 2";
        } else {
            return "parent of A phrygian";
        }
    }

    @Override
    public List<String> getParams() {
        List<String> params = new ArrayList<>();
        if (commandType.equals("modeOf")) {
            params.add("note");
            params.add("1-7");
        } else {
            params.add("note");
            params.add("major mode type");
        }
        return params;
    }
}
