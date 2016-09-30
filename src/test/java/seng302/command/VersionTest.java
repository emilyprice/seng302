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

/**
 * Created by isabelle on 8/03/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class VersionTest {

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
    public void testExecute() throws Exception {
        Version version = new Version();
        version.execute(env);
        verify(transcriptManager).setResult("Current Version: " + version.getCurrentVersion());

    }
}