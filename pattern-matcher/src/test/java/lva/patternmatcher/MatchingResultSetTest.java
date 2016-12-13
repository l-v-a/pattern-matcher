package lva.patternmatcher;

import org.junit.Test;

import static lva.patternmatcher.Utils.getMatchingIndex;
import static org.junit.Assert.*;

/**
 * @author vlitvinenko
 */
public class MatchingResultSetTest {

    @Test
    public void should_add_matching_for_word() {
        MatchingResultSet<String> resultSet = new MatchingResultSet<>();
        resultSet.add("a", 0, 1);

        assertEquals(1, resultSet.getResultSet().size());
        assertEquals(0, getMatchingIndex(resultSet, "a", 0, 1));
    }

    @Test
    public void should_add_many_matchings_for_word_in_order() {
        MatchingResultSet<String> resultSet = new MatchingResultSet<>();
        resultSet.add("a", 0, 1);
        resultSet.add("a", 1, 2);

        assertEquals(1, resultSet.getResultSet().size());
        assertEquals(0, getMatchingIndex(resultSet, "a", 0, 1));
        assertEquals(1, getMatchingIndex(resultSet, "a", 1, 2));
    }

    @Test
    public void should_not_change_order_for_matchings() {
        MatchingResultSet<String> resultSet = new MatchingResultSet<>();
        resultSet.add("a", 1, 2);
        resultSet.add("a", 0, 1);

        assertEquals(1, resultSet.getResultSet().size());
        assertEquals(0, getMatchingIndex(resultSet, "a", 1, 2));
        assertEquals(1, getMatchingIndex(resultSet, "a", 0, 1));
    }

    @Test
    public void should_add_matching_for_many_words() {
        MatchingResultSet<String> resultSet = new MatchingResultSet<>();
        resultSet.add("a", 0, 1);
        resultSet.add("b", 0, 1);

        assertEquals(2, resultSet.getResultSet().size());
        assertEquals(0, getMatchingIndex(resultSet, "a", 0, 1));
        assertEquals(0, getMatchingIndex(resultSet, "b", 0, 1));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void should_return_unmodifiable_result() {
        MatchingResultSet<String> resultSet = new MatchingResultSet<>();
        resultSet.add("a", 0, 1);

        resultSet.getResultSet().remove("a");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void should_return_unmodifiable_matchings() {
        MatchingResultSet<String> resultSet = new MatchingResultSet<>();
        resultSet.add("a", 0, 1);

        resultSet.getResultSet().get("a").getMatchings().remove(0);
    }
}