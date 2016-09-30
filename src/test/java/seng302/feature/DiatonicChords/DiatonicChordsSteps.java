package seng302.feature.DiatonicChords;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import seng302.Environment;
import seng302.FirebaseUpdater;
import seng302.Users.UserHandler;
import seng302.gui.RootController;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Mockito.mock;

/**
 * Created by isabelle on 29/07/16.
 */
public class DiatonicChordsSteps {
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

    @When("I type the command 'quality of (.+)'")
    public void executeCommand(final String romanNumeral) {
        env.getExecutor().executeCommand("quality of " + romanNumeral);
        result = env.getTranscriptManager().getTranscriptTuples().get(0).getResult();
    }

    @When("I type the command 'chord function (I|II|III|IV|V|VI|VII) (.+)'")
    public void executeChordFunctionCommand(final String romanNumeral, final String key) {
        env.getExecutor().executeCommand("chord function " + romanNumeral + " " + key);
        result = env.getTranscriptManager().getTranscriptTuples().get(0).getResult();
    }

    @Then("The following is printed to the transcript pane - (.+)")
    public void verifyResult(final String quality) {
        assertThat(result, equalTo(quality));
    }

    @When("^I type the command 'function of (.+7th) (.+)'$")
    public void executeFunctionCommand(final String chord, final String key) {
        env.getExecutor().executeCommand("function of " + chord + " " + key);
        result = env.getTranscriptManager().getTranscriptTuples().get(0).getResult();
    }
}
