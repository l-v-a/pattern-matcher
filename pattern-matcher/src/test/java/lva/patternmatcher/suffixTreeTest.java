package lva.patternmatcher;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


/**
 * @author vlitvinenko
 */
public class suffixTreeTest {

    @Test
    public void should_find_all_matches_of_substring() {
        SuffixTree<String> ast = new SuffixTree<>(List.of("abababa"));
        MatchingResultSet<String> res = ast.search("aba");

        MatchingResultSet<String> expected = new MatchingResultSet<String>()
            .add("abababa", 0, 3)
            .add("abababa", 2, 5)
            .add("abababa", 4, 7);

        assertEquals(expected, res);
    }

    @Test
    public void should_find_all_matches_of_substring_for_splitted_nodes() {
        SuffixTree<String> ast = new SuffixTree<>(List.of("aaab"));
        MatchingResultSet<String> res = ast.search("aa");

        MatchingResultSet<String> expected = new MatchingResultSet<String>()
            .add("aaab", 0, 2)
            .add("aaab", 1, 3);

        assertEquals(expected, res);
    }

    @Test
    public void should_find_all_matches_of_substring_for_all_inputs() {
        SuffixTree<String> ast = new SuffixTree<>(List.of("ab", "ac"));
        MatchingResultSet<String> res = ast.search("a");

        MatchingResultSet<String> expected = new MatchingResultSet<String>()
            .add("ab", 0, 1)
            .add("ac", 0, 1);

        assertEquals(expected, res);
    }

    @Test
    public void should_find_all_words_for_empty_pattern() {
        SuffixTree<String> ast = new SuffixTree<>(List.of("ab", "ac"));
        MatchingResultSet<String> res = ast.search("");

        MatchingResultSet<String> expected = new MatchingResultSet<String>()
            .add("ab", 2, 3)
            .add("ac", 2, 3);

        assertEquals(expected, res);
    }

    @Test
    public void should_index_equals_word() {
        SuffixTree<String> ast = new SuffixTree<>(List.of("ab", "ab"));
        MatchingResultSet<String> res = ast.search("a");

        MatchingResultSet<String> expected = new MatchingResultSet<String>()
            .add("ab", 0, 1);
        assertEquals(expected, res);
    }


    @Test
    public void should_find_any_matches_of_substring_for_all_inputs() {
        SuffixTree<String> ast = new SuffixTree<>(List.of("ab", "ac"));

        assertEquals(resultSetOf("ab", 1, 2), ast.search("b"));
        assertEquals(resultSetOf("ac", 1, 2), ast.search("c"));
    }


    @Test
    public void should_find_all_substrings_non_overlapped() {
        SuffixTree<String> ast = new SuffixTree<>(List.of("abc"));

        assertEquals(resultSetOf("abc", 0, 3), ast.search("abc"));
        assertEquals(resultSetOf("abc", 0, 2), ast.search("ab"));
        assertEquals(resultSetOf("abc", 1, 3), ast.search("bc"));
        assertEquals(resultSetOf("abc", 0, 1), ast.search("a"));
        assertEquals(resultSetOf("abc", 1, 2), ast.search("b"));
        assertEquals(resultSetOf("abc", 2, 3), ast.search("c"));
    }

    @Test
    public void should_find_all_substrings_overlapped() {
        SuffixTree<String> ast = new SuffixTree<>(List.of("aaa"));

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
        SuffixTree<String> ast = new SuffixTree<>(List.of("abc"));
        assertTrue(ast.search("ac").getResultSet().isEmpty());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void should_return_unmodifiable_result() {
        SuffixTree<String> ast = new SuffixTree<>(List.of("abc"));
        ast.search("d").add("a", 0, 1);
    }

    @Test
    public void should_skip_null_words() {
        new SuffixTree<>(Arrays.asList("abc", null));
    }

    private static MatchingResultSet<String> resultSetOf(String word, int from, int to) {
        return new MatchingResultSet<String>()
            .add(word, from, to);
    }

}