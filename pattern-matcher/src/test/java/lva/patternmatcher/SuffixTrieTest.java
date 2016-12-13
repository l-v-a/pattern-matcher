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

        assertTrue(ast.query("abc").getResultSet().size() > 0);
        assertTrue(ast.query("ab").getResultSet().size() > 0);
        assertTrue(ast.query("bc").getResultSet().size() > 0);
        assertTrue(ast.query("a").getResultSet().size() > 0);
        assertTrue(ast.query("b").getResultSet().size() > 0);
        assertTrue(ast.query("c").getResultSet().size() > 0);
    }

    @Test
    public void should_find_all_substrings_overlapped() {
        SuffixTrie<String> ast = new SuffixTrie<>(Collections.singletonList("aaa"));

        assertTrue(ast.query("aaa").getResultSet().size() > 0);
        assertTrue(ast.query("aa").getResultSet().size() > 0);
        assertTrue(ast.query("a").getResultSet().size() > 0);
    }

    @Test
    public void should_return_empty_result_for_unknown_substring() {
        SuffixTrie<String> ast = new SuffixTrie<>(Collections.singletonList("abc"));
        assertTrue(ast.query("d").getResultSet().isEmpty());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void should_return_unmodifiable_result() {
        SuffixTrie<String> ast = new SuffixTrie<>(Collections.singletonList("abc"));
        ast.query("d").add("a", 0, 1);
    }

}