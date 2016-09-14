package seng302.feature.ExperienceCalculator;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import seng302.utility.ExperienceCalculator;

/**
 * Cucumber tests for ensuring the experience calculator returns the correct values.
 */
public class ExperienceCalculatorSteps {
    int correct;

    int answered;


    @Given("^I have completed a tutoring session$")
    public void i_have_completed_a_tutoring_session() {
        // pass
    }

    @When("^I get (\\d+) questions out of (\\d+)$")
    public void i_get_questions_out_of(int correct, int answered) {
        this.answered = answered;
        this.correct = correct;
    }

    @Then("^The calculator returns a value of (\\d+) experience points$")
    public void the_calculator_returns_a_value_of_experience_points(int points) {
        assert (ExperienceCalculator.calculateExperience(correct, answered) == points);
    }
}
