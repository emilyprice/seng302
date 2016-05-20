package seng302.command;

import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.*;


import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashMap;

import seng302.command.Chord;
import seng302.Environment;
import seng302.managers.TranscriptManager;

@RunWith(MockitoJUnitRunner.class)
public class ChordTest {
    private Environment env;
    @Mock
    private TranscriptManager transcriptManager;

    @Before
    public void setUp() throws Exception {
        env = new Environment();
        env.setTranscriptManager(transcriptManager);
    }


    @Test
    /**
     * Checks to see if invalid chords are being handled correctly, namely chords that contain
     * notes that exceed the octave range
     */
    public void catchInvalidChords() {

        HashMap<String, String> chordMap1 = new HashMap<String, String>();
        chordMap1.put("note", "G9");
        chordMap1.put("scale_type", "major");

        HashMap<String, String> chordMap2 = new HashMap<String, String>();
        chordMap2.put("note", "c#9");
        chordMap2.put("scale_type", "minor");

        /** this error is handled by the DSL */
        //HashMap<String, String> chordMap3 = new HashMap<String, String>();
        //chordMap3.put("note", "100");
        //chordMap3.put("scale_type", "major");

        new Chord(chordMap1, "chord").execute(env);
        verify(transcriptManager).setResult("Invalid chord: G9 major. Exceeds octave range.");
        new Chord(chordMap2, "chord").execute(env);
        verify(transcriptManager).setResult("Invalid chord: c#9 minor. Exceeds octave range.");
        //new Chord(chordMap3, "chord").execute(env);
        //verify(transcriptManager).setResult("[ERROR] Invalid command. new Chord(chordMap1, "play").execute(env)//
    }

    @Test
    /**
     * Tests to see if when a valid chord is passed through, that it prints the correct
     * output.
     * -should print notes of chords ascending from root note
     * -should print correct enharmonic
     * -should print whether it is playing a chord or not
     * -prints octave specifiers
     *
     */
    public void correctChordPrintOut() {

        HashMap<String, String> chordMap1 = new HashMap<String, String>();
        chordMap1.put("note", "C4");
        chordMap1.put("scale_type", "major");

        HashMap<String, String> chordMap2 = new HashMap<String, String>();
        chordMap2.put("note", "G");
        chordMap2.put("scale_type", "minor");

        HashMap<String, String> chordMap3 = new HashMap<String, String>();
        chordMap3.put("note", "C");
        chordMap3.put("scale_type", "major");

        HashMap<String, String> chordMap4 = new HashMap<String, String>();
        chordMap4.put("note", "C4");
        chordMap4.put("chord_type", "dim 7th");

        HashMap<String, String> chordMap5 = new HashMap<String, String>();
        chordMap5.put("note", "C");
        chordMap5.put("chord_type", "diminished seventh");

        HashMap<String, String> chordMap6 = new HashMap<String, String>();
        chordMap6.put("note", "C");
        chordMap6.put("chord_type", "major 7th");

        new Chord(chordMap1, "chord").execute(env);
        verify(transcriptManager).setResult("C4 E4 G4 ");

        new Chord(chordMap2, "chord").execute(env);
        verify(transcriptManager).setResult("G Bb D ");

        new Chord(chordMap3, "chord").execute(env);
        verify(transcriptManager).setResult("C E G ");

        new Chord(chordMap4, "chord").execute(env);
        verify(transcriptManager).setResult("C4 Eb4 Gb4 Bbb4 ");

        new Chord(chordMap5, "chord").execute(env);
        verify(transcriptManager).setResult("C Eb Gb Bbb ");

        new Chord(chordMap6, "chord").execute(env);
        verify(transcriptManager).setResult("C E G B ");



    }
    @Test
    /**Check to see if when the chord is playing, it prints the correct message
     *
     */
    public void playChord() {

        HashMap<String, String> chordMap1 = new HashMap<String, String>();
        chordMap1.put("note", "C");
        chordMap1.put("scale_type", "major");
        chordMap1.put("playStyle", "arpeggio");

        HashMap<String, String> chordMap2 = new HashMap<String, String>();
        chordMap2.put("note", "G");
        chordMap2.put("scale_type", "major");

        HashMap<String, String> chordMap3 = new HashMap<String, String>();
        chordMap2.put("note", "G");
        chordMap2.put("chord_type", "major 7th");

        HashMap<String, String> chordMap4 = new HashMap<String, String>();
        chordMap1.put("note", "C");
        chordMap1.put("chord_type", "half dim");
        chordMap1.put("playStyle", "arpeggio");


        new Chord(chordMap1, "play").execute(env);
        verify(transcriptManager).setResult("Playing chord C major");

        new Chord(chordMap2, "play").execute(env);
        verify(transcriptManager).setResult("Playing chord G major");

        new Chord(chordMap3, "play").execute(env);
        verify(transcriptManager).setResult("Playing chord G major 7th");

        new Chord(chordMap4, "play").execute(env);
        verify(transcriptManager).setResult("Playing chord C half dim");


    }


}
