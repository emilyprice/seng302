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
public class MidiTest {

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
    public void printsCorrectMidi() {
        new Midi("C#-1").execute(env);
        verify(transcriptManager).setResult("1");
    }

    @Test
    public void printsCorrectMidiUnspecifiedOctave() {
        new Midi("C#").execute(env);
        verify(transcriptManager).setResult("61");
    }

    @Test
    public void printsLowestNote() {
        new Midi("C-1").execute(env);
        verify(transcriptManager).setResult("0");
    }

    @Test
    public void printsHighestNote() {
        new Midi("G9").execute(env);
        verify(transcriptManager).setResult("127");
    }

    @Test
    public void caseSensitivity() {
        new Midi("c#").execute(env);
        verify(transcriptManager).setResult("61");
    }
}