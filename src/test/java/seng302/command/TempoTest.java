package seng302.command;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import seng302.Environment;
import seng302.FirebaseUpdater;
import seng302.Users.UserHandler;
import seng302.gui.RootController;
import seng302.gui.UserPageController;
import seng302.managers.TranscriptManager;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Created by emily on 1/05/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class TempoTest {

    private Environment env;
    @Mock
    private TranscriptManager transcriptManager;

    @Mock
    private UserPageController userPageController;

    @Before
    public void setUp() throws Exception {
        FirebaseUpdater mockFireBase = mock(FirebaseUpdater.class);
        UserHandler mockUserH = mock(UserHandler.class);
        env = new Environment(mockFireBase, mockUserH);
        env.setTranscriptManager(transcriptManager);
        RootController root = new RootController();
        env.setRootController(root);
        env.setUserPageController(userPageController);
    }


    @Test
    public void testGetsDefaultTempo() {
        new Tempo().execute(env);
        verify(transcriptManager).setResult("120 BPM");
    }

    @Test
    public void testGetsAlteredTempo() {
        new Tempo("200", false).execute(env);
        new Tempo().execute(env);
        verify(transcriptManager).setResult("200 BPM");
    }

    @Test
    public void testSetsGoodTempoNoForce() {
        new Tempo("200", false).execute(env);
        verify(transcriptManager).setResult("Tempo changed to 200 BPM");
    }

    @Test
    public void testSetsGoodTempoWithForce() {
        new Tempo("200", true).execute(env);
        verify(transcriptManager).setResult("Tempo changed to 200 BPM");
    }

    @Test
    public void testSetsBadTempoNoForce() {
        new Tempo("301", false).execute(env);
        verify(transcriptManager).setResult("Tempo outside valid range. Use 'force set tempo'" +
                " command to override. Use 'help' for more information");
    }

    @Test
    public void testSetsBadTempoWithForce() {
        new Tempo("301", true).execute(env);
        verify(transcriptManager).setResult("Tempo changed to 301 BPM");
    }

    @Test
    public void testForceSets() {
        new Tempo("301", true).execute(env);
        assert env.getPlayer().getTempo() == 301;
    }

    @Test
    public void testNoForceSets() {
        new Tempo("200", false).execute(env);
        assert env.getPlayer().getTempo() == 200;
    }

    @Test
    public void testNoForceDoesntSet() {
        new Tempo("301", false).execute(env);
        assert env.getPlayer().getTempo() == 120;
    }

    @Test
    public void testForceZero() {
        new Tempo("0", true).execute(env);
        assert env.getPlayer().getTempo() == 120;
        verify(transcriptManager).setResult("Invalid tempo");
    }

}
