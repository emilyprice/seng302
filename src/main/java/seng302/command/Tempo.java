package seng302.command;


import java.util.ArrayList;
import java.util.List;

import seng302.Environment;

/**
 * Tempo is  used to return the current set tempo. It has a default value of 120BPM
 */
public class Tempo implements Command {
    private int tempo;
    private String result;
    private boolean isSetter;
    private boolean force;

    public Tempo() {
        this.isSetter = false;
    }

    /**
     * a Given a tempo, checks if it is inside the valid range of 20-300 BPM
     *
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
                    if (this.tempo != 0) {
                        this.result = String.format("Tempo changed to %d BPM", this.tempo);
                    } else {
                        // 0 is never a valid tempo, you can't force it
                        this.force = false;
                        throw new Exception();
                    }
                }

            } else {
                this.result = String.format("Tempo changed to %d BPM", this.tempo);
            }
        } catch (Exception e) {
            this.result = "Invalid tempo";
        }
    }

    /**
     * Executes the tempo command. It will return the current set tempo in BPM. If no tempo has been
     * set, it defaults to the value of 120BMP
     */
    public void execute(Environment env) {
        if (isSetter) {

            // Add Tempo to editHistory

            ArrayList<String> editHistoryArray = new ArrayList<String>();
            editHistoryArray.add(String.valueOf(env.getPlayer().getTempo()));
            editHistoryArray.add(String.valueOf(tempo));
            env.getEditManager().addToHistory("0", editHistoryArray);

            // Only change the tempo under valid circumstances
            if (force || inValidRange(tempo)) {
                env.getPlayer().setTempo(tempo);
                env.getUserPageController().tempoLabel.setText("The current tempo is set to " + String.valueOf(tempo) + " BPM");
                env.getUserPageController().updateCurrentTempo(tempo);
                env.getUserPageController().updateMetronome();

            }
            if (force) {
                try {
                    env.getUserHandler().getCurrentUser().getProjectHandler().getCurrentProject().getBadgeManager().unlockBadge("Speedster");
                } catch (NullPointerException e) {
                    // For Git testing. Badges are not yet generated when testing
                }
            }

            env.getTranscriptManager().setResult(result);
            //Update project saved state
            if(env.getUserHandler().getCurrentUser() != null)
                env.getUserHandler().getCurrentUser().getProjectHandler().getCurrentProject().checkChanges("tempo");

        } else {
            //is getting the tempo
            env.getTranscriptManager().setResult(env.getPlayer().getTempo() + " BPM");
        }

    }





    public String getHelp() {
        if (isSetter) {
            if (force) {
                return "When followed by a tempo, it will set the given tempo, " +
                        "even if it is outside of the recommended range of 20-300BPM.";
            } else {
                return "When followed by a valid tempo (20-300BPM), sets the tempo to that value.";
            }
        } else {
            return "Returns the current tempo. The default value is 120BPM.";
        }
    }

    public List<String> getParams() {
        List<String> params = new ArrayList<>();
        if (isSetter) {
            if (force) {
                params.add("1 or higher");
            } else {
                params.add("20-300");
            }
        }

        return params;
    }

    @Override
    public String getCommandText() {
        if (isSetter) {
            if (force) {
                return "force set tempo";
            } else {
                return "set tempo";
            }
        } else {
            return "tempo";
        }
    }

    @Override
    public String getExample() {
        if (isSetter) {
            if (force) {
                return "force set tempo 3000";
            } else {
                return "set tempo 150";
            }
        } else {
            return "tempo";
        }
    }
}

