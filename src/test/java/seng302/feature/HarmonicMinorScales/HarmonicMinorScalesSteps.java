package seng302.feature.HarmonicMinorScales;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import seng302.Environment;
import seng302.MusicPlayer;
import seng302.gui.RootController;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Mockito.mock;

/**
 * Cucumber tests for the Harmonic Minor Scales command.
 */
public class HarmonicMinorScalesSteps {
    Environment env;
    String result;
    MusicPlayer player;

    @Given("I am on the transcript pane")
    public void createEnvironment() {
        env = new Environment();
        RootController rootController = new RootController();
        env.setRootController(rootController);
        player = mock(MusicPlayer.class);
        env.setPlayer(player);

    }

    @When("I type the command 'scale (.+) harmonic minor'")
    public void get_harmonic_minor_scale(final String startingNote) {
        env.getExecutor().executeCommand("scale " + startingNote + " harmonic minor");
        result = env.getTranscriptManager().getTranscriptTuples().get(0).getResult();
    }

    @When("I type the command 'play scale (.+) harmonic minor'")
    public void iTypeTheCommandPlayScaleStartingNoteHarmonicMinor(final String startingNote) {
        env.getExecutor().executeCommand("play scale " + startingNote + " harmonic minor");
        result = env.getTranscriptManager().getTranscriptTuples().get(0).getResult();
    }

    @When("I type the command 'play scale (.+) harmonic minor down'")
    public void iTypeTheCommandPlayScaleStartingNoteHarmonicMinorDown(final String startingNote) {
        env.getExecutor().executeCommand("play scale " + startingNote + " harmonic minor down");
        result = env.getTranscriptManager().getTranscriptTuples().get(0).getResult();
    }

    @Then("The following is printed to the transcript pane - (.+)")
    public void verifyResult(final String scale) {
        assertThat(result, equalTo(scale));
    }
}
