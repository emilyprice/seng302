package seng302.command;

import seng302.Environment;
import seng302.utility.rhythmFactory;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by jonty on 5/13/16.
 */
public class Rhythm implements Command{

    private String rhythm;
    private String result;
    private boolean isSetter;
    private boolean force;

    private float[] divisions;

    public Rhythm(){
        this.isSetter = false;
    }


    /**
     * Changes the rhythm to the given value. Accepts common swing timings or custom time division sequences.
     */
    public Rhythm(String rhythmStyle, boolean force) {
        this.isSetter = true;

        this.force = force;
        this.divisions = new float[]{0.5f};


        try {
            this.rhythm = rhythmStyle;

            if(rhythmStyle.equals("heavy")){
                this.result = "Rhythm set to heavy swing timing (3/2 1/4).";
                this.divisions = new float[]{3.0f/4.0f, 1.0f/4.0f};
            }

            else if(rhythmStyle.equals("medium")){
                this.result = "Rhythm set to medium swing timing (2/3 1/3).";
                this.divisions = new float[]{2.0f / 3.0f, 1.0f / 3.0f};
            }

            else if(rhythmStyle.equals("light")){
                this.result = "Rhythm set to light swing timing (5/8 3/8).";
                this.divisions = new float[]{5.0f/8.0f, 3.0f/8.0f};
            }

            else if(rhythmStyle.equals("straight")){
                this.result = "Rhythm set to straight, half crotchet timing (1/2).";
                this.divisions = new float[]{0.5f};
            }

            else{
                this.result = String.format("Invalid Rhythm option '%s'. See 'help set rhythm' for valid rhythm options",
                        rhythmStyle);

                //Checks if command entry is a custom input of sequence of time divisions.
                float[] toFloat = rhythmFactory.fractionStringToFloatArray(rhythmStyle);
                if(toFloat.length > 0){
                    this.result = String.format("Rhythm divisions set to: %s",Arrays.toString(toFloat).replaceAll("\\[\\]",""));
                    this.divisions = toFloat;
                }

            }

        } catch (Exception e) {
            this.result = String.format("Invalid Rhythm option '%s'. See 'help set rhythm' for valid rhythm options",
                    rhythmStyle);
        }
    }




    public Rhythm(float[] rhythmDivisions){
        this.result = String.format("Rhythm divisions set to: ({0})",Arrays.toString(rhythmDivisions).replaceAll("\\[\\]",""));
        this.divisions = rhythmDivisions;
    }

    public float getLength(Environment env) {
        return 0;
    };




    /**
     * Executes the rhythm command, adds the rhythm values to the history for undoing/redoing, and indicates the
     * project handler that the rhythm has been changed.
     */
    public void execute(Environment env) {
        if (isSetter){

            //Add Rhythm to editHistory before changes are made.

            ArrayList<String> editHistoryArray = new ArrayList<String>();
            editHistoryArray.add(String.valueOf(env.getPlayer().getRhythmHandler().toString()));


            env.getPlayer().getRhythmHandler().setRhythmTimings(divisions);


            env.getTranscriptManager().setResult(result);
            //Update project saved state
            env.getProjectHandler().checkChanges("rhythm");

            editHistoryArray.add(String.valueOf(env.getPlayer().getRhythmHandler().toString()));
            env.getEditManager().addToHistory("2", editHistoryArray); //Add Edit history changes after they are made.s


        } else {
            //is getting the rhythm
            env.getTranscriptManager().setResult(env.getPlayer().getRhythmHandler().toString());
        }

    }
}