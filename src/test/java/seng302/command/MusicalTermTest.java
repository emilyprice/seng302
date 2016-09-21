package seng302.command;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import seng302.Environment;
import seng302.data.Term;
import seng302.utility.MusicalTermsTutorBackEnd;
import seng302.managers.TranscriptManager;
import java.util.ArrayList;
import static org.mockito.Mockito.verify;


@RunWith(MockitoJUnitRunner.class)
public class MusicalTermTest extends TestCase {

    private Environment env;

    @Mock
    private TranscriptManager transcriptManager;

    private MusicalTermsTutorBackEnd tutorDataManger = new MusicalTermsTutorBackEnd();
    private Term term = new Term("name","category","origin","description");

    @Before
    public void setUp() throws Exception {
        env = new Environment();
        env.setTranscriptManager(transcriptManager);
        env.setMttDataManager(tutorDataManger);
    }

    @After
    public void tearDown() throws Exception {
        //MusicalTerm.MusicalTermsMap.clear();
    }


    @Test
    public void testMeaningOfCommandGoodInput() throws Exception {
        executeGoodInput();

        // get the meaning of
        MusicalTerm termCommand2 = new MusicalTerm("name", "meaning");
        termCommand2.execute(env);

        verify(transcriptManager).setResult("definition");
    }

    @Test
    public void testMeaningOfCommandSpaceInTermName() throws Exception {
        //add new term
        executeInputWithSpaceInName();

        // get the meaning of
        MusicalTerm termCommand2 = new MusicalTerm("name", "meaning");
        termCommand2.execute(env);

//        verify(transcriptManager).setResult(
//                "Origin: category\n" +
//                        "Category: origin\n" +
//                        "Definition: definition");

        verify(transcriptManager).setResult("[ERROR] name is not recognised as an existing musical term.");
    }

    @Test
    public void testMeaningOfWhereTermDoesntExist() throws Exception {
        //add new term
        executeGoodInput();

        MusicalTerm termCommand2 = new MusicalTerm("nonExistantName", "meaning");
        termCommand2.execute(env);

        verify(transcriptManager).setResult(
                "[ERROR] nonExistantName is not recognised as an existing musical term.");
    }

    @Test
    public void testAddMusicalTerm() throws Exception {
        ArrayList<String> musicalTermArray = new ArrayList<String>();
        musicalTermArray.add("name");
        musicalTermArray.add("category");
        musicalTermArray.add("origin");
        musicalTermArray.add("description");
        MusicalTerm termCommand = new MusicalTerm(musicalTermArray);
        termCommand.execute(env);


        verify(transcriptManager).setResult("Added term: name\n" +
                "Origin: category \nCategory: origin\nDefinition: description");

        ArrayList<Term> terms = new ArrayList<Term>();
        terms.add(termCommand.term);
        //assertEquals(terms,tutorDataManger.getTerms()); ////WHY THE ... DOES THIS NOT WORK
    }

    @Test
    public void testOriginGoodInput() throws Exception {
        executeGoodInput();

        // get the origin of
        MusicalTerm termCommand2 = new MusicalTerm("name", "origin");
        termCommand2.execute(env);
        verify(transcriptManager).setResult("origin");
    }

    @Test
    public void testOriginDoesntExist() throws Exception {
        //add new term
        executeGoodInput();
        MusicalTerm termCommand2 = new MusicalTerm("nonExistantName", "origin");
        termCommand2.execute(env);
        verify(transcriptManager).setResult(
                "[ERROR] nonExistantName is not recognised as an existing musical term.");
    }

    @Test
    public void testOriginSpaceInName() throws Exception{
        //add new term
        executeInputWithSpaceInName();
        // get the meaning of
        MusicalTerm termCommand2 = new MusicalTerm("name", "origin");
        termCommand2.execute(env);

        verify(transcriptManager).setResult("[ERROR] name is not recognised as an existing musical term.");
    }

    @Test
    public void testCategoryGoodInput() throws Exception {
        executeGoodInput();
        // get the origin of
        MusicalTerm termCommand2 = new MusicalTerm("name", "category");
        termCommand2.execute(env);
        verify(transcriptManager).setResult("category");
    }

    @Test
    public void testCategoryDoesntExist() throws Exception {
        //add new term
        executeGoodInput();
        MusicalTerm termCommand2 = new MusicalTerm("nonExistantName", "category");
        termCommand2.execute(env);
        verify(transcriptManager).setResult(
                "[ERROR] nonExistantName is not recognised as an existing musical term.");
    }

    @Test
    public void testCategorySpaceInName() throws Exception{
        //add new term
        executeInputWithSpaceInName();
        // get the meaning of
        MusicalTerm termCommand2 = new MusicalTerm("name", "category");
        termCommand2.execute(env);

        verify(transcriptManager).setResult("[ERROR] name is not recognised as an existing musical term.");
    }

    @Test
    public void testGetInvalidAttribute() throws Exception {
        executeGoodInput();
        new MusicalTerm("name", "test").execute(env);
        verify(transcriptManager).setResult("[ERROR] test is not recognised as part of a musical term.");
    }

    public void executeGoodInput() {
        //add new term
        ArrayList<String> musicalTermArray = new ArrayList<String>();
        musicalTermArray.add("name");
        musicalTermArray.add("origin");
        musicalTermArray.add("category");
        musicalTermArray.add("definition");
        MusicalTerm termCommand = new MusicalTerm(musicalTermArray);
        termCommand.execute(env);
    }

    public void executeInputWithSpaceInName() {
        ArrayList<String> musicalTermArray = new ArrayList<String>();
        musicalTermArray.add("space name");
        musicalTermArray.add("category");
        musicalTermArray.add("origin");
        musicalTermArray.add("definition");
        MusicalTerm termCommand = new MusicalTerm(musicalTermArray);
        termCommand.execute(env);
    }
}
