package lva.patternmatcher;

import org.junit.Test;

import java.util.Optional;

import static lva.patternmatcher.MatchingResultSet.emptyResultSet;
import static lva.patternmatcher.Utils.getMatchingIndex;
import static org.junit.Assert.*;

/**
 * @author vlitvinenko
 */
public class MatchingResultSetTest {

    @Test
    public void should_add_matching_for_word() {
        MatchingResultSet<String> resultSet = new MatchingResultSet<String>()
            .add("a", 0, 1);

        assertEquals(1, resultSet.getResultSet().size());
        assertEquals(0, getMatchingIndex(resultSet, "a", 0, 1));
    }

    @Test
    public void should_add_many_matchings_for_word_in_order() {
        MatchingResultSet<String> resultSet = new MatchingResultSet<String>()
            .add("a", 0, 1)
            .add("a", 1, 2);

        assertEquals(1, resultSet.getResultSet().size());
        assertEquals(0, getMatchingIndex(resultSet, "a", 0, 1));
        assertEquals(1, getMatchingIndex(resultSet, "a", 1, 2));
    }


    @Test
    public void should_add_matching_for_many_words() {
        MatchingResultSet<String> resultSet = new MatchingResultSet<String>()
            .add("a", 0, 1)
            .add("b", 0, 1);

        assertEquals(2, resultSet.getResultSet().size());
        assertEquals(0, getMatchingIndex(resultSet, "a", 0, 1));
        assertEquals(0, getMatchingIndex(resultSet, "b", 0, 1));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void should_return_unmodifiable_result() {
        MatchingResultSet<String> resultSet = new MatchingResultSet<String>()
            .add("a", 0, 1);

        resultSet.getResultSet().remove("a");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void should_return_unmodifiable_matchings() {
        MatchingResultSet<String> resultSet = new MatchingResultSet<String>()
            .add("a", 0, 1);

        resultSet.getResultSet().get("a").getMatchings().remove(0);
    }

    @Test
    public void should_remove_all_entries_when_mapping_returns_null() {
        MatchingResultSet<String> resultSet = new MatchingResultSet<String>()
            .add("a", 0, 1)
            .add("b", 0, 1);

        MatchingResultSet<String> transformedResultSet = resultSet.transform((s, entries) -> {
            if ("a".equals(s)) return Optional.of(entries);
            return Optional.empty();
        });

        assertEquals(1, transformedResultSet.getResultSet().size());
        assertEquals(0, getMatchingIndex(transformedResultSet, "a", 0, 1));
    }

    @Test
    public void should_replace_all_entries_when_mapping_result() {
        MatchingResultSet<String> resultSet = new MatchingResultSet<>();
        resultSet.add("a", 0, 1);
        resultSet.add("b", 0, 1);

        MatchingResultSet<String> transformedResultSet = resultSet.transform((s, entries) ->
            Optional.of(new MatchingResultSet.MatchingEntries().add(10, 20))
        );

        assertEquals(2, transformedResultSet.getResultSet().size());
        assertEquals(0, getMatchingIndex(transformedResultSet, "a", 10, 20));
        assertEquals(0, getMatchingIndex(transformedResultSet, "b", 10, 20));
    }


    @Test
    public void should_not_change_source_resultset_while_filtering() {
        MatchingResultSet<String> resultSet = new MatchingResultSet<>();
        resultSet.add("a", 0, 1);
        resultSet.add("b", 0, 1);

        assertEquals(2, resultSet.getResultSet().size());
        assertEquals(0, getMatchingIndex(resultSet, "a", 0, 1));
        assertEquals(0, getMatchingIndex(resultSet, "b", 0, 1));

        MatchingResultSet<String> transformedResultSet = resultSet.transform((s, entries) -> {
            if ("a".equals(s)) return Optional.of(entries);
            return Optional.empty();
        });

        assertEquals(1, transformedResultSet.getResultSet().size());
        assertEquals(0, getMatchingIndex(transformedResultSet, "a", 0, 1));

        assertFalse(resultSet == transformedResultSet);

        assertEquals(1, transformedResultSet.getResultSet().size());
        assertEquals(0, getMatchingIndex(transformedResultSet, "a", 0, 1));

        assertEquals(2, resultSet.getResultSet().size());
        assertEquals(0, getMatchingIndex(resultSet, "a", 0, 1));
        assertEquals(0, getMatchingIndex(resultSet, "b", 0, 1));

    }

    @Test
    public void should_remove_all_entries_when_combine_returns_null() {
        MatchingResultSet<String> resultSet1 = new MatchingResultSet<String>()
            .add("a", 0, 1)
            .add("b", 0, 1);

        MatchingResultSet<String> resultSet2 = new MatchingResultSet<String>()
            .add("a", 0, 1)
            .add("b", 0, 1);


        MatchingResultSet<String> combinedResultSet = resultSet1.combine(resultSet2, (s, entriesLeft, entriesRight) -> {
            if ("a".equals(s)) return Optional.of(entriesLeft);
            return Optional.empty();
        });

        assertEquals(1, combinedResultSet.getResultSet().size());
        assertEquals(0, getMatchingIndex(combinedResultSet, "a", 0, 1));
    }


    @Test
    public void should_replace_all_entries_with_combine_results() {
        MatchingResultSet<String> resultSet1 = new MatchingResultSet<String>()
            .add("a", 0, 1)
            .add("b", 0, 1);

        MatchingResultSet<String> resultSet2 = new MatchingResultSet<String>()
            .add("a", 0, 1)
            .add("b", 0, 1);


        MatchingResultSet<String> combinedResultSet = resultSet1.combine(resultSet2, (s, entriesLeft, entriesRight) ->
            Optional.of(new MatchingResultSet.MatchingEntries().add(10, 20))
        );

        assertEquals(2, combinedResultSet.getResultSet().size());
        assertEquals(0, getMatchingIndex(combinedResultSet, "a", 10, 20));
        assertEquals(0, getMatchingIndex(combinedResultSet, "b", 10, 20));
    }

    @Test
    public void should_not_change_source_resultset_while_combining() {
        MatchingResultSet<String> resultSet1 = new MatchingResultSet<String>()
            .add("a", 0, 1)
            .add("b", 0, 1);

        MatchingResultSet<String> resultSet2 = new MatchingResultSet<String>()
            .add("a", 0, 1)
            .add("b", 0, 1);

        assertEquals(2, resultSet1.getResultSet().size());
        assertEquals(0, getMatchingIndex(resultSet1, "a", 0, 1));
        assertEquals(0, getMatchingIndex(resultSet1, "b", 0, 1));

        assertEquals(2, resultSet2.getResultSet().size());
        assertEquals(0, getMatchingIndex(resultSet2, "a", 0, 1));
        assertEquals(0, getMatchingIndex(resultSet2, "b", 0, 1));

        MatchingResultSet<String> combinedResultSet = resultSet1.combine(resultSet2, (s, entriesLeft, entriesRight) ->
            Optional.of(new MatchingResultSet.MatchingEntries().add(10, 20))
        );

        assertFalse(combinedResultSet == resultSet1);
        assertFalse(combinedResultSet == resultSet2);

        assertEquals(2, combinedResultSet.getResultSet().size());
        assertEquals(0, getMatchingIndex(combinedResultSet, "a", 10, 20));
        assertEquals(0, getMatchingIndex(combinedResultSet, "b", 10, 20));

        assertEquals(2, resultSet1.getResultSet().size());
        assertEquals(0, getMatchingIndex(resultSet1, "a", 0, 1));
        assertEquals(0, getMatchingIndex(resultSet1, "b", 0, 1));

        assertEquals(2, resultSet2.getResultSet().size());
        assertEquals(0, getMatchingIndex(resultSet2, "a", 0, 1));
        assertEquals(0, getMatchingIndex(resultSet2, "b", 0, 1));

    }


    @Test
    public void should_return_left_part() {
        MatchingResultSet<String> resultSet = new MatchingResultSet<String>()
            .add("a", 1, 4)
            .add("a", 2, 10);

        MatchingResultSet<String> expected = new MatchingResultSet<String>()
            .add("a", 1, 2)
            .add("a", 2, 3);

        assertEquals(expected, resultSet.splitLeft(1));
    }

    @Test
    public void should_return_empty_left_part_if_out_of_bounds() {
        MatchingResultSet<String> resultSet = new MatchingResultSet<String>()
            .add("a", 1, 4)
            .add("a", 2, 10);

        assertEquals(emptyResultSet(), resultSet.splitLeft(-1));
    }

    @Test
    public void should_return_right_part() {
        MatchingResultSet<String> resultSet = new MatchingResultSet<String>()
            .add("a", 1, 4)
            .add("a", 2, 10);

        MatchingResultSet<String> expected = new MatchingResultSet<String>()
            .add("a", 2, 4)
            .add("a", 3, 10);

        assertEquals(expected, resultSet.splitRight(1));
    }

    @Test
    public void should_return_empty_right_part_if_out_of_bounds() {
        MatchingResultSet<String> resultSet = new MatchingResultSet<String>()
            .add("a", 1, 4)
            .add("a", 2, 10);

        assertEquals(emptyResultSet(), resultSet.splitRight(10));
    }

    @Test
    public void should_shift_resultset() {
        MatchingResultSet<String> resultSet = new MatchingResultSet<String>()
            .add("a", 1, 4)
            .add("a", 2, 10);

        MatchingResultSet<String> expected = new MatchingResultSet<String>()
            .add("a", 2, 4)
            .add("a", 3, 5);

        assertEquals(expected, resultSet.shift(1, 2));
    }

}