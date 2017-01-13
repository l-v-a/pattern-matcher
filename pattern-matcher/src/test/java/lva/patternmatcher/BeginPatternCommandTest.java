package lva.patternmatcher;

import lva.patternmatcher.PatternCommandTokenizer.Command;
import org.junit.Test;

import static lva.patternmatcher.MatchingResultSet.emptyResultSet;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

/**
 * @author vlitvinenko
 */
public class BeginPatternCommandTest {
    private final Command command = new BeginPatternCommand("pattern");

    @Test
    public void should_retain_begin_matchings() {
        MatchingResultSet<String> r = new MatchingResultSet<String>()
            .add("a", 0, 1)
            .add("a", 1, 2)
            .add("b", 0, 1);

        MatchingResultSet<String> resultSet = command.execute(emptyResultSet(), r);

        MatchingResultSet<String> expected = new MatchingResultSet<String>()
            .add("a", 0, 1)
            .add("b", 0, 1);

        assertEquals(expected, resultSet);
    }

    @Test
    public void should_return_empty_if_none_suite() {
        MatchingResultSet<String> r = new MatchingResultSet<String>()
            .add("a", 1, 2)
            .add("b", 2, 3);

        MatchingResultSet<String> resultSet = command.execute(emptyResultSet(), r);
        assertEquals(emptyResultSet(), resultSet);
    }

    @Test
    public void should_return_new_resultset() {
        MatchingResultSet<String> r = new MatchingResultSet<String>()
            .add("a", 0, 1)
            .add("a", 1, 2)
            .add("b", 0, 1);

        MatchingResultSet<String> resultSet = command.execute(emptyResultSet(), r);

        assertNotSame(resultSet, r);
    }

}