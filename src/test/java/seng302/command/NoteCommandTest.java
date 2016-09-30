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
public class NoteCommandTest {

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
    public void setsCorrectNoteResult() {
        new NoteCommand("60").execute(env);
        verify(transcriptManager).setResult("C4");
    }

    @Test
    public void setsCorrectErrorResult() {
        new NoteCommand("gf").execute(env);
        verify(transcriptManager).setResult("[ERROR] 'gf' is not a valid MIDI value.");
    }
}