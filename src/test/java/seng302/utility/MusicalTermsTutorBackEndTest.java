package seng302.utility;

import org.junit.Before;
import org.junit.Test;

import seng302.data.Term;

/**
 * Created by emily on 1/05/16.
 */
public class MusicalTermsTutorBackEndTest {

    public MusicalTermsTutorBackEnd backEnd;

    @Before
    public void setUp() throws Exception {
        backEnd = new MusicalTermsTutorBackEnd();
    }

    @Test
    public void testAddTerm() {
        Term testTerm = new Term("a", "b", "c", "d");
        try {
            backEnd.addTerm(testTerm);
        } catch (Exception e) {
            e.printStackTrace();
        }
        assert backEnd.getTerms().size() == 1;
        assert backEnd.getTerms().contains(testTerm);
    }

    @Test
    public void testAddMultipleTerms() {
        Term testTerm1 = new Term("a", "b", "c", "d");
        Term testTerm2 = new Term("e", "f", "g", "h");
        Term testTerm3 = new Term("i", "j", "k", "l");

        try {
            backEnd.addTerm(testTerm1);
            backEnd.addTerm(testTerm2);
            backEnd.addTerm(testTerm3);
        } catch (Exception e) {
            e.printStackTrace();
        }

        assert backEnd.getTerms().size() == 3;
        assert backEnd.getTerms().contains(testTerm1);
        assert backEnd.getTerms().contains(testTerm2);
        assert backEnd.getTerms().contains(testTerm3);
    }
}
