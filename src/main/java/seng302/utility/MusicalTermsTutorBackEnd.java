package seng302.utility;

import java.util.ArrayList;

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
}
