package lva.patternmatcher;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static lva.patternmatcher.Utils.getMatchingIndex;
import static org.junit.Assert.*;


/**
 * @author vlitvinenko
 */
public class SuffixTrieTest {

    @Test
    public void should_find_all_matches_of_substring() {
        SuffixTrie<String> ast = new SuffixTrie<>(Collections.singletonList("abababa"));
        MatchingResultSet<String> res = ast.query("aba");

        assertEquals(0, getMatchingIndex(res, "abababa", 0, 3));
        assertEquals(1, getMatchingIndex(res, "abababa", 2, 5));
        assertEquals(2, getMatchingIndex(res, "abababa", 4, 7));
    }

    @Test
    public void should_find_all_matches_of_substring_for_all_inputs() {
        SuffixTrie<String> ast = new SuffixTrie<>(Arrays.asList("ab", "ac"));
        MatchingResultSet<String> res = ast.query("a");

        assertEquals(0, getMatchingIndex(res, "ab", 0, 1));
        assertEquals(0, getMatchingIndex(res, "ac", 0, 1));
    }

    @Test
    public void should_find_any_matches_of_substring_for_all_inputs() {
        SuffixTrie<String> ast = new SuffixTrie<>(Arrays.asList("ab", "ac"));

        assertEquals(0, getMatchingIndex(ast.query("b"), "ab", 1, 2));
        assertEquals(0, getMatchingIndex(ast.query("c"), "ac", 1, 2));
    }


    @Test
    public void should_find_all_substrings_non_overlapped() {
        SuffixTrie<String> ast = new SuffixTrie<>(Collections.singletonList("abc"));

        assertEquals(0, getMatchingIndex(ast.query("abc"), "abc", 0, 3));
        assertEquals(0, getMatchingIndex(ast.query("ab"), "abc", 0, 2));
        assertEquals(0, getMatchingIndex(ast.query("bc"), "abc", 1, 3));
        assertEquals(0, getMatchingIndex(ast.query("a"), "abc", 0, 1));
        assertEquals(0, getMatchingIndex(ast.query("b"), "abc", 1, 2));
        assertEquals(0, getMatchingIndex(ast.query("c"), "abc", 2, 3));

    }

    @Test
    public void should_find_all_substrings_overlapped() {
        SuffixTrie<String> ast = new SuffixTrie<>(Collections.singletonList("aaa"));

        assertEquals(0, getMatchingIndex(ast.query("aaa"), "aaa", 0, 3));

        assertEquals(0, getMatchingIndex(ast.query("aa"), "aaa", 0, 2));
        assertEquals(1, getMatchingIndex(ast.query("aa"), "aaa", 1, 3));

        assertEquals(0, getMatchingIndex(ast.query("a"), "aaa", 0, 1));
        assertEquals(1, getMatchingIndex(ast.query("a"), "aaa", 1, 2));
        assertEquals(2, getMatchingIndex(ast.query("a"), "aaa", 2, 3));
    }

    @Test
    public void should_return_empty_result_for_unknown_substring() {
        SuffixTrie<String> ast = new SuffixTrie<>(Collections.singletonList("abc"));
        assertTrue(ast.query("d").getResultSet().isEmpty());
    }

    @Test
    public void should_add_matchings_in_sorted_order() {
        SuffixTrie<String> ast = new SuffixTrie<>(Collections.singletonList("aaa"));
        MatchingResultSet<String> res = ast.query("a");

        assertEquals(0, getMatchingIndex(res, "aaa", 0, 1));
        assertEquals(1, getMatchingIndex(res, "aaa", 1, 2));
        assertEquals(2, getMatchingIndex(res, "aaa", 2, 3));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void should_return_unmodifiable_result() {
        SuffixTrie<String> ast = new SuffixTrie<>(Collections.singletonList("abc"));
        ast.query("d").add("a", 0, 1);
    }

}