package seng302.utility;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import seng302.data.Term;

/**
 * Used to store all the terms that have been added
 */
public class MusicalTermsTutorBackEnd {

    private ArrayList<Term> terms = new ArrayList<Term>();


    /**
     * Adds a new term if it is valid
     *
     * @param term The Term object to be added
     * @throws Exception when one or more fields are invalid (ie too long)
     */
    public void addTerm(Term term) throws Exception {
        List<String> errors = generateErrors(term);

        if (getTermByName(term.getMusicalTermName()) != null) {
            errors.add("Term with the name of " + term.getMusicalTermName() + " has already been added");
        }

        if (errors.size() > 0) {
            String errorMessage = String.join("\n", errors);

            throw new Exception(errorMessage);
        } else {
            terms.add(term);
        }

    }

    public ArrayList<Term> getTerms() {
        return terms;
    }

    public void setTerms(ArrayList<Term> t) {

        this.terms = t;
    }

    /**
     * Provided with the name of a term, deletes that term from the manager
     *
     * @param termName The name of the term to be deleted.
     */
    public void removeTerm(String termName) {
        int termToRemove = 0;
        for (Term term : this.terms) {
            if (term.getMusicalTermName().equals(termName)) {
                termToRemove = terms.indexOf(term);
            }
        }
        terms.remove(termToRemove);

    }

    /**
     * Alters any field or any number of fields of any musical term.
     *
     * @param termInfo A map containing the name (to find the term object by) and any altered
     *                 fields. May contain the following keys: oldName, name, category, origin,
     *                 description.
     * @throws Exception if any of the edited content is too long, or a term with the new name
     *                   already exists.
     */
    public void editTerm(Map<String, String> termInfo) throws Exception {
        Term editedTerm = getTermByName(termInfo.get("oldName"));

        Term dummyTerm = new Term(termInfo.get("name"), termInfo.get("category"), termInfo.get("origin"), termInfo.get("definition"));

        if (editedTerm != null) {
            List<String> errors = new ArrayList<>();
            boolean nameChanged = !termInfo.get("oldName").equalsIgnoreCase(termInfo.get("name"));

            if (nameChanged && getTermByName(termInfo.get("name")) != null) {
                // Throws an exception if the name was changed to the name of a different,
                // existing term.
                errors.add("Term with the name of " + termInfo.get("name") + " has already been added");
            } else {
                errors.addAll(generateErrors(dummyTerm));
            }

            if (errors.size() > 0) {
                throw new Exception(String.join("\n", errors));
            } else {
                for (Map.Entry<String, String> entry : termInfo.entrySet()) {
                    editedTerm.updateInfo(entry.getKey(), entry.getValue());
                }
            }

        }

    }

    /**
     * Given a string, attempts to find a musical term by the name of that string.
     *
     * @param name A string representing a musical term to be found
     * @return The term with the given name if applicable, else null.
     */
    public Term getTermByName(String name) {
        for (Term term : terms) {
            if (term.getMusicalTermName().equalsIgnoreCase(name)) {
                return term;
            }
        }
        return null;
    }

    /**
     * Looks for a given substring inside the name, category, origin, and description of all terms
     *
     * @param searchSequence The substring to be found
     * @return A list of terms whose fields contain searchSequence, in some capacity.
     */
    public List<Term> search(String searchSequence) {
        List<Term> searchResults = new ArrayList<>();


        for (Term term : terms) {
            // Uppercases everything so that case does not matter when searching for terms
            if (term.getMusicalTermName().toUpperCase().contains(searchSequence.toUpperCase())) {
                searchResults.add(term);
            } else if (term.getMusicalTermCategory().toUpperCase().contains(searchSequence.toUpperCase())) {
                searchResults.add(term);
            } else if (term.getMusicalTermDefinition().toUpperCase().contains(searchSequence.toUpperCase())) {
                searchResults.add(term);
            } else if (term.getMusicalTermOrigin().toUpperCase().contains(searchSequence.toUpperCase())) {
                searchResults.add(term);
            }
        }
        return searchResults;
    }

    /**
     * Checks that each field of a new term is valid. That is, that the term name is at least one
     * character, and that all fields are 100 characters or less.
     *
     * @return An arraylist containing textual representations of invalid fields of the given term
     */
    private List<String> generateErrors(Term term) {

        ArrayList<String> errors = new ArrayList<>();
        if (term.getMusicalTermName().length() == 0) {
            errors.add("Term name must not be empty.");
        }
        if (term.getMusicalTermCategory().length() > 100) {
            errors.add("Your musical term category exceeds 100 characters. Please give a shorter category.");
        }
        if (term.getMusicalTermName().length() > 100) {
            errors.add("Your musical term name exceeds 100 characters. Please give a shorter name.");
        }
        if (term.getMusicalTermOrigin().length() > 100) {
            errors.add("Your musical term origin exceeds 100 characters. Please give a shorter origin.");
        }
        if (term.getMusicalTermDefinition().length() > 100) {
            errors.add("Your musical term definition exceeds 100 characters. Please give a shorter definition.");
        }

        return errors;
    }
}
