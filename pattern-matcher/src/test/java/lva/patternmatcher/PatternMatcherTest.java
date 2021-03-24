package lva.patternmatcher;

import org.junit.Test;

import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;

/**
 * @author vlitvinenko
 */
public class PatternMatcherTest {
    private final PatternMatcher<String> matcher = new PatternMatcher<>(
        Stream.of("AbcDef", "AbcDefGhj", "xyzAbcDef")
    );

    @Test
    public void should_return_matching_for_pattern_begin() {
        MatchingResultSet<String> resultSet = matcher.match("Abc");
        MatchingResultSet<String> expected = new MatchingResultSet<String>()
            .add("AbcDef", 0, 3)
            .add("AbcDefGhj", 0, 3);
        assertEquals(expected, resultSet);
    }

    @Test
    public void should_return_matching_for_pattern_begin_anny() {
        MatchingResultSet<String> resultSet = matcher.match("*Abc");
        MatchingResultSet<String> expected = new MatchingResultSet<String>()
            .add("AbcDef", 0, 3)
            .add("AbcDefGhj", 0, 3)
            .add("xyzAbcDef", 3, 6);
        assertEquals(expected, resultSet);
    }

    @Test
    public void should_return_matching_for_pattern_expression_strict() {
        MatchingResultSet<String> resultSet = matcher.match("AD");
        MatchingResultSet<String> expected = new MatchingResultSet<String>()
            .add("AbcDef", 0, 1)
            .add("AbcDef", 3, 4)
            .add("AbcDefGhj", 0, 1)
            .add("AbcDefGhj", 3, 4);

        assertEquals(expected, resultSet);
    }

    @Test
    public void should_return_matching_for_pattern_expression_strict_with_any() {
        MatchingResultSet<String> resultSet = matcher.match("*AD");
        MatchingResultSet<String> expected = new MatchingResultSet<String>()
            .add("AbcDef", 0, 1)
            .add("AbcDef", 3, 4)
            .add("AbcDefGhj", 0, 1)
            .add("AbcDefGhj", 3, 4)
            .add("xyzAbcDef", 3, 4)
            .add("xyzAbcDef", 6, 7);

        assertEquals(expected, resultSet);
    }

    @Test
    public void should_return_matching_for_pattern_expression_any() {
        MatchingResultSet<String> resultSet = matcher.match("A*f");
        MatchingResultSet<String> expected = new MatchingResultSet<String>()
            .add("AbcDef", 0, 1)
            .add("AbcDef", 5, 6)
            .add("AbcDefGhj", 0, 1)
            .add("AbcDefGhj", 5, 6);

        assertEquals(expected, resultSet);
    }

}