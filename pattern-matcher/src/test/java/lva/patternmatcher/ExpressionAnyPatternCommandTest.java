package lva.patternmatcher;

import lva.patternmatcher.PatternCommandTokenizer.Command;
import org.junit.Test;

import static lva.patternmatcher.MatchingResultSet.emptyResultSet;
import static org.junit.Assert.*;

/**
 * @author vlitvinenko
 */
public class ExpressionAnyPatternCommandTest {
    private final Command command = new ExpressionAnyPatternCommand("pattern");

    @Test
    public void should_combine_nearest_matchings_that_greater() {
        MatchingResultSet<String> l = new MatchingResultSet<String>()
            .add("a", 1, 2)
            .add("a", 3, 4);

        MatchingResultSet<String> r = new MatchingResultSet<String>()
            .add("a", 0, 1)
            .add("a", 5, 6)
            .add("a", 7, 8);

        MatchingResultSet<String> resultSet = command.execute(l, r);

        MatchingResultSet<String> expected = new MatchingResultSet<String>()
            .add("a", 1, 2)
            .add("a", 3, 4)
            .add("a", 5, 6);

        assertEquals(expected, resultSet);
    }

    @Test
    public void should_combine_nearest_matchings_that_equals() {
        MatchingResultSet<String> l = new MatchingResultSet<String>()
            .add("a", 1, 2)
            .add("a", 3, 4);

        MatchingResultSet<String> r = new MatchingResultSet<String>()
            .add("a", 0, 1)
            .add("a", 4, 5)
            .add("a", 6, 7);

        MatchingResultSet<String> resultSet = command.execute(l, r);

        MatchingResultSet<String> expected = new MatchingResultSet<String>()
            .add("a", 1, 2)
            .add("a", 3, 4)
            .add("a", 4, 5);

        assertEquals(expected, resultSet);
    }

    @Test
    public void should_combine_nearest_matchings_that_overlapped() {
        MatchingResultSet<String> l = new MatchingResultSet<String>()
            .add("a", 1, 12)
            .add("a", 3, 14);

        MatchingResultSet<String> r = new MatchingResultSet<String>()
            .add("a", 10, 110)
            .add("a", 15, 160)
            .add("a", 17, 180);

        MatchingResultSet<String> resultSet = command.execute(l, r);

        MatchingResultSet<String> expected = new MatchingResultSet<String>()
            .add("a", 1, 12)
            .add("a", 3, 14)
            .add("a", 15, 160);

        assertEquals(expected, resultSet);
    }

    @Test
    public void should_filter_out_entries_without_nearest_matchings() {
        MatchingResultSet<String> l = new MatchingResultSet<String>()
            .add("a", 1, 2)
            .add("a", 3, 4);

        MatchingResultSet<String> r = new MatchingResultSet<String>()
            .add("a", 3, 4);

        MatchingResultSet<String> resultSet = command.execute(l, r);

        assertEquals(emptyResultSet(), resultSet);

    }

    @Test
    public void should_filter_out_entries_without_matchings() {
        MatchingResultSet<String> l = new MatchingResultSet<String>()
            .add("a", 1, 2);

        MatchingResultSet<String> r = new MatchingResultSet<String>()
            .add("b", 1, 2);

        MatchingResultSet<String> resultSet = command.execute(l, r);

        assertEquals(emptyResultSet(), resultSet);
    }

    @Test
    public void should_return_new_resultset() {
        MatchingResultSet<String> l = new MatchingResultSet<String>()
            .add("a", 1, 2);

        MatchingResultSet<String> r = new MatchingResultSet<String>()
            .add("a", 3, 4);

        MatchingResultSet<String> resultSet = command.execute(l, r);

        assertNotSame(resultSet, l);
        assertNotSame(resultSet, r);
    }

}