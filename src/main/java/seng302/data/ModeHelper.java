package seng302.data;


import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import seng302.command.Scale;
import seng302.utility.musicNotation.OctaveUtil;

/**
 * Utility class to store a list of major and minor melodic valueModes, along with their corresponding degree.
 */
public class ModeHelper {


    private static Map valueModes = new HashMap<Integer, String>();
    private static Map keyModes = new HashMap<String, Integer>();
    private static Map modeNoteMap = new HashMap<String, ArrayList>();

    private static Map mmValueModes = new HashMap<Integer, String>();
    private static Map mmKeyModes = new HashMap<String, Integer>();
    private static Map mmModeNoteMap = new HashMap<String, ArrayList>();

    static {

        valueModes.put(1, "ionian");
        valueModes.put(2, "dorian");
        valueModes.put(3, "phrygian");
        valueModes.put(4, "lydian");
        valueModes.put(5, "mixolydian");
        valueModes.put(6, "aeolian");
        valueModes.put(7, "locrian");

        mmValueModes.put(1, "minormajor");
        mmValueModes.put(2, "dorian b2");
        mmValueModes.put(3, "lydian #5");
        mmValueModes.put(4, "lydian dominant");
        mmValueModes.put(5, "mixolydian b6");
        mmValueModes.put(6, "locrian #2");
        mmValueModes.put(7, "altered");

    }

    static {

        keyModes.put("ionian", 1);
        keyModes.put("dorian", 2);
        keyModes.put("phrygian", 3);
        keyModes.put("lydian", 4);
        keyModes.put("mixolydian", 5);
        keyModes.put("aeolian", 6);
        keyModes.put("locrian", 7);

        mmKeyModes.put("minormajor", 1);
        mmKeyModes.put("dorian b2", 2);
        mmKeyModes.put("lydian #5", 3);
        mmKeyModes.put("lydian dominant", 4);
        mmKeyModes.put("mixolydian b6", 5);
        mmKeyModes.put("locrian #2", 6);
        mmKeyModes.put("altered", 7);

    }

    static {

        modeNoteMap.put(1, new ArrayList<>(Arrays.asList("C", "G", "D", "A", "E", "B", "F", "Bb", "Eb", "Ab", "Db", "Gb")));
        modeNoteMap.put(2, new ArrayList<>(Arrays.asList("D", "A", "E", "B", "F#", "C#", "G", "C", "F", "Bb", "Eb", "Ab")));
        modeNoteMap.put(3, new ArrayList<>(Arrays.asList("E", "B", "F#", "C#", "G#", "D#", "A", "D", "G", "C", "F", "Bb")));
        modeNoteMap.put(4, new ArrayList<>(Arrays.asList("F", "C", "G", "D", "A", "E", "Bb", "Eb", "Ab", "Dd", "Gb", "Cb")));
        modeNoteMap.put(5, new ArrayList<>(Arrays.asList("G", "D", "A", "E", "B", "F#", "C", "F", "Bb", "Eb", "Ab", "Db")));
        modeNoteMap.put(6, new ArrayList<>(Arrays.asList("A", "E", "B", "F#", "C#", "G#", "D", "G", "C", "F", "Bb", "EB")));
        modeNoteMap.put(7, new ArrayList<>(Arrays.asList("B", "F#", "C#", "G#", "D#", "A#", "E", "A", "D", "G", "C", "F")));

        mmModeNoteMap.put(1, new ArrayList<>());
        mmModeNoteMap.put(2, new ArrayList<>());
        mmModeNoteMap.put(3, new ArrayList<>());
        mmModeNoteMap.put(4, new ArrayList<>());
        mmModeNoteMap.put(5, new ArrayList<>());
        mmModeNoteMap.put(6, new ArrayList<>());
        mmModeNoteMap.put(7, new ArrayList<>());


        ArrayList<String> tonics = new ArrayList<>(Arrays.asList("C", "C#", "Db", "D", "D", "D#", "Eb", "E", "Fb", "E#", "F", "F#", "Gb", "G", "G#", "Ab", "A", "A#", "Bb", "B", "B#", "Cb"));
        for (String tonic : tonics) {
            String tonicWithOctave = OctaveUtil.addDefaultOctave(tonic);
            ArrayList<String> mmScale = (Scale.scaleNameList(tonicWithOctave, Note.lookup(tonicWithOctave).getScale("melodic minor", true), true, "melodic minor"));

            for (int i = 1; i < 8; i++) {
                ((ArrayList) mmModeNoteMap.get(i)).add(OctaveUtil.removeOctaveSpecifier(mmScale.get(i - 1)));
            }
        }
//        System.out.println(mmModeNoteMap.get(1));
//        System.out.println(mmModeNoteMap.get(2));
//        System.out.println(mmModeNoteMap.get(3));
//        System.out.println(mmModeNoteMap.get(4));
//        System.out.println(mmModeNoteMap.get(5));
//        System.out.println(mmModeNoteMap.get(6));
//        System.out.println(mmModeNoteMap.get(7));

    }


    public static Map<Integer, String> getMajorValueModes() {
        return valueModes;
    }

    public static Map<Integer, String> getMelodicMinorValueModes() {
        return mmValueModes;
    }

    public static Map<String, Integer> getMajorKeyModes() {
        return keyModes;
    }

    public static Map<String, ArrayList> getMajorModeNoteMap() {
        return modeNoteMap;
    }

    public static Map<String, Integer> getMelodicMinorKeyModes() {
        return mmKeyModes;
    }

    public static Map<String, ArrayList> getMelodicMinorModeNoteMap() {
        return mmModeNoteMap;
    }


}
