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


    public void addTerm(Term term) throws Exception {
        if (getTermByName(term.getMusicalTermName()) != null) {
            throw new Exception("Term with the name of " + term.getMusicalTermName() + " has already been added");
        } else if (term.getMusicalTermCategory().length() > 100) {
            throw new Exception("Your musical term category exceeds 100 characters. Please give a shorter category.");
        } else if (term.getMusicalTermName().length() > 100) {
            throw new Exception("Your musical term name exceeds 100 characters. Please give a shorter name.");
        } else if (term.getMusicalTermOrigin().length() > 100) {
            throw new Exception("Your musical term origin exceeds 100 characters. Please give a shorter origin.");
        } else if (term.getMusicalTermDefinition().length() > 100) {
            throw new Exception("Your musical term definition exceeds 100 characters. Please give a shorter definition.");
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

        if (editedTerm != null) {
            boolean nameChanged = !termInfo.get("oldName").equalsIgnoreCase(termInfo.get("name"));

            for (Map.Entry<String, String> entry : termInfo.entrySet()) {

                if (entry.getKey().equals("name") && nameChanged && getTermByName(entry.getValue()) != null) {
                    // Throws an exception if the name was changed to the name of a different,
                    // existing term.
                    throw new Exception("Term with the name of " + entry.getValue() + " has already been added");
                } else {
                    if (entry.getValue().length() > 100) {
                        throw new Exception(String.format("Your musical term" +
                                        " %s exceeds 100 characters. Please give a shorter %s.",
                                entry.getKey(), entry.getKey()));
                    } else {
                        editedTerm.updateInfo(entry.getKey(), entry.getValue());
                    }
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
