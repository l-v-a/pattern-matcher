package lva.patternmatcher;

import lva.patternmatcher.MatchingResultSet.Matching;
import lva.patternmatcher.MatchingResultSet.MatchingEntries;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author vlitvinenko
 */
public class MatchingEntriesTest {

    @Test
    public void should_return_first_matching() {
        MatchingEntries entries = new MatchingEntries()
            .add(0, 1)
            .add(1, 2);
        assertEquals(new Matching(0, 1), entries.getFirstMatching());
    }

    @Test
    public void should_return_null_for_first_matching_if_empty() {
        MatchingEntries entries = new MatchingEntries();
        assertNull(entries.getFirstMatching());
    }

    @Test
    public void should_return_last_matching() {
        MatchingEntries entries = new MatchingEntries()
            .add(0, 1)
            .add(1, 2);
        assertEquals(new Matching(1, 2), entries.getLastMatching());
    }

    @Test
    public void should_return_null_for_last_matching_if_empty() {
        MatchingEntries entries = new MatchingEntries();
        assertNull(entries.getLastMatching());
    }

    @Test
    public void should_return_nearest_matching_if_equals() {
        MatchingEntries entries = new MatchingEntries()
            .add(1, 2)
            .add(3, 4);

        assertEquals(new Matching(1, 2), entries.findNearestMatching(new Matching(0, 1)));
    }

    @Test
    public void should_return_nearest_matching_if_greater() {
        MatchingEntries entries = new MatchingEntries()
            .add(2, 3)
            .add(3, 4);

        assertEquals(new Matching(2, 3), entries.findNearestMatching(new Matching(0, 1)));
    }

    @Test
    public void should_return_nearest_matching_between() {
        MatchingEntries entries = new MatchingEntries()
            .add(10, 20)
            .add(30, 40)
            .add(50, 60);

        assertEquals(new Matching(50, 60), entries.findNearestMatching(new Matching(41, 42)));
    }

    @Test
    public void should_return_null_if_no_nearest_matching() {
        MatchingEntries entries = new MatchingEntries()
            .add(1, 2)
            .add(3, 4);

        assertNull(entries.findNearestMatching(new Matching(3, 4)));
    }

    public void could_not_accept_lesser_matchings() {
        MatchingEntries entries = new MatchingEntries()
            .add(1, 2)
            .add(0, 1);

        MatchingEntries expected = new MatchingEntries()
            .add(1, 2);

        assertEquals(expected, entries);

    }

}