package seng302.command;

import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.*;


import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import seng302.Environment;
import seng302.FirebaseUpdater;
import seng302.Users.UserHandler;
import seng302.managers.TranscriptManager;

@RunWith(MockitoJUnitRunner.class)
public class PlayNoteTest {
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
    public void catchInvalidNotes() {
        new PlayNote("M").execute(env);
        verify(transcriptManager).setResult("[ERROR] 'M' is not a valid note.");

        new PlayNote("400").execute(env);
        verify(transcriptManager).setResult("[ERROR] '400' is not a valid note.");
    }

    public void catchInvalidDurations() {
        new PlayNote("C", "-1").execute(env);
        verify(transcriptManager).setResult("[ERROR] Invalid duration -500");

        new PlayNote("C", "0").execute(env);
        verify(transcriptManager).setResult("[ERROR] Invalid duration 0");

        new PlayNote("C", "-500").execute(env);
        verify(transcriptManager).setResult("[ERROR] Invalid duration -500");

        new PlayNote("C", "test").execute(env);
        verify(transcriptManager).setResult("[ERROR] Invalid duration test");
    }

}
