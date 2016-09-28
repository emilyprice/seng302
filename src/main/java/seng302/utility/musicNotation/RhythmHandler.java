package seng302.utility.musicNotation;

/**
 * Created by jonty on 5/13/16.
 *
 * Handles any rhythm functionality, such as containing information specific to rhythm.
 */
public class RhythmHandler {

    private int beatResolution; //Ticks per beat. (generally crotchet)
    int rIndex;
    int[] rhythmTimings;


    public RhythmHandler() {
        beatResolution = 24;
        rhythmTimings = new int[]{beatResolution / 2}; //Default of half the resolution (8th)
        rIndex = 0;

    }

    /**
     * Returns the next note play duration in respect to the beat resolution.
     *
     * @return next note play duration.
     */
    public int getNextTickTiming() {
        //Below line is bugged. Needs to add one to rIndex regardless.
        //return rIndex < rhythmTimings.length ? rhythmTimings[rIndex++] : rhythmTimings[rIndex = 0];

        if (rIndex >= rhythmTimings.length) rIndex = 0;
        return rhythmTimings[rIndex++];


    }

    /**
     * Sets rhythm timings using float time division values. such as 0.5f for half timing.
     *
     * @param divisions timing division array. i.e [1/2] for straight, or [2/3, 1/3] for medium
     *                  swing.
     */
    public void setRhythmTimings(float[] divisions) {
        int[] timings = new int[divisions.length];

        for (int i = 0; i < timings.length; i++) {
            timings[i] = (int) (beatResolution * divisions[i]);

        }
        this.rhythmTimings = timings;

    }

    /**
     * Used to directly set the timings if the beat resolution is known. For example, for straight,
     * half timing with a beat resolution of 24, the input will be [12]
     *
     * @param timings int array of beat timings.
     */
    public void setRhythmTimings(int[] timings) {
        this.rhythmTimings = timings;
        resetIndex();

    }

    public void setBeatResolution(int ticks) {
        beatResolution = ticks;
        resetIndex();
    }

    public int getBeatResolution() {
        return beatResolution;
    }

    public int[] getRhythmTimings() {
        return rhythmTimings;
    }

    public String toString() {
        String retString = "Rhythm beat divisions:";

        for (int timing : rhythmTimings) {
            retString += " " + RhythmFactory.asFraction(timing, beatResolution);

        }
        return retString;
    }


    public void resetIndex() {
        rIndex = 0;
    }


}
