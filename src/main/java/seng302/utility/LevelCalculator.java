package seng302.utility;

/**
 * Utility class for calculating information related to user levels.
 */
public class LevelCalculator {

    /**
     * Checks if a user is able to level up, based on their current level and experience
     *
     * @param currentLevel      The level the user is currently on
     * @param currentExperience The amount of experience points the user currently has
     * @return True if the user can increase to the next level, false otherwise.
     */
    public static boolean isLevelUp(int currentLevel, int currentExperience) {
        if (currentExperience >= getRequiredExp(currentLevel + 1)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Calculates the total amount of experience a user requires to reach a given level.
     *
     * @param level The level to calculate the required experience for
     * @return The required experience, represented as an integer
     */
    public static int getRequiredExp(int level) {
        int amountRequired = 0;

        while (level > 1) {
            amountRequired += getExpDiff(level);
            level--;
        }
        return amountRequired;
    }

    /**
     * Recursive function to calculate the difference in experience required for any given level and
     * the level below it.
     *
     * @param level The level whose required experience is to be found
     * @return The required experience, represented as an integer
     */
    private static int getExpDiff(int level) {
        // amount required to get to the given level from the prev level
        if (level == 1) {
            return 1;
        } else {
            return (int) (Math.pow(level + 10, 2) + getExpDiff(level - 1));
        }
    }

}
