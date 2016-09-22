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


    public void addTerm(Term term) {
        terms.add(term);
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
     */
    public void editTerm(Map<String, String> termInfo) {
        Term editedTerm = getTermByName(termInfo.get("oldName"));

        if (editedTerm != null) {

            for (Map.Entry<String, String> entry : termInfo.entrySet()) {

                // If a term exists with the new name, we cannot apply that change
                if (getTermByName(entry.getValue()) == null) {
                    editedTerm.updateInfo(entry.getKey(), entry.getValue());
                } else {
                    //TODO: tell the user in a nice way that this name is invalid
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
}
