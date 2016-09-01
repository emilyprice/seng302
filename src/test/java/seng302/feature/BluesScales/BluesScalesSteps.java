package seng302.feature.BluesScales;

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
 * Created by Elliot on 29/07/2016.
 */
public class BluesScalesSteps {
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

    @When("I type the command 'play scale (.+) blues'")
    public void play_blues_scale(final String startingNote) {
        env.getExecutor().executeCommand("play scale " + startingNote + " blues");
        result = env.getTranscriptManager().getTranscriptTuples().get(0).getResult();
    }

    @When("I type the command 'play scale (.+) blues down'")
    public void play_blues_scale_down(final String startingNote) {
        env.getExecutor().executeCommand("play scale " + startingNote + " blues down");
        result = env.getTranscriptManager().getTranscriptTuples().get(0).getResult();
    }

    @When("I type the command 'play scale (.+) blues updown'")
    public void play_blues_scale_updown(final String startingNote) {
        env.getExecutor().executeCommand("play scale " + startingNote + " blues updown");
        result = env.getTranscriptManager().getTranscriptTuples().get(0).getResult();
    }

    @When("I type the command 'play scale (.+) blues 2'")
    public void play_blues_scale_2_octaves(final String startingNote) {
        env.getExecutor().executeCommand("play scale " + startingNote + " blues 2");
        result = env.getTranscriptManager().getTranscriptTuples().get(0).getResult();
    }

    @When("I type the command 'play scale (.+) blues 3'")
    public void play_blues_scale_3_octaves(final String startingNote) {
        env.getExecutor().executeCommand("play scale " + startingNote + " blues 3");
        result = env.getTranscriptManager().getTranscriptTuples().get(0).getResult();
    }

    @Then("The following is printed to the transcript pane - (.+)")
    public void verifyResult(final String scale) {
        assertThat(result, equalTo(scale));
    }

}
