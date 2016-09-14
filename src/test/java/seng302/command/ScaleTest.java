package seng302.command;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import seng302.Environment;
import seng302.MusicPlayer;
import seng302.data.Note;
import seng302.managers.TranscriptManager;

import static org.mockito.Mockito.verify;

/**
 * Created by isabelle on 18/03/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class ScaleTest {

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
    }

    @Test
    public void setsCorrectScaleResult() {
        new Scale("C4", "major", "note").execute(env);
        verify(transcriptManager).setResult("C4 D4 E4 F4 G4 A4 B4 C5");
    }

    @Test
    public void setsCorrectScaleResultWithoutOctaveSpecifier() {
        new Scale("C", "major", "note").execute(env);
        verify(transcriptManager).setResult("C D E F G A B C");
    }

    @Test
    public void setsCorrectScaleResultWithFlat() {
        new Scale("F", "major", "note").execute(env);
        verify(transcriptManager).setResult("F G A Bb C D E F");
    }

    @Test
    public void setsCorrectScaleResultWithEnharmonicSharp() {
        new Scale("F#", "major", "note").execute(env);
        verify(transcriptManager).setResult("F# G# A# B C# D# E# F#");
    }

    @Test
    public void setsCorrectScaleResultWithTwoEnharmonicSharps() {
        new Scale("C#", "major", "note").execute(env);
        verify(transcriptManager).setResult("C# D# E# F# G# A# B# C#");
    }

    @Test
    public void setsCorrectScaleResultWithFlatScale() {
        new Scale("Fb", "major", "note").execute(env);
        verify(transcriptManager).setResult("Fb Gb Ab Bbb Cb Db Eb Fb");
    }

    @Test
    public void correctResultWithCaseInsensitivity() {
        new Scale("c#", "major", "note").execute(env);
        verify(transcriptManager).setResult("C# D# E# F# G# A# B# C#");
    }

    @Test
    public void correctResultWithCaseInsensitivityOfType() {
        new Scale("C#", "maJOr", "note").execute(env);
        verify(transcriptManager).setResult("C# D# E# F# G# A# B# C#");
    }

    @Test
    public void correctErrorMessageForDoubleSharpScale() {
        new Scale("Fx", "major", "note").execute(env);
        verify(transcriptManager).setResult("[ERROR] Invalid scale: 'Fx major'.");
    }

    // MIDI scale tests

    @Test
    public void setsCorrectScaleResultMidi() {
        new Scale("C4", "major", "midi").execute(env);
        verify(transcriptManager).setResult("60 62 64 65 67 69 71 72");
    }

    @Test
    public void setsCorrectEnharmonicScaleMidi() {
        new Scale("B#", "major", "midi").execute(env);
        verify(transcriptManager).setResult("60 62 64 65 67 69 71 72");
    }

    @Test
    public void correctErrorMessageForDoubleSharpScaleMidi() {
        new Scale("Fx", "major", "midi").execute(env);
        verify(transcriptManager).setResult("[ERROR] Invalid scale: 'Fx major'.");
    }

    @Test
    public void testPlayScale() {
        new Scale("F", "major", "play").execute(env);
        verify(transcriptManager).setResult("F G A Bb C D E F");
        verify(player).playNotes(Note.lookup("F4").getScale("major", true));
    }

    @Test
    public void testPlayCorrectScaleDown() {
        new Scale("C", "major", "play", "down").execute(env);
        verify(transcriptManager).setResult("C B A G F E D C");
        ArrayList<Note> scale = Note.lookup("C4").getScale("major", false);
        verify(player).playNotes(scale);
    }

    @Test
    public void testPlayCorrectScaleUpDown() {
        new Scale("C", "major", "play", "updown").execute(env);
        verify(transcriptManager).setResult("C D E F G A B C C B A G F E D C");
        ArrayList<Note> notesToPlay = Note.lookup("C4").getScale("major", true);
        ArrayList<Note> notes = new ArrayList<Note>(notesToPlay);
        Collections.reverse(notes);
        notesToPlay.addAll(notes);
        verify(player).playNotes(notesToPlay);
    }

    @Test
    public void testCorrectErrorInvalidDirection() {
        new Scale("C", "major", "play", "cake").execute(env);
        verify(transcriptManager).setResult("[ERROR] 'cake' is not a valid scale direction. Try 'up', 'updown' or 'down'.");
    }

    @Test
    public void testNoteTooHighForScale() {
        new Scale("C9", "major", "note").execute(env);
        verify(transcriptManager).setResult("[ERROR] This scale goes beyond the MIDI notes available.");
    }

    @Test
    public void testNoteTooLowForScale() {
        new Scale("D-1", "major", "play", "down").execute(env);
        verify(transcriptManager).setResult("[ERROR] This scale goes beyond the MIDI notes available.");
    }

    @Test
    public void testNoteTooHighForScaleTwoOctaves() {
        new Scale("C8", "major", "play", "up", "2").execute(env);
        verify(transcriptManager).setResult("[ERROR] This scale goes beyond the MIDI notes available.");
    }

    @Test
    public void testNoteTooHighForScaleUpDown() {
        new Scale("E9", "major", "play", "updown").execute(env);
        verify(transcriptManager).setResult("[ERROR] This scale goes beyond the MIDI notes available.");
    }

    @Test
    public void testPlayThreeOctavesUp() {
        new Scale("F3", "major", "play", "up", "3").execute(env);
        verify(transcriptManager).setResult("F3 G3 A3 Bb3 C4 D4 E4 F4 G4 A4 Bb4 C5 D5 E5 F5 G5 A5 Bb5 C6 D6 E6 F6");
        ArrayList<Note> scale = Note.lookup("F3").getOctaveScale("major", 3, true);
        verify(player).playNotes(scale);
    }

    @Test
    public void testPlayFourOctavesDown() {
        new Scale("G6", "major", "play", "down", "4").execute(env);
        verify(transcriptManager).setResult("G6 F#6 E6 D6 C6 B5 A5 G5 F#5 E5 D5 C5 B4 A4 G4 F#4 E4 D4 C4 B3 A3 G3 F#3 E3 D3 C3 B2 A2 G2");
        ArrayList<Note> scale = Note.lookup("G6").getOctaveScale("major", 4, false);
        verify(player).playNotes(scale);
    }

    @Test
    public void testPlayTwoOctavesUpDown() {
        new Scale("D", "major", "play", "updown", "2").execute(env);
        verify(transcriptManager).setResult("D E F# G A B C# D E F# G A B C# D D C# B A G F# E D C# B A G F# E D");
        ArrayList<Note> scale = Note.lookup("D4").getOctaveScale("major", 2, true);
        ArrayList<Note> notes = new ArrayList<Note>(scale);
        Collections.reverse(notes);
        scale.addAll(notes);
        verify(player).playNotes(scale);

    }

    // Test minor scales

    @Test
    public void testMinorScale() {
        new Scale("C", "minor", "note").execute(env);
        verify(transcriptManager).setResult("C D Eb F G Ab Bb C");
    }

    @Test
    public void testMinorScaleOctave() {
        new Scale("C4", "minor", "note").execute(env);
        verify(transcriptManager).setResult("C4 D4 Eb4 F4 G4 Ab4 Bb4 C5");
    }

    @Test
    public void testMidiMinorScale() {
        new Scale("C", "minor", "midi").execute(env);
        verify(transcriptManager).setResult("60 62 63 65 67 68 70 72");
    }

    @Test
    public void testPlayMinorScale() {
        new Scale("C", "minor", "play").execute(env);
        verify(transcriptManager).setResult("C D Eb F G Ab Bb C");
        verify(player).playNotes(Note.lookup("C4").getScale("minor", true));
    }

    @Test
    public void testSharpMinorScale() {
        new Scale("F#", "minor", "note").execute(env);
        verify(transcriptManager).setResult("F# G# A B C# D E F#");
    }

    @Test
    public void correctErrorMessageForDoubleSharpMinorScale() {
        new Scale("Fx", "minor", "note").execute(env);
        verify(transcriptManager).setResult("[ERROR] Invalid scale: 'Fx minor'.");
    }

    /**
     * Melodic Minor scale testing ###############################################
     */

    @Test
    public void validCMelodicMinorScale() {
        new Scale("C", "melodic minor", "note").execute(env);
        verify(transcriptManager).setResult("C D Eb F G A B C");
    }

    @Test
    public void validDMelodicMinorScale() {
        new Scale("D", "melodic minor", "note").execute(env);
        verify(transcriptManager).setResult("D E F G A B C# D");
    }

    @Test
    public void validDFlatMelodicMinorScale() {
        new Scale("Db", "melodic minor", "note").execute(env);
        verify(transcriptManager).setResult("Db Eb Fb Gb Ab Bb C Db");
    }

    @Test
    public void validFMelodicMinorScale5thOctave() {
        new Scale("F5", "melodic minor", "note").execute(env);
        verify(transcriptManager).setResult("F5 G5 Ab5 Bb5 C6 D6 E6 F6");
    }

    @Test
    public void validGSharpMelodicMinorScale() {
        new Scale("G#", "melodic minor", "note").execute(env);
        verify(transcriptManager).setResult("G# A# B C# D# E# Fx G#");
    }

    @Test
    public void testPlayCorrectMelodicMinorScaleDown() {
        new Scale("C", "melodic minor", "play", "down").execute(env);
        verify(transcriptManager).setResult("C Bb Ab G F Eb D C");
        ArrayList<Note> scale = Note.lookup("C4").getScale("melodic minor", false);
        verify(player).playNotes(scale);
    }

    @Test
    public void invalidDoubleSharpMelodicMinor() {
        new Scale("Fx", "melodic minor", "note").execute(env);
        verify(transcriptManager).setResult("[ERROR] Invalid scale: 'Fx melodic minor'.");
    }

    @Test
    public void setsCorrectMelodicMinorScaleResultMidi() {
        new Scale("C4", "melodic minor", "midi").execute(env);
        verify(transcriptManager).setResult("60 62 63 65 67 69 71 72");
    }


    @Test
    public void setsCorrectIonianResultUp() {
        new Scale("C", "ionian", "note").execute(env);
        verify(transcriptManager).setResult("C D E F G A B C");
    }

    @Test
    public void setsCorrectIonianResultDown() {
        new Scale("C", "ionian", "play", "down").execute(env);
        verify(transcriptManager).setResult("C B A G F E D C");
    }

    @Test
    public void setsCorrectDorianResultUp() {
        new Scale("C", "dorian", "note").execute(env);
        verify(transcriptManager).setResult("C D Eb F G A Bb C");
    }

    @Test
    public void setsCorrectDorianResultDown() {
        new Scale("C", "dorian", "play", "down").execute(env);
        verify(transcriptManager).setResult("C Bb A G F Eb D C");
    }

    @Test
    public void setsCorrectPhrygianResultUp() {
        new Scale("C", "phrygian", "note").execute(env);
        verify(transcriptManager).setResult("C Db Eb F G Ab Bb C");
    }

    @Test
    public void setsCorrectPhrygianResultDown() {
        new Scale("C", "phrygian", "play", "down").execute(env);
        verify(transcriptManager).setResult("C Bb Ab G F Eb Db C");
    }

    @Test
    public void setsCorrectLydianResultUp() {
        new Scale("C", "lydian", "note").execute(env);
        verify(transcriptManager).setResult("C D E F# G A B C");
    }

    @Test
    public void setsCorrectLydianResultDown() {
        new Scale("C", "lydian", "play", "down").execute(env);
        verify(transcriptManager).setResult("C B A G F# E D C");
    }

    @Test
    public void setsCorrectMixolydianResultUp() {
        new Scale("C", "mixolydian", "note").execute(env);
        verify(transcriptManager).setResult("C D E F G A Bb C");
    }

    @Test
    public void setsCorrectMixolydianResultDown() {
        new Scale("C", "mixolydian", "play", "down").execute(env);
        verify(transcriptManager).setResult("C Bb A G F E D C");
    }

    @Test
    public void setsCorrectAeolianResultUp() {
        new Scale("C", "aeolian", "note").execute(env);
        verify(transcriptManager).setResult("C D Eb F G Ab Bb C");
    }

    @Test
    public void setsCorrectAeolianResultDown() {
        new Scale("C", "aeolian", "play", "down").execute(env);
        verify(transcriptManager).setResult("C Bb Ab G F Eb D C");
    }

    @Test
    public void setsCorrectLocrianResultUp() {
        new Scale("C", "locrian", "note").execute(env);
        verify(transcriptManager).setResult("C Db Eb F Gb Ab Bb C");
    }

    @Test
    public void setsCorrectLocrianResultDown() {
        new Scale("C", "locrian", "play", "down").execute(env);
        verify(transcriptManager).setResult("C Bb Ab Gb F Eb Db C");
    }



}