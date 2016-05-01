package seng302.command;


import seng302.Environment;

/**
 * Tempo is  used to return the current set tempo. It has a default value of 120BPM
 */
public class Tempo implements Command {
    private int tempo;
    private String result;
    private boolean isSetter;
    private boolean force;

    public Tempo(){
        this.isSetter = false;
    }

    /**
     * Given a tempo, checks if it is inside the valid range of 20-300 BPM
     * @param tempo the value to check
     * @return whether or not the tempo is inside a valid range
     */
    private boolean inValidRange(int tempo) {
        if (tempo >= 20 && tempo <= 300) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Changes the tempo to the given value. If the value is outside of the appropriate tempo range,
     * an error message will raise and notify the user
     */
    public Tempo(String tempo, boolean force) {
        this.isSetter = true;
        this.force = force;
        try {
            this.tempo = Integer.parseInt(tempo);
            if (!inValidRange(this.tempo)) {
                if (this.force == false) {
                    this.result = "Tempo outside valid range. Use 'force set tempo' command to " +
                            "override. Use 'help' for more information";
                } else {
                    this.result = String.format("Tempo changed to %d BPM", this.tempo);
                }

            } else {
                this.result = String.format("Tempo changed to %d BPM", this.tempo);
            }
        } catch (Exception e) {
            this.result = "Invalid tempo";
        }
    }

    /**
     * Executes the tempo command. It will return the current set tempo in BPM. If no tempo has
     * been set, it defaults to the value of 120BMP
     *
     */
    public void execute(Environment env) {
        if (isSetter){
            // Only change the tempo under valid circumstances
            if (force || inValidRange(tempo)) {
                env.getPlayer().setTempo(tempo);
            }

            env.getTranscriptManager().setResult(result);
            //Update project saved state
            env.getProjectHandler().checkChanges("tempo");

        } else {
            //is getting the tempo
            env.getTranscriptManager().setResult(env.getPlayer().getTempo() + " BPM");
        }

    }
}

