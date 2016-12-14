package lva.patternmatcher;

import java.util.*;


/**
 * @author vlitvinenko
 */
class MatchingResultSet<T extends CharSequence & Comparable<? super T>> {
    private static final MatchingResultSet EMPTY = unmodifiable(new MatchingResultSet());

    static class Matching {
        private final int from;
        private final int to;

        private Matching(int from, int to) {
            this.from = from;
            this.to = to;
        }

        int getFrom() {
            return from;
        }

        int getTo() {
            return to;
        }

        @Override
        public String toString() {
            return String.format("[%d , %d)", from, to);
        }
    }


    static class MatchingEntries {
        private final List<Matching> matchings;

        private MatchingEntries() {
            matchings = new ArrayList<>();
        }

        private MatchingEntries(MatchingEntries other) {
            matchings = new ArrayList<>(other.matchings);
        }

        List<Matching> getMatchings() {
            return Collections.unmodifiableList(matchings);
        }

        private void add(int left, int right) {
            add(new Matching(left, right));
        }

        private void add(Matching matching) {
            matchings.add(matching);
        }

        @Override
        public String toString() {
            return matchings.toString();
        }

    }

    private final Map<T, MatchingEntries> resultSet;

    MatchingResultSet() {
        resultSet = new TreeMap<>();
    }

    private MatchingResultSet(Map<T, MatchingEntries> matchingEntriesMap) {
        this.resultSet = matchingEntriesMap;
    }

    Map<T, MatchingEntries> getResultSet() {
        return Collections.unmodifiableMap(resultSet);
    }

    void add(T word, int from, int to) {
        resultSet.computeIfAbsent(word, (i) -> new MatchingEntries()).add(from, to);
    }

    @SuppressWarnings("unchecked")
    static <T extends CharSequence & Comparable<? super T>> MatchingResultSet<T> emptyResultSet() {
        return (MatchingResultSet<T>) EMPTY;
    }

    static <T extends CharSequence & Comparable<? super T>> MatchingResultSet<T> unmodifiable(MatchingResultSet<T> m) {
        return new MatchingResultSet<>(Collections.unmodifiableMap(m.resultSet));
    }
}