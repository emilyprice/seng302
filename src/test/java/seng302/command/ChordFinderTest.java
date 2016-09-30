package seng302.command;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import seng302.Environment;
import seng302.FirebaseUpdater;
import seng302.Users.UserHandler;
import seng302.data.Note;
import seng302.managers.TranscriptManager;
import seng302.utility.musicNotation.OctaveUtil;

import java.util.ArrayList;
import java.util.HashSet;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Created by Jonty on 24-May-16.
 */

@RunWith(MockitoJUnitRunner.class)
public class ChordFinderTest {

    Environment env;
    @Mock
    private TranscriptManager transcriptManager;

    @Before
    public void setUp() throws Exception {
        FirebaseUpdater mockFireBase = mock(FirebaseUpdater.class);
        UserHandler mockUserH = mock(UserHandler.class);
        env = new Environment(mockFireBase, mockUserH);
        env.setTranscriptManager(transcriptManager);
        //FirebaseUpdater mockCommand = mock(FirebaseUpdater.class);

    }

    @Test
    public void testGetLength() throws Exception {

    }


    ///////////////////////////////////////////////////////////////////////////////////
    /*
        Tests for 3-note chords without inversion flag
    */
    @Test
    public void testValidTrioNotAll_CMajor() throws Exception {
        //C Major: C E G
        ArrayList<Note> notes = new ArrayList<Note>();
        notes.add(Note.lookup(OctaveUtil.validateNoteString("C4")));
        notes.add(Note.lookup(OctaveUtil.validateNoteString("E4")));
        notes.add(Note.lookup(OctaveUtil.validateNoteString("G4")));

        new ChordFinder(notes, false).execute(env);
        verify(transcriptManager).setResult("C major");
    }

    @Test
    public void testValidTrioNotAll_CMinor() throws Exception {
        //C minor (C Eb G)
        ArrayList<Note> notes = new ArrayList<Note>();
        notes.add(Note.lookup(OctaveUtil.validateNoteString("C4")));
        notes.add(Note.lookup(OctaveUtil.validateNoteString("D#4")));
        notes.add(Note.lookup(OctaveUtil.validateNoteString("G4")));
        new ChordFinder(notes, false).execute(env);
        verify(transcriptManager).setResult("C minor");
    }

    @Test
    public void testValidTrioNotAll_FMajor() throws Exception {
        //F major (F A C)
        ArrayList<Note> notes = new ArrayList<Note>();
        notes.add(Note.lookup(OctaveUtil.validateNoteString("F")));
        notes.add(Note.lookup(OctaveUtil.validateNoteString("A")));
        notes.add(Note.lookup(OctaveUtil.validateNoteString("C")));
        new ChordFinder(notes, false).execute(env);
        verify(transcriptManager).setResult("F major");
    }

    @Test
    public void testValidTrioNotAll_FMinor() throws Exception {
        //F minor (F Ab C)
        ArrayList<Note> notes = new ArrayList<Note>();
        notes.add(Note.lookup(OctaveUtil.validateNoteString("F")));
        notes.add(Note.lookup(OctaveUtil.validateNoteString("Ab")));
        notes.add(Note.lookup(OctaveUtil.validateNoteString("C")));
        new ChordFinder(notes, false).execute(env);
        verify(transcriptManager).setResult("F minor");
    }

    @Test
    public void testValidTrioNotAll_FDiminished() throws Exception {
        //F Diminished (F Ab Cb)
        ArrayList<Note> notes = new ArrayList<Note>();
        notes.add(Note.lookup(OctaveUtil.validateNoteString("F")));
        notes.add(Note.lookup(OctaveUtil.validateNoteString("Ab")));
        notes.add(Note.lookup(OctaveUtil.validateNoteString("Cb")));
        new ChordFinder(notes, false).execute(env);
        verify(transcriptManager).setResult("F diminished");


    }

/////////////////////////////////////////////////////////////////////////////////
    /*
        tests for 4-note chords without inversion flags
     */

    @Test
    public void testValidFourNotAll_CDiminished7() throws Exception {
        //C diminished seventh (C Eb Gb Bbb)
        ArrayList<Note> notes = new ArrayList<Note>();
        notes.add(Note.lookup(OctaveUtil.validateNoteString("C4")));
        notes.add(Note.lookup(OctaveUtil.validateNoteString("Eb4")));
        notes.add(Note.lookup(OctaveUtil.validateNoteString("Gb4")));
        notes.add(Note.lookup(OctaveUtil.validateNoteString("Bbb5")));
        new ChordFinder(notes, false).execute(env);
        verify(transcriptManager).setResult("C diminished 7th");
    }

    @Test
    public void testValidFourNotAll_CHalfDimished() throws Exception {
        //C half diminished (C Eb Gb Bb)
        ArrayList<Note> notes = new ArrayList<Note>();
        notes.add(Note.lookup(OctaveUtil.validateNoteString("C")));
        notes.add(Note.lookup(OctaveUtil.validateNoteString("Eb")));
        notes.add(Note.lookup(OctaveUtil.validateNoteString("Gb")));
        notes.add(Note.lookup(OctaveUtil.validateNoteString("Bb")));
        new ChordFinder(notes, false).execute(env);
        verify(transcriptManager).setResult("C half diminished");
    }

    @Test
    public void testValidFourNotAll_FMajor7() throws Exception {
        //F major seventh (F A C E)
        ArrayList<Note> notes = new ArrayList<Note>();
        notes.add(Note.lookup(OctaveUtil.validateNoteString("F4")));
        notes.add(Note.lookup(OctaveUtil.validateNoteString("A5")));
        notes.add(Note.lookup(OctaveUtil.validateNoteString("C5")));
        notes.add(Note.lookup(OctaveUtil.validateNoteString("E5")));
        new ChordFinder(notes, false).execute(env);
        verify(transcriptManager).setResult("F major 7th");
    }

    @Test
    public void testValidFour2NotAll_FMajor7() throws Exception {
        //F major seventh (F A C E)
        ArrayList<Note> notes = new ArrayList<Note>();
        notes.add(Note.lookup(OctaveUtil.validateNoteString("F")));
        notes.add(Note.lookup(OctaveUtil.validateNoteString("A")));
        notes.add(Note.lookup(OctaveUtil.validateNoteString("C")));
        notes.add(Note.lookup(OctaveUtil.validateNoteString("E")));
        new ChordFinder(notes, false).execute(env);
        verify(transcriptManager).setResult("F major 7th");
    }

    @Test
    public void testValidFourNotAll_FMinor7() throws Exception {
        //F minor seventh (F Ab C Eb)
        ArrayList<Note> notes = new ArrayList<Note>();
        notes.add(Note.lookup(OctaveUtil.validateNoteString("F")));
        notes.add(Note.lookup(OctaveUtil.validateNoteString("Ab")));
        notes.add(Note.lookup(OctaveUtil.validateNoteString("C")));
        notes.add(Note.lookup(OctaveUtil.validateNoteString("Eb")));
        new ChordFinder(notes, false).execute(env);
        verify(transcriptManager).setResult("F minor 7th");
    }

    @Test
    public void testValidFourNotAll_G7() throws Exception {
        //G seventh (G B D F)
        ArrayList<Note> notes = new ArrayList<Note>();
        notes.add(Note.lookup(OctaveUtil.validateNoteString("G")));
        notes.add(Note.lookup(OctaveUtil.validateNoteString("B")));
        notes.add(Note.lookup(OctaveUtil.validateNoteString("D")));
        notes.add(Note.lookup(OctaveUtil.validateNoteString("F")));
        new ChordFinder(notes, false).execute(env);
        verify(transcriptManager).setResult("G seventh");
    }




//////////////////////////////////////////////////////////////////////////////////
    /*
    Tests for 3-note enharmonics
     */

    @Test
    public void testValidTrioEnharmonics_CMajor() throws Exception {
        //C Major: C E G
        ArrayList<Note> notes = new ArrayList<Note>();
        notes.add(Note.lookup(OctaveUtil.validateNoteString("C")));
        notes.add(Note.lookup(OctaveUtil.validateNoteString("E")));
        notes.add(Note.lookup(OctaveUtil.validateNoteString("G")));
        new ChordFinder(notes, false).execute(env);
        verify(transcriptManager).setResult("C major");
    }

    @Test
    public void testValidTrioEnharmonics_CMinor() throws Exception {
        //C minor (C Eb G)
        ArrayList<Note> notes = new ArrayList<Note>();
        notes.add(Note.lookup(OctaveUtil.validateNoteString("B#")));
        notes.add(Note.lookup(OctaveUtil.validateNoteString("Eb")));
        notes.add(Note.lookup(OctaveUtil.validateNoteString("Fx")));
        new ChordFinder(notes, false).execute(env);
        verify(transcriptManager).setResult("B# minor");
    }

    @Test
    public void testValidTrioEnharmonics_FMajor() throws Exception {

        //F major (F A C)
        ArrayList<Note> notes = new ArrayList<Note>();
        notes.add(Note.lookup(OctaveUtil.validateNoteString("E#")));
        notes.add(Note.lookup(OctaveUtil.validateNoteString("Gx")));
        notes.add(Note.lookup(OctaveUtil.validateNoteString("B#")));
        new ChordFinder(notes, false).execute(env);
        verify(transcriptManager).setResult("E# major");
    }


    @Test
    public void testValidTrioEnharmonics_FMinor() throws Exception {
        //F minor (F Ab C)
        ArrayList<Note> notes = new ArrayList<Note>();
        notes.add(Note.lookup(OctaveUtil.validateNoteString("E#")));
        notes.add(Note.lookup(OctaveUtil.validateNoteString("G#")));
        notes.add(Note.lookup(OctaveUtil.validateNoteString("B#")));
        new ChordFinder(notes, false).execute(env);
        verify(transcriptManager).setResult("E# minor");
    }

//////////////////////////////////////////////////////////////////////////////////
    /**
     * Tests for finding 4 note enharmonics
     */

    @Test
    public void testValidFourEnharmonics_CDiminished7() throws Exception {
        //C diminished seventh (C Eb Gb Bbb) with enharmonic (B# D# F# Bbb)
        ArrayList<Note> notes = new ArrayList<Note>();
        notes.add(Note.lookup(OctaveUtil.validateNoteString("B#4")));
        notes.add(Note.lookup(OctaveUtil.validateNoteString("D#4")));
        notes.add(Note.lookup(OctaveUtil.validateNoteString("F#4")));
        notes.add(Note.lookup(OctaveUtil.validateNoteString("Bbb5")));
        new ChordFinder(notes, false).execute(env);
        verify(transcriptManager).setResult("B# diminished 7th");
    }

    @Test
    public void testValidFourEnharmonics_CHalfDimished() throws Exception {
        //C half diminished (C Eb Gb Bb) with enharmonic (B# D# F# A#)
        ArrayList<Note> notes = new ArrayList<Note>();
        notes.add(Note.lookup(OctaveUtil.validateNoteString("B#")));
        notes.add(Note.lookup(OctaveUtil.validateNoteString("D#")));
        notes.add(Note.lookup(OctaveUtil.validateNoteString("F#")));
        notes.add(Note.lookup(OctaveUtil.validateNoteString("A#")));
        new ChordFinder(notes, false).execute(env);
        verify(transcriptManager).setResult("B# half diminished");
    }

    @Test
    public void testValidFourEnharmonics_FMajor7() throws Exception {
        //F major seventh (F A C E) with enharmonic (E# Gx B# Dx)
        ArrayList<Note> notes = new ArrayList<Note>();
        notes.add(Note.lookup(OctaveUtil.validateNoteString("E#4")));
        notes.add(Note.lookup(OctaveUtil.validateNoteString("Gx4")));
        notes.add(Note.lookup(OctaveUtil.validateNoteString("B#5")));
        notes.add(Note.lookup(OctaveUtil.validateNoteString("Dx5")));
        new ChordFinder(notes, false).execute(env);
        verify(transcriptManager).setResult("E# major 7th");
    }

    @Test
    public void testValidFourEnharmonics_FMinor7() throws Exception {
        //F minor seventh (F Ab C Eb) with enharmonic (E# Gx B# D#)
        ArrayList<Note> notes = new ArrayList<Note>();
        notes.add(Note.lookup(OctaveUtil.validateNoteString("E#")));
        notes.add(Note.lookup(OctaveUtil.validateNoteString("G#")));
        notes.add(Note.lookup(OctaveUtil.validateNoteString("B#")));
        notes.add(Note.lookup(OctaveUtil.validateNoteString("D#")));
        new ChordFinder(notes, false).execute(env);
        verify(transcriptManager).setResult("E# minor 7th");
    }

    @Test
    public void testValidFourEnharmonics_G7() throws Exception {
        //G seventh (G B D F) with enharmonic (Fx Ax Cx E#)
        ArrayList<Note> notes = new ArrayList<Note>();
        notes.add(Note.lookup(OctaveUtil.validateNoteString("Fx")));
        notes.add(Note.lookup(OctaveUtil.validateNoteString("Ax")));
        notes.add(Note.lookup(OctaveUtil.validateNoteString("Cx")));
        notes.add(Note.lookup(OctaveUtil.validateNoteString("E#")));
        new ChordFinder(notes, false).execute(env);
        verify(transcriptManager).setResult("Fx seventh");
    }







////////////////////////////////////////////////////////////////////////////////////
    /**
     * Tests for finding inversions of chords for 3-note chords
     */
    @Test
    public void testValidTrioInversions_CMajor() {
        //C Major: C E G in order E G C
        ArrayList<Note> notes = new ArrayList<Note>();
        notes.add(Note.lookup(OctaveUtil.validateNoteString("E")));
        notes.add(Note.lookup(OctaveUtil.validateNoteString("G")));
        notes.add(Note.lookup(OctaveUtil.validateNoteString("C")));
        new ChordFinder(notes, true).execute(env);
        verify(transcriptManager).setResult("C major");
    }

    @Test
    public void testValidTrioInversions_FMajor() {
        //F Major: F A C in order A F C
        ArrayList<Note> notes = new ArrayList<Note>();
        notes.add(Note.lookup(OctaveUtil.validateNoteString("A")));
        notes.add(Note.lookup(OctaveUtil.validateNoteString("F")));
        notes.add(Note.lookup(OctaveUtil.validateNoteString("C")));
        new ChordFinder(notes, true).execute(env);
        verify(transcriptManager).setResult("F major");
    }


    @Test
    public void testValidTrioInversionsFail() {
        //C major combination from testValidTrioInversions_CMajor()
        // but without 'all specifier'
        ArrayList<Note> notes = new ArrayList<Note>();
        notes.add(Note.lookup(OctaveUtil.validateNoteString("E")));
        notes.add(Note.lookup(OctaveUtil.validateNoteString("G")));
        notes.add(Note.lookup(OctaveUtil.validateNoteString("C")));
        new ChordFinder(notes, false).execute(env);
        verify(transcriptManager).setResult("No chords found for given notes.");


    }

////////////////////////////////////////////////////////////////////////////////////
    /**
     * Tests for finding inversions of 4-note chords
     */

    @Test
    public void testValidFourAll_CDiminished7() throws Exception {
        //C diminished seventh inversion (C Eb Gb Bbb) in order (Bbb C Eb Gb)
        ArrayList<Note> notes = new ArrayList<Note>();

        notes.add(Note.lookup(OctaveUtil.validateNoteString("C")));
        notes.add(Note.lookup(OctaveUtil.validateNoteString("Eb")));
        notes.add(Note.lookup(OctaveUtil.validateNoteString("Gb")));
        new ChordFinder(notes, true).execute(env);
        verify(transcriptManager).setResult("C diminished");
    }

    @Test
    public void testValidFourAll_CHalfDiminished() throws Exception {
        //C half diminished inversion (C Eb Gb Bb) in order (Eb Gb Bb C)
        ArrayList<Note> notes = new ArrayList<Note>();
        notes.add(Note.lookup(OctaveUtil.validateNoteString("Eb4")));
        notes.add(Note.lookup(OctaveUtil.validateNoteString("Gb4")));
        notes.add(Note.lookup(OctaveUtil.validateNoteString("Bb5")));
        notes.add(Note.lookup(OctaveUtil.validateNoteString("C5")));
        new ChordFinder(notes, true).execute(env);
        verify(transcriptManager).setResult("C half diminished");
    }

    @Test
    public void testValidFourAll_FMajor7() throws Exception {
        //F major seventh inversion(F A C E) in order (A C E F)
        ArrayList<Note> notes = new ArrayList<Note>();
        notes.add(Note.lookup(OctaveUtil.validateNoteString("A")));
        notes.add(Note.lookup(OctaveUtil.validateNoteString("C")));
        notes.add(Note.lookup(OctaveUtil.validateNoteString("E")));
        notes.add(Note.lookup(OctaveUtil.validateNoteString("F")));
        new ChordFinder(notes, true).execute(env);
        verify(transcriptManager).setResult("F major 7th");
    }

    @Test
    public void testValidFourAll_FMinor7() throws Exception {
        //F minor seventh inversion(F Ab C Eb) in order (Eb F Ab C)
        ArrayList<Note> notes = new ArrayList<Note>();
        notes.add(Note.lookup(OctaveUtil.validateNoteString("Eb5")));
        notes.add(Note.lookup(OctaveUtil.validateNoteString("F5")));
        notes.add(Note.lookup(OctaveUtil.validateNoteString("Ab6")));
        notes.add(Note.lookup(OctaveUtil.validateNoteString("C6")));
        new ChordFinder(notes, true).execute(env);
        verify(transcriptManager).setResult("F minor 7th");
    }

    @Test
    public void testValidFourAll_G7() throws Exception {
        //G seventh inversion (G B D F) in order (D F G B)
        ArrayList<Note> notes = new ArrayList<Note>();
        notes.add(Note.lookup(OctaveUtil.validateNoteString("D")));
        notes.add(Note.lookup(OctaveUtil.validateNoteString("F")));
        notes.add(Note.lookup(OctaveUtil.validateNoteString("G")));
        notes.add(Note.lookup(OctaveUtil.validateNoteString("B")));
        new ChordFinder(notes, true).execute(env);
        verify(transcriptManager).setResult("G seventh");
    }




//////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * tests Trios with no valid matching chords
     */
    @Test
    public void testInvalidTrio_randomNotes1() {
        // A B C (No valid chord)
        ArrayList<Note> notes = new ArrayList<Note>();
        notes.add(Note.lookup(OctaveUtil.validateNoteString("A")));
        notes.add(Note.lookup(OctaveUtil.validateNoteString("B")));
        notes.add(Note.lookup(OctaveUtil.validateNoteString("C")));
        new ChordFinder(notes, false).execute(env);
        verify(transcriptManager, times(1)).setResult("No chords found for given notes.");
    }


    @Test
    public void testInvalidTrio_randomNotes2() {
        //with octave specified
        ArrayList<Note> notes = new ArrayList<Note>();
        notes.add(Note.lookup(OctaveUtil.validateNoteString("F4")));
        notes.add(Note.lookup(OctaveUtil.validateNoteString("A5")));
        notes.add(Note.lookup(OctaveUtil.validateNoteString("G5")));
        new ChordFinder(notes, false).execute(env);
        verify(transcriptManager, times(1)).setResult("No chords found for given notes.");
    }

//////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * tests 4-notes with no valid matching chords
     */

    @Test
    public void testInvalidFour_randomNotes1() {
        ArrayList<Note> notes = new ArrayList<Note>();
        notes.add(Note.lookup(OctaveUtil.validateNoteString("F")));
        notes.add(Note.lookup(OctaveUtil.validateNoteString("A")));
        notes.add(Note.lookup(OctaveUtil.validateNoteString("G")));
        notes.add(Note.lookup(OctaveUtil.validateNoteString("E")));
        new ChordFinder(notes, false).execute(env);
        verify(transcriptManager, times(1)).setResult("No chords found for given notes.");
    }

    @Test
    public void testInvalidFour_randomNotes2() {
        //with octave specified
        ArrayList<Note> notes = new ArrayList<Note>();
        notes.add(Note.lookup(OctaveUtil.validateNoteString("F4")));
        notes.add(Note.lookup(OctaveUtil.validateNoteString("A5")));
        notes.add(Note.lookup(OctaveUtil.validateNoteString("G5")));
        notes.add(Note.lookup(OctaveUtil.validateNoteString("E5")));
        new ChordFinder(notes, false).execute(env);
        verify(transcriptManager, times(1)).setResult("No chords found for given notes.");
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * tests Note sets of different sizes which should be invalid.
     */
    @Test
    public void testInvalidSizedSets_zeroNotes() {
        //Test for 0 notes in chord
        ArrayList<Note> notes = new ArrayList<Note>();
        new ChordFinder(notes, false).execute(env);
        verify(transcriptManager, times(1)).setResult("Not chords found. Must provide either 3 or 4 notes.");
    }

    @Test
    public void testInvalidSizedSets_twoNotes() {
        //Test for notes for chord given < 3
        ArrayList<Note> notes = new ArrayList<Note>();
        notes.add(Note.lookup(OctaveUtil.validateNoteString("F")));
        notes.add(Note.lookup(OctaveUtil.validateNoteString("A")));
        new ChordFinder(notes, false).execute(env);
        verify(transcriptManager, times(1)).setResult("Not chords found. Must provide either 3 or 4 notes.");
    }

    @Test
    public void testInvalidSizedSets_5Notes() {
        //Test for notes given for find chord with more than 4 notes
        ArrayList<Note> notes = new ArrayList<Note>();
        notes.add(Note.lookup(OctaveUtil.validateNoteString("F")));
        notes.add(Note.lookup(OctaveUtil.validateNoteString("A")));
        notes.add(Note.lookup(OctaveUtil.validateNoteString("C")));
        notes.add(Note.lookup(OctaveUtil.validateNoteString("B")));
        notes.add(Note.lookup(OctaveUtil.validateNoteString("G")));
        new ChordFinder(notes, false).execute(env);
        verify(transcriptManager, times(1)).setResult("Not chords found. Must provide either 3 or 4 notes.");

    }

    @Test
    public void testFourNoteInversion() {
        //Test for notes given for find chord with more than 4 notes inversion
        ArrayList<Note> notes = new ArrayList<Note>();

        notes.add(Note.lookup(OctaveUtil.validateNoteString("F")));
        notes.add(Note.lookup(OctaveUtil.validateNoteString("E")));
        notes.add(Note.lookup(OctaveUtil.validateNoteString("A")));
        notes.add(Note.lookup(OctaveUtil.validateNoteString("C")));


        new ChordFinder(notes, true).execute(env);
        verify(transcriptManager, times(1)).setResult("F major 7th");

    }

    @Test
    public void testDiminishedSeventhEnharmonics() {
        //
        ArrayList<Note> notes = new ArrayList<Note>();

        notes.add(Note.lookup(OctaveUtil.validateNoteString("C")));
        notes.add(Note.lookup(OctaveUtil.validateNoteString("Eb")));
        notes.add(Note.lookup(OctaveUtil.validateNoteString("Gb")));
        notes.add(Note.lookup(OctaveUtil.validateNoteString("Bbb")));


        new ChordFinder(notes, true).execute(env);
        verify(transcriptManager, times(1)).setResult("C diminished 7th : Eb diminished 7th : Gb diminished 7th : Bbb diminished 7th");

    }


}