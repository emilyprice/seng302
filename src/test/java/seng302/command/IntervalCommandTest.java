package seng302.command;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashMap;

import seng302.Environment;
import seng302.managers.TranscriptManager;

import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class IntervalCommandTest {

    private Environment env;
    @Mock
    private TranscriptManager transcriptManager;
    private HashMap<String, String> interval;

    @Before
    public void setUp() throws Exception {
        env = new Environment();
        env.setTranscriptManager(transcriptManager);
        interval = new HashMap<String, String>();
    }

    @Test
    public void setsCorrectSemitoneResult() {
        interval.put("interval", "perfect octave");
        new IntervalCommand(interval, "semitones").execute(env);
        verify(transcriptManager).setResult("12 semitones");
    }

    /**
     * Comprehensive testing of interval enharmonics including valid and invalid interval
     * enharmonics
     * @throws Exception
     */
    @Test
    public void testCorrectEnharmonic() throws Exception {
        interval.put("interval", "unison");
        new IntervalCommand(interval, "equivalent").execute(env);
        verify(transcriptManager).setResult("Interval has no enharmonics");

        interval.clear();
        interval.put("interval", "major second");
        new IntervalCommand(interval, "equivalent").execute(env);
        verify(transcriptManager, atLeast(1)).setResult("Interval has no enharmonics");

        interval.clear();
        interval.put("interval", "major third");
        new IntervalCommand(interval, "equivalent").execute(env);
        verify(transcriptManager, atLeast(1)).setResult("Interval has no enharmonics");

        interval.clear();
        interval.put("interval", "augmented fourth");
        new IntervalCommand(interval, "equivalent").execute(env);
        verify(transcriptManager).setResult("diminished fifth");

        interval.clear();
        interval.put("interval", "diminished fifth");
        new IntervalCommand(interval, "equivalent").execute(env);
        verify(transcriptManager).setResult("augmented fourth");

        interval.clear();
        interval.put("interval", "minor sixth");
        new IntervalCommand(interval, "equivalent").execute(env);
        verify(transcriptManager, atLeast(1)).setResult("Interval has no enharmonics");

        interval.clear();
        interval.put("interval", "minor seventh");
        new IntervalCommand(interval, "equivalent").execute(env);
        verify(transcriptManager, atLeast(1)).setResult("Interval has no enharmonics");

        interval.clear();
        interval.put("interval", "minor second");
        new IntervalCommand(interval, "equivalent").execute(env);
        verify(transcriptManager, atLeast(1)).setResult("Interval has no enharmonics");

        interval.clear();
        interval.put("interval", "minor third");
        new IntervalCommand(interval, "equivalent").execute(env);
        verify(transcriptManager, atLeast(1)).setResult("Interval has no enharmonics");

        interval.clear();
        interval.put("interval", "perfect fourth");
        new IntervalCommand(interval, "equivalent").execute(env);
        verify(transcriptManager, atLeast(1)).setResult("Interval has no enharmonics");

        interval.clear();
        interval.put("interval", "perfect fifth");
        new IntervalCommand(interval, "equivalent").execute(env);
        verify(transcriptManager, atLeast(1)).setResult("Interval has no enharmonics");

        interval.clear();
        interval.put("interval", "major sixth");
        new IntervalCommand(interval, "equivalent").execute(env);
        verify(transcriptManager).setResult("diminished seventh");

        interval.clear();
        interval.put("interval", "diminished seventh");
        new IntervalCommand(interval, "equivalent").execute(env);
        verify(transcriptManager).setResult("major sixth");

        interval.clear();
        interval.put("interval", "major seventh");
        new IntervalCommand(interval, "equivalent").execute(env);
        verify(transcriptManager, atLeast(1)).setResult("Interval has no enharmonics");

        interval.clear();
        interval.put("interval", "major seventh");
        new IntervalCommand(interval, "equivalent").execute(env);
        verify(transcriptManager, atLeast(1)).setResult("Interval has no enharmonics");

        interval.put("interval", "perfect octave");
        new IntervalCommand(interval, "equivalent").execute(env);
        verify(transcriptManager, atLeast(1)).setResult("Interval has no enharmonics");

        interval.clear();
        interval.put("interval", "minor ninth");
        new IntervalCommand(interval, "equivalent").execute(env);
        verify(transcriptManager, atLeast(1)).setResult("Interval has no enharmonics");

        interval.clear();
        interval.put("interval", "major ninth");
        new IntervalCommand(interval, "equivalent").execute(env);
        verify(transcriptManager, atLeast(1)).setResult("Interval has no enharmonics");

        interval.clear();
        interval.put("interval", "minor tenth");
        new IntervalCommand(interval, "equivalent").execute(env);
        verify(transcriptManager, atLeast(1)).setResult("Interval has no enharmonics");

        interval.clear();
        interval.put("interval", "major tenth");
        new IntervalCommand(interval, "equivalent").execute(env);
        verify(transcriptManager, atLeast(1)).setResult("Interval has no enharmonics");

        interval.clear();
        interval.put("interval", "perfect eleventh");
        new IntervalCommand(interval, "equivalent").execute(env);
        verify(transcriptManager, atLeast(1)).setResult("Interval has no enharmonics");

        interval.clear();
        interval.put("interval", "augmented eleventh");
        new IntervalCommand(interval, "equivalent").execute(env);
        verify(transcriptManager).setResult("diminished twelfth");

        interval.clear();
        interval.put("interval", "diminished twelfth");
        new IntervalCommand(interval, "equivalent").execute(env);
        verify(transcriptManager).setResult("augmented eleventh");

        interval.clear();
        interval.put("interval", "perfect twelfth");
        new IntervalCommand(interval, "equivalent").execute(env);
        verify(transcriptManager, atLeast(1)).setResult("Interval has no enharmonics");

        interval.clear();
        interval.put("interval", "minor thirteenth");
        new IntervalCommand(interval, "equivalent").execute(env);
        verify(transcriptManager, atLeast(1)).setResult("Interval has no enharmonics");

        interval.clear();
        interval.put("interval", "major thirteenth");
        new IntervalCommand(interval, "equivalent").execute(env);
        verify(transcriptManager, atLeast(1)).setResult("diminished fourteenth");

        interval.clear();
        interval.put("interval", "diminished fourteenth");
        new IntervalCommand(interval, "equivalent").execute(env);
        verify(transcriptManager, atLeast(1)).setResult("major thirteenth");

        interval.clear();
        interval.put("interval", "minor fourteenth");
        new IntervalCommand(interval, "equivalent").execute(env);
        verify(transcriptManager, atLeast(1)).setResult("Interval has no enharmonics");

        interval.clear();
        interval.put("interval", "major fourteenth");
        new IntervalCommand(interval, "equivalent").execute(env);
        verify(transcriptManager, atLeast(1)).setResult("Interval has no enharmonics");

        interval.clear();
        interval.put("interval", "double octave");
        new IntervalCommand(interval, "equivalent").execute(env);
        verify(transcriptManager, atLeast(1)).setResult("Interval has no enharmonics");

    }

    @Test
    public void setsCorrectErrorResult() {
        interval.put("interval", "blah");
        new IntervalCommand(interval, "semitones").execute(env);
        verify(transcriptManager).setResult("[ERROR] Unknown interval: blah");
    }

    @Test
    public void setsCorrectNoteResult() {
        interval.put("interval", "perfect fourth");
        interval.put("note", "G");
        new IntervalCommand(interval, "note").execute(env);
        verify(transcriptManager).setResult("C");

        interval.clear();
        interval.put("interval", "major seventh");
        interval.put("note", "G4");
        new IntervalCommand(interval, "note").execute(env);
        verify(transcriptManager).setResult("F#5");
    }

    @Test
    public void setsCorrectNoteErrors() {
        interval.put("interval", "perfect fourth");
        interval.put("note", "M");
        new IntervalCommand(interval, "note").execute(env);
        verify(transcriptManager).setResult("[ERROR] 'M' is not a valid note.");

        interval.clear();
        interval.put("interval", "blah");
        interval.put("note", "C");
        new IntervalCommand(interval, "note").execute(env);
        verify(transcriptManager).setResult("[ERROR] Unknown interval: blah");
    }

    @Test
    public void setsCorrectPlayErrors() {
        interval.put("interval", "perfect fourth");
        interval.put("note", "M");
        new IntervalCommand(interval, "play").execute(env);
        verify(transcriptManager).setResult("[ERROR] 'M' is not a valid note.");

        interval.clear();
        interval.put("interval", "blah");
        interval.put("note", "C");
        new IntervalCommand(interval, "play").execute(env);
        verify(transcriptManager).setResult("[ERROR] Unknown interval: blah");


        interval.clear();
        interval.put("semitones", "-1");
        interval.put("note", "C");
        new IntervalCommand(interval, "play").execute(env);
        verify(transcriptManager).setResult("[ERROR] Unknown interval: -1");
    }
}
