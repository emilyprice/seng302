package seng302.command;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


import java.util.ArrayList;

import seng302.Environment;
import seng302.FirebaseUpdater;
import seng302.Users.UserHandler;
import seng302.gui.UserPageController;
import seng302.managers.TranscriptManager;
import seng302.utility.MusicalTermsTutorBackEnd;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class UndoRedoTest {

    private Environment env;

    @Mock
    private TranscriptManager transcriptManager;

    @Mock
    private UserPageController userPageController;

    private MusicalTermsTutorBackEnd tutorDataManger = new MusicalTermsTutorBackEnd();

    @Before
    public void setUp() throws Exception {
        FirebaseUpdater mockFireBase = mock(FirebaseUpdater.class);
        UserHandler mockUserH = mock(UserHandler.class);
        env = new Environment(mockFireBase, mockUserH);
        env.setTranscriptManager(transcriptManager);
        env.setMttDataManager(tutorDataManger);
        env.setUserPageController(userPageController);
    }

    @Test
    public void printsCorrectErrorMessageInitial() {
        new UndoRedo(0).execute(env);
        verify(transcriptManager).setResult("[ERROR] No command to undo.");
        new UndoRedo(1).execute(env);
        verify(transcriptManager).setResult("[ERROR] No command to redo.");
    }

    @Test
    public void printsCorrectUndoSingle() {

        // Tempo
        new Tempo("40", true).execute(env);
        verify(transcriptManager).setResult("Tempo changed to 40 BPM");
        new UndoRedo(0).execute(env);
        verify(transcriptManager).setResult("Tempo changed to 120 BPM");

        // Musical terms
        ArrayList<String> musicalTermArray = new ArrayList<String>();
        musicalTermArray.add("name");
        musicalTermArray.add("category");
        musicalTermArray.add("origin");
        musicalTermArray.add("description");
        MusicalTerm termCommand = new MusicalTerm(musicalTermArray);
        termCommand.execute(env);
        verify(transcriptManager).setResult("Added term: name\n" +
                "Origin: category \nCategory: origin\nDefinition: description");

        UndoRedo ur = new UndoRedo(0);
        ur.execute(env);
        verify(transcriptManager).setResult("Musical Term name has been deleted.");
    }

    @Test
    public void printsCorrectRedoSingle() {
        // Tempo
        new Tempo("40", true).execute(env);
        verify(transcriptManager).setResult("Tempo changed to 40 BPM");
        new UndoRedo(0).execute(env);
        verify(transcriptManager).setResult("Tempo changed to 120 BPM");
        new UndoRedo(1).execute(env);
        verify(transcriptManager, times(2)).setResult("Tempo changed to 40 BPM");

        // Musical terms
        ArrayList<String> musicalTermArray = new ArrayList<String>();
        musicalTermArray.add("name");
        musicalTermArray.add("category");
        musicalTermArray.add("origin");
        musicalTermArray.add("description");
        MusicalTerm termCommand = new MusicalTerm(musicalTermArray);
        termCommand.execute(env);
        verify(transcriptManager).setResult("Added term: name\n" +
                "Origin: category \nCategory: origin\nDefinition: description");

        UndoRedo ur = new UndoRedo(0);
        ur.execute(env);
        verify(transcriptManager).setResult("Musical Term name has been deleted.");
        new UndoRedo(1).execute(env);
        verify(transcriptManager, times(2)).setResult("Added term: name\n" +
                "Origin: category \nCategory: origin\nDefinition: description");
    }

    @Test
    public void correctlyScrollsThroughUndos() {
        new Tempo("30", true).execute(env);
        verify(transcriptManager).setResult("Tempo changed to 30 BPM");
        new Tempo("40", true).execute(env);
        verify(transcriptManager).setResult("Tempo changed to 40 BPM");
        new Tempo("50", true).execute(env);
        verify(transcriptManager).setResult("Tempo changed to 50 BPM");
        new Tempo("60", true).execute(env);
        verify(transcriptManager).setResult("Tempo changed to 60 BPM");
        new Tempo("70", true).execute(env);
        verify(transcriptManager).setResult("Tempo changed to 70 BPM");
        new Tempo("80", true).execute(env);
        verify(transcriptManager).setResult("Tempo changed to 80 BPM");
        new UndoRedo(0).execute(env);
        verify(transcriptManager, times(2)).setResult("Tempo changed to 70 BPM");
        new UndoRedo(0).execute(env);
        verify(transcriptManager, times(2)).setResult("Tempo changed to 60 BPM");
        new UndoRedo(0).execute(env);
        verify(transcriptManager, times(2)).setResult("Tempo changed to 50 BPM");
        new UndoRedo(0).execute(env);
        verify(transcriptManager, times(2)).setResult("Tempo changed to 40 BPM");
        new UndoRedo(0).execute(env);
        verify(transcriptManager, times(2)).setResult("Tempo changed to 30 BPM");
        new UndoRedo(0).execute(env);
        verify(transcriptManager).setResult("Tempo changed to 120 BPM");
        new UndoRedo(0).execute(env);
        verify(transcriptManager).setResult("[ERROR] No command to undo.");
    }

    @Test
    public void correctlyScrollsThroughRedos() {
        new Tempo("30", true).execute(env);
        verify(transcriptManager).setResult("Tempo changed to 30 BPM");
        new Tempo("40", true).execute(env);
        verify(transcriptManager).setResult("Tempo changed to 40 BPM");
        new Tempo("50", true).execute(env);
        verify(transcriptManager).setResult("Tempo changed to 50 BPM");
        new Tempo("60", true).execute(env);
        verify(transcriptManager).setResult("Tempo changed to 60 BPM");
        new Tempo("70", true).execute(env);
        verify(transcriptManager).setResult("Tempo changed to 70 BPM");
        new Tempo("80", true).execute(env);
        verify(transcriptManager).setResult("Tempo changed to 80 BPM");
        new UndoRedo(0).execute(env);
        verify(transcriptManager, times(2)).setResult("Tempo changed to 70 BPM");
        new UndoRedo(0).execute(env);
        verify(transcriptManager, times(2)).setResult("Tempo changed to 60 BPM");
        new UndoRedo(0).execute(env);
        verify(transcriptManager, times(2)).setResult("Tempo changed to 50 BPM");
        new UndoRedo(0).execute(env);
        verify(transcriptManager, times(2)).setResult("Tempo changed to 40 BPM");
        new UndoRedo(0).execute(env);
        verify(transcriptManager, times(2)).setResult("Tempo changed to 30 BPM");
        new UndoRedo(0).execute(env);
        verify(transcriptManager).setResult("Tempo changed to 120 BPM");
        new UndoRedo(0).execute(env);
        verify(transcriptManager).setResult("[ERROR] No command to undo.");
        new UndoRedo(1).execute(env);
        verify(transcriptManager, times(3)).setResult("Tempo changed to 30 BPM");
        new UndoRedo(1).execute(env);
        verify(transcriptManager, times(3)).setResult("Tempo changed to 40 BPM");
        new UndoRedo(1).execute(env);
        verify(transcriptManager, times(3)).setResult("Tempo changed to 50 BPM");
        new UndoRedo(1).execute(env);
        verify(transcriptManager, times(3)).setResult("Tempo changed to 60 BPM");
        new UndoRedo(1).execute(env);
        verify(transcriptManager, times(3)).setResult("Tempo changed to 70 BPM");
        new UndoRedo(1).execute(env);
        verify(transcriptManager, times(2)).setResult("Tempo changed to 80 BPM");
        new UndoRedo(1).execute(env);
        verify(transcriptManager).setResult("[ERROR] No command to redo.");
    }

    @Test
    public void scrollsCorrectlyAfterUndoThenCommand() {
        // SET UP
        new Tempo("40", true).execute(env);
        verify(transcriptManager).setResult("Tempo changed to 40 BPM");
        new Tempo("30", true).execute(env);
        verify(transcriptManager).setResult("Tempo changed to 30 BPM");
        // Actual
        new UndoRedo(0).execute(env);
        verify(transcriptManager, times(2)).setResult("Tempo changed to 40 BPM");
        ArrayList<String> musicalTermArray = new ArrayList<String>();
        musicalTermArray.add("name");
        musicalTermArray.add("category");
        musicalTermArray.add("origin");
        musicalTermArray.add("description");
        MusicalTerm termCommand = new MusicalTerm(musicalTermArray);
        termCommand.execute(env);
        verify(transcriptManager).setResult("Added term: name\n" +
                "Origin: category \nCategory: origin\nDefinition: description");
        new UndoRedo(0).execute(env);
        verify(transcriptManager).setResult("Musical Term name has been deleted.");
        new UndoRedo(0).execute(env);
        verify(transcriptManager).setResult("Tempo changed to 120 BPM");
        new UndoRedo(0).execute(env);
        verify(transcriptManager).setResult("[ERROR] No command to undo.");
        new UndoRedo(1).execute(env);
        verify(transcriptManager, times(3)).setResult("Tempo changed to 40 BPM");
        new UndoRedo(1).execute(env);
        verify(transcriptManager, times(2)).setResult("Added term: name\n" +
                "Origin: category \nCategory: origin\nDefinition: description");
        new UndoRedo(1).execute(env);
        verify(transcriptManager).setResult("[ERROR] No command to redo.");
    }
}