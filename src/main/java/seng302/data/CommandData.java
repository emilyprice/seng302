package seng302.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import seng302.command.Chord;
import seng302.command.ChordFinder;
import seng302.command.Command;
import seng302.command.CrotchetDuration;
import seng302.command.Diatonic;
import seng302.command.Enharmonic;
import seng302.command.Help;
import seng302.command.InstrumentCommand;
import seng302.command.IntervalCommand;
import seng302.command.KeySignatureCommand;
import seng302.command.Modes;
import seng302.command.Midi;
import seng302.command.MusicalTerm;
import seng302.command.NoteCommand;
import seng302.command.PlayNote;
import seng302.command.Rhythm;
import seng302.command.Scale;
import seng302.command.Semitone;
import seng302.command.Tempo;
import seng302.command.Twinkle;
import seng302.command.UndoRedo;
import seng302.command.Version;

/**
 * This Data class holds a single instance of every command, to be used for fetching help info.
 * Also contains a collection of lists for sorting the commands into type.
 */
public class CommandData {

    //This map is for getting a command based on the keyword - this allows us to get relevant info
    public static TreeMap<String, Command> keywordToCommand = new TreeMap<>();

    // These lists hold different command "types"
    public static List<Command> playCommands = new ArrayList<>();
    public static List<Command> showCommands = new ArrayList<>();
    public static List<Command> translationCommands = new ArrayList<>();
    public static List<Command> termCommands = new ArrayList<>();
    public static List<Command> settingsCommands = new ArrayList<>();
    public static List<Command> specialCommands = new ArrayList<>();
    public static List<Command> allCommands = new ArrayList<>();


    // Populate the map and lists
    static {
        HashMap chordInfo = new HashMap<>();
        chordInfo.put("scale_type", "major");
        chordInfo.put("note", "C4");

        keywordToCommand.put("chord", new Chord(chordInfo, "chord"));
        keywordToCommand.put("play chord", new Chord(chordInfo, "play"));


        ArrayList chordNotes = new ArrayList();
        chordNotes.add(Note.lookup("A4"));
        chordNotes.add(Note.lookup("C4"));
        chordNotes.add(Note.lookup("F4"));

        keywordToCommand.put("find chord", new ChordFinder(chordNotes, false));
        keywordToCommand.put("find chord all", new ChordFinder(chordNotes, true));

        keywordToCommand.put("crotchet duration", new CrotchetDuration());

        HashMap diatonicInfo = new HashMap<>();
        diatonicInfo.put("function", "");
        diatonicInfo.put("note", "C4");
        diatonicInfo.put("scale_type", "major");

        keywordToCommand.put("chord function", new Diatonic(diatonicInfo));

        HashMap diatonicInfo2 = new HashMap<>();
        diatonicInfo2.put("chord_type", "");
        diatonicInfo2.put("scaleNote", "C4");
        diatonicInfo2.put("chordNote", "C4");

        keywordToCommand.put("function of", new Diatonic(diatonicInfo2));
        keywordToCommand.put("quality of", new Diatonic("II"));

        keywordToCommand.put("simple enharmonic", new Enharmonic("C", 2));
        keywordToCommand.put("enharmonic higher", new Enharmonic("C", 0));
        keywordToCommand.put("enharmonic lower", new Enharmonic("C", 1));
        keywordToCommand.put("all enharmonics", new Enharmonic("C", 3));

        keywordToCommand.put("instrument", new InstrumentCommand("current"));
        keywordToCommand.put("all instruments", new InstrumentCommand("all"));

        ArrayList<String> instrument = new ArrayList<>();
        instrument.add("10");
        keywordToCommand.put("set instrument", new InstrumentCommand(true, instrument));


        HashMap intervalInfo = new HashMap();

        keywordToCommand.put("interval", new IntervalCommand(intervalInfo, "note"));
        keywordToCommand.put("play interval", new IntervalCommand(intervalInfo, "play"));
        keywordToCommand.put("interval enharmonic", new IntervalCommand(intervalInfo, "equivalent"));

        keywordToCommand.put("scale signature with", new KeySignatureCommand(new ArrayList<String>()));

        HashMap keySigInfo = new HashMap();
        keySigInfo.put("note", "C4");

        keywordToCommand.put("scale signature num", new KeySignatureCommand(keySigInfo, "number"));
        keywordToCommand.put("scale signature", new KeySignatureCommand(keySigInfo, "notes"));

        keywordToCommand.put("midi", new Midi("C"));

        keywordToCommand.put("category of", new MusicalTerm("term", "category"));
        keywordToCommand.put("origin of", new MusicalTerm("term", "origin"));
        keywordToCommand.put("meaning of", new MusicalTerm("term", "meaning"));

        ArrayList musicalTermArray = new ArrayList();
        musicalTermArray.add("a");
        musicalTermArray.add("b");
        musicalTermArray.add("c");
        musicalTermArray.add("d");
        keywordToCommand.put("add musical term", new MusicalTerm(musicalTermArray));

        keywordToCommand.put("note", new NoteCommand("1"));

        keywordToCommand.put("play", new PlayNote("C"));

        keywordToCommand.put("set rhythm", new Rhythm("heavy", false));
        keywordToCommand.put("rhythm", new Rhythm());

        HashMap scaleInfo = new HashMap<>();
        scaleInfo.put("scale_type", "major");
        scaleInfo.put("note", "C4");

        keywordToCommand.put("scale", new Scale(scaleInfo, "note"));
        keywordToCommand.put("midi scale", new Scale(scaleInfo, "midi"));
        keywordToCommand.put("play scale", new Scale(scaleInfo, "play"));


        keywordToCommand.put("semitone up", new Semitone("C", true));
        keywordToCommand.put("semitone down", new Semitone("C", false));

        keywordToCommand.put("force set tempo", new Tempo("120", true));
        keywordToCommand.put("set tempo", new Tempo("120", false));
        keywordToCommand.put("tempo", new Tempo());

        keywordToCommand.put("version", new Version());
        keywordToCommand.put("help", new Help());
        keywordToCommand.put("undo", new UndoRedo(0));
        keywordToCommand.put("redo", new UndoRedo(1));
        keywordToCommand.put("twinkle", new Twinkle());

        keywordToCommand.put("get mode", new Modes(scaleInfo, "2"));
        scaleInfo.remove("scale_type");
        scaleInfo.put("scale_type", "phrygian");
        keywordToCommand.put("parent of", new Modes(scaleInfo));


        //These are the commands we want in the 'terms' category
        List<String> terms = new ArrayList<>();
        terms.add("origin of");
        terms.add("meaning of");
        terms.add("category of");
        terms.add("add musical term");

        //These are the commands we want in the 'special' category
        List<String> special = new ArrayList<>();
        special.add("twinkle");
        special.add("undo");
        special.add("redo");
        special.add("version");
        special.add("help");

        //These are the commands we want in the 'translation' category
        List<String> translation = new ArrayList<>();
        translation.add("note");
        translation.add("midi");


        for (Map.Entry command : keywordToCommand.entrySet()) {
            Command thisCommand = (Command) command.getValue();
            allCommands.add(thisCommand);
            if (thisCommand.getCommandText().contains("play")) {
                //Put play commands in their own category
                playCommands.add(thisCommand);
            } else if (thisCommand.getCommandText().contains("set ")) {
                //Put all settings commands in their own category
                settingsCommands.add(thisCommand);
            } else if (terms.contains(thisCommand.getCommandText())) {
                termCommands.add(thisCommand);
            } else if (special.contains(thisCommand.getCommandText())) {
                specialCommands.add(thisCommand);
            } else if (translation.contains(thisCommand.getCommandText())) {
                translationCommands.add(thisCommand);
            } else {
                showCommands.add(thisCommand);
            }
        }

    }

    /**
     * Returns a specific category of commands as a list.
     *
     * @param commandsToGet The category of command we are looking for
     * @return an ArrayList of commands belonging to the provided category.
     */
    public static List<Command> getCommands(String commandsToGet) {
        if (commandsToGet.equals("Play")) {
            return playCommands;
        }
        if (commandsToGet.equals("Show")) {
            return showCommands;
        }
        if (commandsToGet.equals("Special")) {
            return specialCommands;
        }
        if (commandsToGet.equals("Translation")) {
            return translationCommands;
        }
        if (commandsToGet.equals("Terms")) {
            return termCommands;
        }
        if (commandsToGet.equals("Settings")) {
            return settingsCommands;
        }
        return allCommands;
    }
}
