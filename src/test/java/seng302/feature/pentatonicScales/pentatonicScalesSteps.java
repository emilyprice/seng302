package seng302.feature.pentatonicScales;

import java.util.HashMap;


import cucumber.api.CucumberOptions;
import cucumber.api.PendingException;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import seng302.Environment;
import seng302.FirebaseUpdater;
import seng302.Users.UserHandler;
import seng302.command.Scale;
import seng302.gui.RootController;
import seng302.managers.TranscriptManager;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;


/**
 * Created by dominicjarvis on 5/08/16.
 */
@CucumberOptions()
public class pentatonicScalesSteps {

    Environment env;
    String result;

    @Given("I am on the transcript pane")
    public void createEnvironment() {
        FirebaseUpdater mockFireBase = mock(FirebaseUpdater.class);
        UserHandler mockUserH = mock(UserHandler.class);
        env = new Environment(mockFireBase, mockUserH);
        RootController rootController = new RootController();
        env.setRootController(rootController);
    }

    @When("^I type the command '([^\"]*) scale ([^\"]*) ([^\"]*) pentatonic'$")
    public void iTypeTheCommandScale(String arg0, String arg1, String arg2) throws Throwable {
        if (arg0.equals("note")) {
            env.getExecutor().executeCommand("scale " + arg1 + " " + arg2 + " pentatonic");
        } else {
            env.getExecutor().executeCommand("midi scale " + arg1 + " " + arg2 + " pentatonic");
        }
        result = env.getTranscriptManager().getTranscriptTuples().get(0).getResult();
    }

    @Then("^The following is printed to the transcript pane - ([^\"]*)$")
    public void theFollowingIsPrintedToTheTranscriptPane(String arg0) throws Throwable {
        assertEquals(result, arg0);
    }

}
