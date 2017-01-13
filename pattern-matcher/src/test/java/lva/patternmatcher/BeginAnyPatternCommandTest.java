package lva.patternmatcher;

import lva.patternmatcher.PatternCommandTokenizer.Command;
import org.junit.Test;

import static lva.patternmatcher.MatchingResultSet.emptyResultSet;
import static org.junit.Assert.*;

/**
 * @author vlitvinenko
 */
public class BeginAnyPatternCommandTest {
    private final Command command = new BeginAnyPatternCommand("pattern");

    @Test
    public void should_retain_first_matchings() {
        MatchingResultSet<String> r = new MatchingResultSet<String>()
            .add("a", 1, 2)
            .add("a", 3, 4)
            .add("b", 1, 2);

        MatchingResultSet<String> resultSet = command.execute(emptyResultSet(), r);

        MatchingResultSet<String> expected = new MatchingResultSet<String>()
            .add("a", 1, 2)
            .add("b", 1, 2);

        assertEquals(expected, resultSet);
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