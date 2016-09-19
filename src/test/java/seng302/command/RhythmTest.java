package seng302.command;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import seng302.Environment;
import seng302.managers.TranscriptManager;
import seng302.utility.musicNotation.RhythmHandler;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;

/**
 * Created by jonty on 5/16/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class RhythmTest {

    private Environment env;
    @Mock
    private TranscriptManager transcriptManager;

    @Mock
    private RhythmHandler rh;


    @Before
    public void setUp() throws Exception {
        env = new Environment();
        env.setTranscriptManager(transcriptManager);

        rh = env.getPlayer().getRhythmHandler();
        Mockito.spy(rh);
    }

    @Test
    public void testGetsDefaultRhythm() {
        new Rhythm().execute(env);
        verify(transcriptManager).setResult("Rhythm beat divisions: 1/2");
    }

    @Test
    public void testChangeRhythms() {
        new Rhythm("medium", false).execute(env);
        verify(transcriptManager).setResult("Rhythm set to medium swing timing (2/3 1/3).");

        new Rhythm("heavy", false).execute(env);
        verify(transcriptManager).setResult("Rhythm set to heavy swing timing (3/4 1/4).");

        new Rhythm("light", false).execute(env);
        verify(transcriptManager).setResult("Rhythm set to light swing timing (5/8 3/8).");

        new Rhythm("straight", false).execute(env);

        verify(transcriptManager).setResult("Rhythm set to straight, half crotchet timing (1/2).");

    }

    @Test
    public void testGetAlteredRhythm() {
        new Rhythm("medium", false).execute(env);
        new Rhythm().execute(env);
        verify(transcriptManager).setResult("Rhythm beat divisions: 2/3 1/3");
    }

    @Test
    public void testBadRhythm(){
        new Rhythm("bad", false).execute(env);


        verify(transcriptManager).setResult("Invalid Rhythm option 'bad'. See 'help set rhythm' for valid rhythm options");

        //should still be set to 'straight'
        new Rhythm().execute(env);
        verify(transcriptManager).setResult("Rhythm beat divisions: 1/2");
    }

    @Test
    public void testCustomRhythmValues() {
        rh.setBeatResolution(24);
        new Rhythm("2/3 1/3", false).execute(env);

        assertEquals(16, rh.getNextTickTiming());
        assertEquals(8, rh.getNextTickTiming());
        assertArrayEquals(new int[]{16, 8}, rh.getRhythmTimings());


        new Rhythm("1/4 1/2 1/4", false).execute(env);

        assertEquals(6, rh.getNextTickTiming());
        assertArrayEquals(new int[]{6, 12, 6}, rh.getRhythmTimings());

        new Rhythm("heavy", false).execute(env);

        assertEquals(18, rh.getNextTickTiming());

        assertArrayEquals(new int[]{18, 6}, rh.getRhythmTimings());
    }


}