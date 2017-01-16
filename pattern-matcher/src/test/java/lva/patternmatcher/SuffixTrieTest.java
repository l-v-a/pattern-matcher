package lva.patternmatcher;

import org.junit.Test;

import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


/**
 * @author vlitvinenko
 */
public class SuffixTrieTest {

    @Test
    public void should_find_all_matches_of_substring() {
        SuffixTrie<String> ast = new SuffixTrie<>(Stream.of("abababa"));
        MatchingResultSet<String> res = ast.search("aba");

        MatchingResultSet<String> expected = new MatchingResultSet<String>()
            .add("abababa", 0, 3)
            .add("abababa", 2, 5)
            .add("abababa", 4, 7);

        assertEquals(expected, res);
    }

    @Test
    public void should_find_all_matches_of_substring_for_all_inputs() {
        SuffixTrie<String> ast = new SuffixTrie<>(Stream.of("ab", "ac"));
        MatchingResultSet<String> res = ast.search("a");

        MatchingResultSet<String> expected = new MatchingResultSet<String>()
            .add("ab", 0, 1)
            .add("ac", 0, 1);

        assertEquals(expected, res);
    }

    @Test
    public void should_index_equals_word() {
        SuffixTrie<String> ast = new SuffixTrie<>(Stream.of("ab", "ab"));
        MatchingResultSet<String> res = ast.search("a");

        MatchingResultSet<String> expected = new MatchingResultSet<String>()
            .add("ab", 0, 1);
        assertEquals(expected, res);
    }


    @Test
    public void should_find_any_matches_of_substring_for_all_inputs() {
        SuffixTrie<String> ast = new SuffixTrie<>(Stream.of("ab", "ac"));

        assertEquals(resultSetOf("ab", 1, 2), ast.search("b"));
        assertEquals(resultSetOf("ac", 1, 2), ast.search("c"));
    }


    @Test
    public void should_find_all_substrings_non_overlapped() {
        SuffixTrie<String> ast = new SuffixTrie<>(Stream.of("abc"));

        assertEquals(resultSetOf("abc", 0, 3), ast.search("abc"));
        assertEquals(resultSetOf("abc", 0, 2), ast.search("ab"));
        assertEquals(resultSetOf("abc", 1, 3), ast.search("bc"));
        assertEquals(resultSetOf("abc", 0, 1), ast.search("a"));
        assertEquals(resultSetOf("abc", 1, 2), ast.search("b"));
        assertEquals(resultSetOf("abc", 2, 3), ast.search("c"));
    }

    @Test
    public void should_find_all_substrings_overlapped() {
        SuffixTrie<String> ast = new SuffixTrie<>(Stream.of("aaa"));

        MatchingResultSet<String> expected = new MatchingResultSet<String>()
            .add("aaa", 0, 3);
        assertEquals(expected, ast.search("aaa"));

        expected = new MatchingResultSet<String>()
            .add("aaa", 0, 2)
            .add("aaa", 1, 3);
        assertEquals(expected, ast.search("aa"));


        expected = new MatchingResultSet<String>()
            .add("aaa", 0, 1)
            .add("aaa", 1, 2)
            .add("aaa", 2, 3);
        assertEquals(expected, ast.search("a"));
    }

    @Test
    public void should_return_empty_result_for_unknown_substring() {
        SuffixTrie<String> ast = new SuffixTrie<>(Stream.of("abc"));
        assertTrue(ast.search("d").getResultSet().isEmpty());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void should_return_unmodifiable_result() {
        SuffixTrie<String> ast = new SuffixTrie<>(Stream.of("abc"));
        ast.search("d").add("a", 0, 1);
    }

    @Test
    public void should_skip_null_words() {
        new SuffixTrie<>(Stream.of("abc", null));
    }

    private static MatchingResultSet<String> resultSetOf(String word, int from, int to) {
        return new MatchingResultSet<String>()
            .add(word, from, to);
    }

}