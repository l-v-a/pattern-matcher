package lva.patternmatcher;

import lva.patternmatcher.PatternCommandTokenizer.Command;
import org.junit.Test;

import java.util.Arrays;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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

    @Test
    public void should_pass_result_set_from_previous_command_to_next() {
        @SuppressWarnings("unchecked")
        Searchable<String> searchable = (Searchable<String>) mock(Searchable.class);
        PatternCommandTokenizer tokenizer = mock(PatternCommandTokenizer.class);
        PatternMatcher<String> matcher = new PatternMatcher<>(tokenizer, searchable);

        Command command1 = mock(Command.class);
        Command command2 = mock(Command.class);
        MatchingResultSet<String> searchRs = new MatchingResultSet<>();
        MatchingResultSet<String> resultSet1 = new MatchingResultSet<>();
        MatchingResultSet<String> resultSet2 = new MatchingResultSet<>();

        when(searchable.search(anyString())).thenReturn(searchRs);
        when(command1.execute(any(), same(searchRs))).thenReturn(resultSet1);
        when(command2.execute(same(resultSet1), same(searchRs))).thenReturn(resultSet2);
        when(tokenizer.nextCommand()).thenReturn(command1, command2, null);

        MatchingResultSet<String> result = matcher.match("pattern");

        assertSame(resultSet2, result);
    }

}