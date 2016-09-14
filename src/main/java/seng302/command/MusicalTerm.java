

package seng302.command;

/**
 * MusicalTerm is used to look up and add musical terms to the musical terms hashmap Created by
 * Sarah on 3/04/2016.
 */

import java.util.ArrayList;
import java.util.List;

import seng302.Environment;
import seng302.data.Term;

public class MusicalTerm implements Command {
    private String result;
    private String input;
    //protected static HashMap<String, Term> MusicalTermsMap = new HashMap<String, Term>();

    private boolean termAdded = false;
    private boolean validAdd = true;
    public Term term = null;


    private String musicalTermName;
    private String infoToGet;
    private boolean lookupTerm = false;
    private boolean error = false;

    private ArrayList<String> rawInput;

    private String type;

    public ArrayList<Term> terms;


    /**
     * Checks to see if a musical term stream has been inputted and splits this input into the 4
     * corresponding categories: name; origin; category; definition.
     *
     * If the musical term name is not null, it will look up to see if it exists in the dictionary
     * and reutrn the definition, else it will notify the user that the musical term is not in the
     * dictionary.
     *
     * If the musical term name is null, it will add it to the dictionary and format it accordingly
     */
    public MusicalTerm(ArrayList<String> musicalTermArray) {
        infoToGet = "add";
        rawInput = musicalTermArray;
        termAdded = true;
        term = new Term(musicalTermArray.get(0), musicalTermArray.get(2), musicalTermArray.get(1), musicalTermArray.get(3));

    }

    /**
     * Displays information about a given musical term.
     *
     * @param termToLookUp The musical term in question
     * @param infoToGet    Whether we are fetching the musical term's category, origin, or
     *                     definition.
     */
    public MusicalTerm(String termToLookUp, String infoToGet) {
        lookupTerm = true;
        musicalTermName = termToLookUp.toLowerCase();
        this.infoToGet = infoToGet.toLowerCase();
    }


    private void addTermBool() {
        boolean resultSet = false;
        for (Term term : terms) {
            if (term.getMusicalTermName().equals(this.term.getMusicalTermName())) {
                validAdd = false;
                this.result = "[ERROR] Term with the name of " + this.term.getMusicalTermName() + " has already been added";
                resultSet = true;
            } else if (term.getMusicalTermDefinition().length() > 100) {
                validAdd = false;
                this.result = "[ERROR] Your musical term definition exceeds 100 characters. Please give a shorter definition.";
                resultSet = true;
            } else if (term.getMusicalTermCategory().length() > 100) {
                validAdd = false;
                this.result = "[ERROR] Your musical term category exceeds 100 characters. Please give a shorter category.";
                resultSet = true;
            } else if (term.getMusicalTermOrigin().length() > 100) {
                validAdd = false;
                this.result = "[ERROR] Your musical term origin exceeds 100 characters. Please give a shorter origin.";
                resultSet = true;
            } else if (term.getMusicalTermName().length() > 100) {
                validAdd = false;
                this.result = "[ERROR] Your musical term name exceeds 100 characters. Please give a shorter name.";
                resultSet = true;
            } else if (!resultSet) {
                this.result = "Added term: " + this.term.getMusicalTermName() +
                        "\nOrigin: " + this.term.getMusicalTermOrigin() + " \nCategory: " +
                        this.term.getMusicalTermCategory() + "\nDefinition: "
                        + this.term.getMusicalTermDefinition();
            }
        }
        if (!resultSet) {
            this.result = "Added term: " + this.term.getMusicalTermName() +
                    "\nOrigin: " + this.term.getMusicalTermOrigin() + " \nCategory: " +
                    this.term.getMusicalTermCategory() + "\nDefinition: "
                    + this.term.getMusicalTermDefinition();
        }
    }

    private void lookupTerm() {
        boolean resultSet = false;
        for (Term term : this.terms) {
            if (term.getMusicalTermName().equals(musicalTermName)) {
                // Returns the correct information
                if (infoToGet.equals("meaning")) {
                    this.result = term.getMusicalTermDefinition();
                    resultSet = true;
                } else if (infoToGet.equals("origin")) {
                    this.result = term.getMusicalTermOrigin();
                    resultSet = true;
                } else if (infoToGet.equals("category")) {
                    this.result = term.getMusicalTermCategory();
                    resultSet = true;
                } else {
                    // What the user is looking for is invalid.
                    // This may never be reachable by the DSL, but is good to have regardless.
                    this.result = String.format("[ERROR] %s is not recognised as part of a musical term.",
                            infoToGet);
                    resultSet = true;
                    error = true;
                }

                //if a given term is not in the hash map it will return an error to the user
            } else if (!resultSet) {
                this.result = String.format("[ERROR] %s is not recognised as an existing musical term.",
                        musicalTermName);
                error = true;
            }

        }
        if (!resultSet) {
            this.result = String.format("[ERROR] %s is not recognised as an existing musical term.",
                    musicalTermName);
            error = true;
        }
    }

    /**
     * will add the musical term to the dictionary, or print the relevant definition of the musical
     * term exists in the transcript manager
     */
    public void execute(Environment env) {
        this.terms = env.getMttDataManager().getTerms();
        if (lookupTerm) {
            lookupTerm();
        } else {
            addTermBool();
        }
        if (termAdded == true && validAdd == true) {
            env.getMttDataManager().addTerm(term);
            if (env.getUserHandler().getCurrentUser() != null)
                env.getUserHandler().getCurrentUser().checkMusicTerms();
            env.getEditManager().addToHistory("1", rawInput);
        }
        env.getTranscriptManager().setResult(result);
    }

    public String getHelp() {

        switch (infoToGet) {
            case "add":
                return "When followed by a musical term in the format of 'name; origin; " +
                        "category; definition', it will add the musical term to the Musical " +
                        "Term dictionary.";
            case "origin":
                return "When followed by a musical term, will display the origin of that term.";
            case "meaning":
                return "When followed by a musical term, will display the definition of that term.";
            case "category":
                return "When followed by a musical term, will display the category of that term.";
        }
        return null;

    }

    public List<String> getParams() {
        List<String> params = new ArrayList<>();
        if (infoToGet.equals("add")) {
            params.add("name; origin; category; definition");
        } else {
            params.add("musical term name");
        }
        return params;
    }

    @Override
    public String getCommandText() {
        switch (infoToGet) {
            case "add":
                return "add musical term";
            case "origin":
                return "origin of";
            case "meaning":
                return "meaning of";
            case "category":
                return "category of";
        }
        return null;
    }

    @Override
    public String getExample() {
        switch (infoToGet) {
            case "add":
                return "add musical term Lento; Italian; Tempo; Slowly";
            case "origin":
                return "origin of Lento";
            case "meaning":
                return "meaning of Lento";
            case "category":
                return "category of Lento";
        }
        return null;
    }


}

