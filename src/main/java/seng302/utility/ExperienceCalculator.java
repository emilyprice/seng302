package seng302.utility;

/**
 * Calculates how much experience a user has earned
 * Currently, only used to calculate experience based on tutoring sessions.
 * This may be extended if experience gaining is introduced through other means.
 */
public class ExperienceCalculator {

    /**
     * Calculates how much experience a user will gain from a completed tutoring session.
     *
     * @param correctQuestions  How many questions the user answered correctly
     * @param answeredQuestions How many questions the user answered in total
     * @return The integer value of experience gained by the user in this tutoring session
     */
    public static int calculateExperience(int correctQuestions, int answeredQuestions) {
        int totalExperience = 0;

        //User gets 10 points for every correct question
        totalExperience += correctQuestions * 10;

        float percentage = 100 * correctQuestions / answeredQuestions;

        //User gets different bonuses based on their overall percentage
        int bonus = 0;
        if (percentage >= 90) {
            //If the user gets 90% to 100%, they get a ~10% point bonus
            bonus = (int) (0.1 * totalExperience);
        } else if (percentage >= 75) {
            //If the user gets 75% to 90%, they get a ~5% point bonus
            bonus = (int) (0.05 * totalExperience);
        }

        totalExperience += bonus;
        return totalExperience;
    }

}

