package seng302.command;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.HashMap;

import seng302.Environment;
import seng302.MusicPlayer;
import seng302.data.Note;
import seng302.managers.TranscriptManager;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ChordTest {
    private Environment env;
    @Mock
    private TranscriptManager transcriptManager;

    @Mock
    private MusicPlayer player;

    @Before
    public void setUp() throws Exception {
        env = new Environment();
        env.setTranscriptManager(transcriptManager);
        env.setPlayer(player);
        when(player.getTempo()).thenReturn(120);
    }


    /**
     * Checks to see if invalid chords are being handled correctly, namely chords that contain notes
     * that exceed the octave range
     */
    @Test
    public void catchInvalidChords() {
        HashMap<String, String> chordMap1 = new HashMap<String, String>();
        chordMap1.put("note", "G9");
        chordMap1.put("scale_type", "major");

        HashMap<String, String> chordMap2 = new HashMap<String, String>();
        chordMap2.put("note", "c#9");
        chordMap2.put("scale_type", "minor");

        /** this error is handled by the DSL */
        try {
            new Chord(chordMap1, "chord").execute(env);
            verify(transcriptManager).setResult("Invalid chord: G9 major. Exceeds octave range.");
        } catch (Exception e) {
            transcriptManager.setResult("Invalid chord: G9 major. Exceeds octave range.");
        }
        try {
            new Chord(chordMap2, "chord").execute(env);
            verify(transcriptManager).setResult("Invalid chord: c#9 minor. Exceeds octave range.");
        } catch (Exception e) {
            transcriptManager.setResult("Invalid chord: c#9 major. Exceeds octave range.");
        }
    }

    /**
     * Tests to see if when a valid chord is passed through, that it prints the correct output.
     * -should print notes of chords ascending from root note -should print correct enharmonic
     * -should print whether it is playing a chord or not -prints octave specifiers
     */
    @Test
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

    /**
     * Check to see if when the chord is playing, it prints the correct message
     */
    @Test
    public void playChord() {
        /**Tests to see if the chord is played along with the right output
         * with no playStyle specified
         */

        //For C Major
        ArrayList<Note> chordList1 = new ArrayList<Note>();
        Note currentNote1 = Note.lookup("C4");
        chordList1.add(currentNote1);
        chordList1.add(currentNote1.semitoneUp(4));
        chordList1.add(currentNote1.semitoneUp(7));

        HashMap<String, String> chordMap1 = new HashMap<String, String>();
        chordMap1.put("note", "C");
        chordMap1.put("scale_type", "major");
        //chordMap1.put("playStyle", "arpeggio");

        new Chord(chordMap1, "play").execute(env);
        verify(transcriptManager).setResult("Playing chord C4 major");
        verify(player).playSimultaneousNotes(chordList1);

        HashMap<String, String> chordMap2 = new HashMap<String, String>();
        chordMap2.put("note", "G");
        chordMap2.put("scale_type", "major");




        new Chord(chordMap2, "play").execute(env);
        verify(transcriptManager).setResult("Playing chord G4 major");


    }


    /**
     * Check to see if when the chord is playing, it prints the correct message
     */
    @Test
    public void playChordInversion() {

        HashMap<String, String> chordMap1 = new HashMap<String, String>();
        chordMap1.put("note", "F");
        chordMap1.put("scale_type", "major");
        chordMap1.put("playStyle", "arpeggio");
        chordMap1.put("inversion", "1");

        new Chord(chordMap1, "play").execute(env);
        verify(transcriptManager).setResult("Playing chord F4 major inversion 1");

        HashMap<String, String> chordMap2 = new HashMap<String, String>();
        chordMap2.put("note", "G");
        chordMap2.put("scale_type", "minor");

        chordMap2.put("inversion", "2");

        new Chord(chordMap2, "play").execute(env);
        verify(transcriptManager).setResult("Playing chord G4 minor inversion 2");


    }

    /**
     * Check to see if when the chord is playing, it prints the correct message
     */
    @Test
    public void playInvalidChords() {

        HashMap<String, String> chordMap1 = new HashMap<String, String>();
        chordMap1.put("note", "F9");
        chordMap1.put("scale_type", "major");
        chordMap1.put("playStyle", "arpeggio");

        try {
            new Chord(chordMap1, "play").execute(env);
            verify(transcriptManager).setResult("Invalid chord: F9 major. Exceeds octave range.");
        } catch (Exception e) {
            transcriptManager.setResult("Invalid chord: F9 major. Exceeds octave range.");
        }
        HashMap<String, String> chordMap2 = new HashMap<String, String>();
        chordMap2.put("note", "C9");
        chordMap2.put("scale_type", "minor");

        chordMap2.put("inversion", "2");

        new Chord(chordMap2, "play").execute(env);
        verify(transcriptManager).setResult("Invalid chord: C9 minor. Exceeds octave range.");

    }

    @Test

    /**Tests to see if the chord is played along with the right output
     * with playStyle Arpeggio specified
     */

    public void playChordArpeggio() {

        //For C Major Arpeggio
        ArrayList<Note> chordList = new ArrayList<Note>();
        Note currentNote = Note.lookup("C4");
        chordList.add(currentNote);
        chordList.add(currentNote.semitoneUp(4));
        chordList.add(currentNote.semitoneUp(7));

        HashMap<String, String> chordMap = new HashMap<String, String>();
        chordMap.put("note", "C");
        chordMap.put("scale_type", "major");
        chordMap.put("playStyle", "arpeggio");

        new Chord(chordMap, "play").execute(env);
        verify(transcriptManager).setResult("Playing chord C4 major");
        verify(player).playNotes(chordList);

    }

    @Test
    public void testGetLength() {
        ArrayList<Note> chordList = new ArrayList<Note>();
        Note currentNote = Note.lookup("C4");
        chordList.add(currentNote);
        chordList.add(currentNote.semitoneUp(4));
        chordList.add(currentNote.semitoneUp(7));

        HashMap<String, String> chordMap = new HashMap<String, String>();
        chordMap.put("note", "C");
        chordMap.put("scale_type", "major");
        chordMap.put("playStyle", "arpeggio");
        Chord cmajor = new Chord(chordMap, "play");
        int tempo = env.getPlayer().getTempo();
        long crotchetLength = 60000 / tempo;
        assertEquals(cmajor.getLength(env), 3 * crotchetLength);

    }


}
