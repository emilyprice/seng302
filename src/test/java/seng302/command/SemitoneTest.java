package seng302.command;

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
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class SemitoneTest {

    private Environment env;
    @Mock private TranscriptManager transcriptManager;

    @Before
    public void setUp() throws Exception {
        FirebaseUpdater mockFireBase = mock(FirebaseUpdater.class);
        UserHandler mockUserH = mock(UserHandler.class);
        env = new Environment(mockFireBase, mockUserH);
        env.setTranscriptManager(transcriptManager);
    }

    @Test
    public void printsCorrectSemitoneUp() {
        new Semitone("C#-1", true).execute(env);
        verify(transcriptManager).setResult("D-1");
    }

    @Test
    public void printsCorrectSemitoneDown() {
        new Semitone("C#-1", false).execute(env);
        verify(transcriptManager).setResult("C-1");
    }

    @Test
    public void printsCorrectErrorWithHighestNote() {
        new Semitone("G9", true).execute(env);
        verify(transcriptManager).setResult("[ERROR] Note is not contained in the MIDI library.");
    }

    @Test public void printsCorrectErrorWithLowestNote() {
        new Semitone("C-1", false).execute(env);
        verify(transcriptManager).setResult("[ERROR] Note is not contained in the MIDI library.");
    }

    @Test
    public void printsCorrectError() {
        new Semitone("Cake", true).execute(env);
        verify(transcriptManager).setResult("[ERROR] Note is not contained in the MIDI library.");
    }

    @Test
    public void caseInsensitivity() {
        new Semitone("c#-1", true).execute(env);
        verify(transcriptManager).setResult("D-1");
    }

    @Test
    public void testPrintsCorrectSemitoneUpEnharmonic() {
        new Semitone("Cx", true).execute(env);
        verify(transcriptManager).setResult("D#");
    }

    @Test
    public void testPrintsCorrectSemitoneDownEnharmonic() {
        new Semitone("Cx", false).execute(env);
        verify(transcriptManager).setResult("Db");
    }

    @Test
    public void printsCorrectErrorWithHighestNoteEnharmonic() {
        new Semitone("Fx9", true).execute(env);
        verify(transcriptManager).setResult("[ERROR] Note is not contained in the MIDI library.");
    }

    @Test
    public void printsCorrectErrorWithLowestNoteEnharmonic() {
        new Semitone("B#-1", false).execute(env);
        verify(transcriptManager).setResult("[ERROR] Note is not contained in the MIDI library.");
    }
}