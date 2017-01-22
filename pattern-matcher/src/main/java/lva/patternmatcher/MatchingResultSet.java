package lva.patternmatcher;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;

import java.util.*;
import java.util.function.BiFunction;


/**
 * Represents matching results for some sequence, that includes matched words with list of occurrence indexes in each.
 *
 * @author vlitvinenko
 */
@EqualsAndHashCode
@ToString
public class MatchingResultSet<T extends CharSequence & Comparable<? super T>> {
    private static final MatchingResultSet EMPTY = unmodifiable(new MatchingResultSet());

    /**
     * Occurrence indexes within sequence.
     */
    @Data
    public static class Matching {
        /**
         * beginning index, inclusive
         */
        private final int from;
        /**
         * ending index, exclusive
         */
        private final int to;

        @Override
        public String toString() {
            return String.format("[%d , %d)", from, to);
        }
    }


    /**
     * Provides list of matchings (occurrence indexes)
     */
    @EqualsAndHashCode
    @ToString
    public static class MatchingEntries {
        private final List<Matching> matchings;

        MatchingEntries() {
            matchings = new ArrayList<>();
        }

        MatchingEntries(MatchingEntries other) {
            matchings = new ArrayList<>(other.matchings);
        }

        /**
         * @return list of matchings.
         */
        public List<Matching> getMatchings() {
            return Collections.unmodifiableList(matchings);
        }

        MatchingEntries add(int left, int right) {
            return add(new Matching(left, right));
        }

        MatchingEntries add(@NonNull Matching matching) {
            Matching lastMatching  = getLastMatching().orElse(null);
            if (lastMatching == null || lastMatching.getFrom() < matching.getFrom()) {
                matchings.add(matching);
            }
            return this;
        }

        Optional<Matching> getFirstMatching() {
            return Optional.ofNullable(matchings.isEmpty() ? null : matchings.get(0));
        }

        Optional<Matching> getLastMatching() {
            return Optional.ofNullable(matchings.isEmpty() ? null : matchings.get(matchings.size() - 1));
        }

        Optional<Matching> findNearestMatching(@NonNull Matching matching) {
            // search for nearest entry (lists are sorted)
            Matching searchMatching = new Matching(matching.getTo(), matching.getTo());
            int idx = Collections.binarySearch(matchings, searchMatching, (m1, m2) ->
                Integer.compare(m1.getFrom(), m2.getFrom())
            );

            idx = idx < 0 ? -idx - 1 : idx;
            return Optional.ofNullable(idx < matchings.size() ? matchings.get(idx) : null);
        }

    }

    private final Map<T, MatchingEntries> resultSet;

    MatchingResultSet() {
        resultSet = new TreeMap<>();
    }

    private MatchingResultSet(@NonNull Map<T, MatchingEntries> matchingEntriesMap) {
        this.resultSet = matchingEntriesMap;
    }

    /**
     * Returns matching result set, that contains a map of matched words to their lists of lower and upper bounds intervals,
     * that represents occurrence indexes within corresponding word.
     *
     * @return matching result set
     */
    public Map<T, MatchingEntries> getResultSet() {
        return Collections.unmodifiableMap(resultSet);
    }

    MatchingResultSet<T> add(T word, int from, int to) {
        resultSet.computeIfAbsent(word, (i) -> new MatchingEntries()).add(from, to);
        return this;
    }

    MatchingResultSet<T> filter(BiFunction<? super T, MatchingEntries, Optional<MatchingEntries>> filter) {
        MatchingResultSet<T> result = new MatchingResultSet<>();
        resultSet.forEach((word, entries) -> {
            filter.apply(word, entries).ifPresent((value) -> {
                result.resultSet.put(word, value);
            });
        });
        return result;
    }


    @FunctionalInterface
    interface CombineFunction <T> {
        Optional<MatchingEntries> apply(T t, MatchingEntries entriesLeft, MatchingEntries entriesRight);
    }

    MatchingResultSet<T> combine(MatchingResultSet<T> other, CombineFunction<? super T> combineFunction) {
        MatchingResultSet<T> result = new MatchingResultSet<>();
        resultSet.forEach((word, entries) -> {
            Optional.ofNullable(other.resultSet.get(word)).ifPresent((entriesOther) -> {
                combineFunction.apply(word, entries, entriesOther).ifPresent((combinedEntries) -> {
                    result.resultSet.put(word, combinedEntries);
                });
            });
        });
        return result;
    }


    @SuppressWarnings("unchecked")
    static <T extends CharSequence & Comparable<? super T>> MatchingResultSet<T> emptyResultSet() {
        return (MatchingResultSet<T>) EMPTY;
    }

    static <T extends CharSequence & Comparable<? super T>> MatchingResultSet<T> unmodifiable(MatchingResultSet<T> m) {
        return new MatchingResultSet<>(Collections.unmodifiableMap(m.resultSet));
    }
}
