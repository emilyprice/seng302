package seng302.utility;

import java.util.ArrayList;
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

    public void removeTerm(String termName) {
        int termToRemove = 0;
        for (Term term : this.terms) {
            if (term.getMusicalTermName().equals(termName)) {
                termToRemove = terms.indexOf(term);
            }
        }
        terms.remove(termToRemove);

    }

    public void editTerm(Map<String, String> termInfo) {
        Term editedTerm = getTermByName(termInfo.get("oldName"));

        if (editedTerm != null) {

            for (Map.Entry<String, String> entry : termInfo.entrySet()) {
                editedTerm.updateInfo(entry.getKey(), entry.getValue());
            }

        }

    }

    public Term getTermByName(String name) {
        for (Term term : terms) {
            if (term.getMusicalTermName().equalsIgnoreCase(name)) {
                return term;
            }
        }
        return null;
    }
}
