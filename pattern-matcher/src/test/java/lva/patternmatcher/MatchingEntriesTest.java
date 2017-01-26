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
        assertEquals(new Matching(0, 1), entries.getFirstMatching().get());
    }

    @Test
    public void should_return_empty_for_first_matching_if_empty() {
        MatchingEntries entries = new MatchingEntries();
        assertFalse(entries.getFirstMatching().isPresent());
    }

    @Test
    public void should_return_last_matching() {
        MatchingEntries entries = new MatchingEntries()
            .add(0, 1)
            .add(1, 2);
        assertEquals(new Matching(1, 2), entries.getLastMatching().orElse(null));
    }

    @Test
    public void should_return_empty_for_last_matching_if_empty() {
        MatchingEntries entries = new MatchingEntries();
        assertFalse(entries.getLastMatching().isPresent());
    }

    @Test
    public void should_return_nearest_matching_if_equals() {
        MatchingEntries entries = new MatchingEntries()
            .add(1, 2)
            .add(3, 4);

        assertEquals(new Matching(1, 2), entries.findNearestMatching(new Matching(0, 1)).orElse(null));
    }

    @Test
    public void should_return_nearest_matching_if_greater() {
        MatchingEntries entries = new MatchingEntries()
            .add(2, 3)
            .add(3, 4);

        assertEquals(new Matching(2, 3), entries.findNearestMatching(new Matching(0, 1)).orElse(null));
    }

    @Test
    public void should_return_nearest_matching_between() {
        MatchingEntries entries = new MatchingEntries()
            .add(10, 20)
            .add(30, 40)
            .add(50, 60);

        assertEquals(new Matching(50, 60), entries.findNearestMatching(new Matching(41, 42)).orElse(null));
    }

    @Test
    public void should_return_empty_if_no_nearest_matching() {
        MatchingEntries entries = new MatchingEntries()
            .add(1, 2)
            .add(3, 4);

        assertFalse(entries.findNearestMatching(new Matching(3, 4)).isPresent());
    }

    @Test
    public void could_not_accept_lesser_matchings() {
        MatchingEntries entries = new MatchingEntries()
            .add(1, 2)
            .add(0, 1);

        MatchingEntries expected = new MatchingEntries()
            .add(1, 2);

        assertEquals(expected, entries);
    }

    @Test
    public void should_return_left_part() {
        MatchingEntries entries = new MatchingEntries()
            .add(1, 4)
            .add(2, 10);

        MatchingEntries expected = new MatchingEntries()
            .add(1, 2)
            .add(2, 3);

        assertEquals(expected, entries.getLeft(1));
    }

    @Test
    public void should_return_empty_left_part_if_all_less() {
        MatchingEntries entries = new MatchingEntries()
            .add(1, 4)
            .add(2, 10);

        MatchingEntries expected = new MatchingEntries();
        assertEquals(expected, entries.getLeft(10));
    }

    @Test
    public void should_return_right_part() {
        MatchingEntries entries = new MatchingEntries()
            .add(1, 4)
            .add(2, 10);

        MatchingEntries expected = new MatchingEntries()
            .add(2, 4)
            .add(3, 10);

        assertEquals(expected, entries.getRight(1));
    }

    @Test
    public void should_return_empty_right_part_if_all_greater() {
        MatchingEntries entries = new MatchingEntries()
            .add(1, 4)
            .add(2, 10);

        MatchingEntries expected = new MatchingEntries();
        assertEquals(expected, entries.getRight(10));
    }


}