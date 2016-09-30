package seng302.command;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import seng302.Environment;
import seng302.FirebaseUpdater;
import seng302.Users.UserHandler;
import seng302.managers.TranscriptManager;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Created by team 5 on 18/03/2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class EnharmonicTest extends TestCase {

    private Environment env;
    @Mock
    private TranscriptManager transcriptManager;

    @Before
    public void setUp() throws Exception {
        FirebaseUpdater mockFireBase = mock(FirebaseUpdater.class);
        UserHandler mockUserH = mock(UserHandler.class);
        env = new Environment(mockFireBase, mockUserH);
        env.setTranscriptManager(transcriptManager);
    }

    @Test
    public void testEnharmonicLower() throws Exception {
        new Enharmonic("C4", 1).execute(env);
        verify(transcriptManager).setResult("B#4");

        new Enharmonic("C", 1).execute(env);
        verify(transcriptManager).setResult("B#");

        new Enharmonic("F4", 1).execute(env);
        verify(transcriptManager).setResult("E#4");

        new Enharmonic("Bx", 1).execute(env);
        verify(transcriptManager).setResult("[ERROR] Note does not have a lower enharmonic.");

        new Enharmonic("Ab", 1).execute(env);
        verify(transcriptManager).setResult("G#");

        new Enharmonic("Ebb", 1).execute(env);
        verify(transcriptManager).setResult("D");

        new Enharmonic("Fx", 1).execute(env);
        verify(transcriptManager, times(2)).setResult("[ERROR] Note does not have a lower enharmonic.");


    }

    @Test
    public void testEnharmonicHigher() throws Exception {
        new Enharmonic("E4", 0).execute(env);
        verify(transcriptManager).setResult("Fb4");

        new Enharmonic("F4", 0).execute(env);
        verify(transcriptManager).setResult("Gbb4");

        new Enharmonic("Dx", 0).execute(env);
        verify(transcriptManager).setResult("E");

        new Enharmonic("C#", 0).execute(env);
        verify(transcriptManager).setResult("Db");

        new Enharmonic("Fbb", 0).execute(env);
        verify(transcriptManager).setResult("[ERROR] Note does not have a higher enharmonic.");

        new Enharmonic("G#", 0).execute(env);
        verify(transcriptManager).setResult("Ab");

        new Enharmonic("Ab", 0).execute(env);
        verify(transcriptManager, times(2)).setResult("[ERROR] Note does not have a higher enharmonic.");


    }

    @Test
    public void testSimpleEnharmonic() throws Exception {
        new Enharmonic("C4", 2).execute(env);
        verify(transcriptManager).setResult("B#4");

        new Enharmonic("D4", 2).execute(env);
        verify(transcriptManager).setResult("[ERROR] Note does not have a simple enharmonic.");

        new Enharmonic("E4", 2).execute(env);
        verify(transcriptManager).setResult("Fb4");

        new Enharmonic("F4", 2).execute(env);
        verify(transcriptManager).setResult("E#4");

        new Enharmonic("G4", 2).execute(env);
        verify(transcriptManager, times(2)).setResult("[ERROR] Note does not have a simple enharmonic.");

        new Enharmonic("A4", 2).execute(env);
        verify(transcriptManager, times(3)).setResult("[ERROR] Note does not have a simple enharmonic.");

        new Enharmonic("B4", 2).execute(env);
        verify(transcriptManager).setResult("Cb4");

        new Enharmonic("Abb", 2).execute(env);
        verify(transcriptManager).setResult("G");

        new Enharmonic("Dx", 2).execute(env);
        verify(transcriptManager).setResult("E");

        new Enharmonic("Ab", 2).execute(env);
        verify(transcriptManager).setResult("G#");
    }

    @Test
    public void testAllEnharmonics() throws Exception {
        //Without Octave
        new Enharmonic("C", 3).execute(env);
        verify(transcriptManager).setResult("B# Dbb");

        //With Octave
        new Enharmonic("C4", 3).execute(env);
        verify(transcriptManager).setResult("B#4 Dbb4");

        // Alphabetic from above
        new Enharmonic("Fbb", 3).execute(env);
        verify(transcriptManager).setResult("D# Eb");

        // Alphabetic from below
        new Enharmonic("Ex", 3).execute(env);
        verify(transcriptManager).setResult("F# Gb");

        // Alphabetic from below
        new Enharmonic("Ax", 3).execute(env);
        verify(transcriptManager).setResult("B Cb");

        //Test all 'base' notes
        new Enharmonic("C#", 3).execute(env);
        verify(transcriptManager).setResult("Bx Db");

        new Enharmonic("D", 3).execute(env);
        verify(transcriptManager).setResult("Cx Ebb");

        new Enharmonic("D#", 3).execute(env);
        verify(transcriptManager).setResult("Eb Fbb");

        new Enharmonic("E", 3).execute(env);
        verify(transcriptManager).setResult("Dx Fb");

        new Enharmonic("F", 3).execute(env);
        verify(transcriptManager).setResult("E# Gbb");

        new Enharmonic("F#", 3).execute(env);
        verify(transcriptManager).setResult("Ex Gb");

        new Enharmonic("G", 3).execute(env);
        verify(transcriptManager).setResult("Abb Fx");

        //Outlier - One enharmonic equivalent
        new Enharmonic("G#", 3).execute(env);
        verify(transcriptManager).setResult("Ab");

        new Enharmonic("A", 3).execute(env);
        verify(transcriptManager).setResult("Bbb Gx");

        new Enharmonic("A#", 3).execute(env);
        verify(transcriptManager).setResult("Bb Cbb");

        new Enharmonic("B", 3).execute(env);
        verify(transcriptManager).setResult("Ax Cb");
    }
}