package seng302.command;

import seng302.Environment;
import seng302.data.Term;

/**
 * Created by erp35 on 21/09/16.
 */
public class DeleteTermCommand implements Command {

    private String termToDelete;

    public DeleteTermCommand(String termToDelete) {
        this.termToDelete = termToDelete;
    }


    @Override
    public void execute(Environment env) {
        try {
            env.getMttDataManager().removeTerm(termToDelete);
            env.getUserHandler().getCurrentUser().checkMusicTerms();
            env.getTranscriptManager().setResult("Deleted term " + termToDelete);
        } catch (Exception e) {
            env.error("Could not delete term " + termToDelete);
        }
    }

    @Override
    public String getHelp() {
        return "Deletes the musical term with the provided name.";
    }

    @Override
    public String getCommandText() {
        return "delete musical term";
    }

    @Override
    public String getExample() {
        return "delete musical term lento";
    }
}
