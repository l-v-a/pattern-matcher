package lva.patternmatcher;

import lva.patternmatcher.PatternCommandTokenizer.Command;
import org.junit.Test;

import static lva.patternmatcher.MatchingResultSet.emptyResultSet;
import static org.junit.Assert.*;

/**
 * @author vlitvinenko
 */
public class ExpressionStrictPatternCommandTest {
    // private final Command command = new ExpressionStrictPatternCommand("pattern");

    @Test
    public void should_combine_nearest_matchings_that_greater_and_lower_cased() {
        Command command = new ExpressionStrictPatternCommand("D");

        MatchingResultSet<String> l = new MatchingResultSet<String>()
            .add("AbcDefDef", 0, 1)
            .add("AbcDefDef", 1, 2);

        MatchingResultSet<String> r = new MatchingResultSet<String>()
            .add("AbcDefDef", 3, 4)
            .add("AbcDefDef", 6, 7);

        MatchingResultSet<String> resultSet = command.execute(l, r);

        MatchingResultSet<String> expected = new MatchingResultSet<String>()
            .add("AbcDefDef", 0, 1)
            .add("AbcDefDef", 1, 2)
            .add("AbcDefDef", 3, 4);

        assertEquals(expected, resultSet);
    }

    @Test
    public void should_combine_nearest_matchings_that_equals() {
        Command command = new ExpressionStrictPatternCommand("D");

        MatchingResultSet<String> l = new MatchingResultSet<String>()
            .add("AbcDefDef", 0, 1)
            .add("AbcDefDef", 1, 3);

        MatchingResultSet<String> r = new MatchingResultSet<String>()
            .add("AbcDefDef", 3, 4)
            .add("AbcDefDef", 6, 7);

        MatchingResultSet<String> resultSet = command.execute(l, r);

        MatchingResultSet<String> expected = new MatchingResultSet<String>()
            .add("AbcDefDef", 0, 1)
            .add("AbcDefDef", 1, 3)
            .add("AbcDefDef", 3, 4);

        assertEquals(expected, resultSet);
    }

    @Test
    public void should_filter_out_if_upper_cased_symbols_resides_between_matchings() {
        Command command = new ExpressionStrictPatternCommand("D");

        MatchingResultSet<String> l = new MatchingResultSet<String>()
            .add("AbCDefDef", 0, 1)
            .add("AbCDefDef", 1, 2);

        MatchingResultSet<String> r = new MatchingResultSet<String>()
            .add("AbCDefDef", 3, 4)
            .add("AbCDefDef", 6, 7);

        MatchingResultSet<String> resultSet = command.execute(l, r);

        assertEquals(emptyResultSet(), resultSet);
    }

    @Test
    public void should_filter_out_entries_without_nearest_matchings() {
        Command command = new ExpressionStrictPatternCommand("A ");

        MatchingResultSet<String> l = new MatchingResultSet<String>()
            .add("AbcDefDef", 0, 1)
            .add("AbcDefDef", 1, 2);

        MatchingResultSet<String> r = new MatchingResultSet<String>()
            .add("AbcDefDef", 0, 1);

        MatchingResultSet<String> resultSet = command.execute(l, r);

        assertEquals(emptyResultSet(), resultSet);
    }

    @Test
    public void should_return_new_resultset() {
        Command command = new ExpressionStrictPatternCommand("D");

        MatchingResultSet<String> l = new MatchingResultSet<String>()
            .add("AbcDefDef", 0, 1)
            .add("AbcDefDef", 1, 2);

        MatchingResultSet<String> r = new MatchingResultSet<String>()
            .add("AbcDefDef", 3, 4)
            .add("AbcDefDef", 6, 7);

        MatchingResultSet<String> resultSet = command.execute(l, r);

        assertNotSame(resultSet, l);
        assertNotSame(resultSet, r);
    }


}