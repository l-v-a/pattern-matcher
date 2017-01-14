package lva.patternmatcher;

import lva.patternmatcher.MatchingResultSet.Matching;
import lva.patternmatcher.MatchingResultSet.MatchingEntries;

import java.util.*;

/**
 * @author vlitvinenko
 */

abstract class AbstractPatternCommand implements PatternCommandTokenizer.Command {
    private final CharSequence pattern;
    AbstractPatternCommand(CharSequence pattern) {
        this.pattern = Objects.requireNonNull(pattern);
        if (this.pattern.length() == 0) {
            throw new IllegalArgumentException("Empty pattern for command");
        }
    }

    @Override
    public CharSequence getPattern() {
        return pattern ;
    }

}

class BeginPatternCommand extends AbstractPatternCommand {
    BeginPatternCommand(CharSequence pattern) {
        super(pattern);
    }

    @Override
    public <T extends CharSequence & Comparable<? super T>> MatchingResultSet<T> execute(
        MatchingResultSet<T> l, MatchingResultSet<T> r) {

        return r.filter((word, entries) -> {
            List<Matching> matchings = entries.getMatchings();
            if (!matchings.isEmpty() && matchings.get(0).getFrom() == 0) {
                return new MatchingEntries()
                    .add(matchings.get(0));
            }
            return null;
        });
    }
}

class BeginAnyPatternCommand extends AbstractPatternCommand {
    BeginAnyPatternCommand(CharSequence pattern) {
        super(pattern);
    }

    @Override
    public <T extends CharSequence & Comparable<? super T>> MatchingResultSet<T> execute(
        MatchingResultSet<T> l, MatchingResultSet<T> r) {

        return r.filter((word, entries) -> {
            List<Matching> matchings = entries.getMatchings();
            if (!matchings.isEmpty()) {
                return new MatchingEntries()
                    .add(matchings.get(0));
            }
            return null;
        });
    }
}

class ExpressionAnyPatternCommand extends AbstractPatternCommand {
    ExpressionAnyPatternCommand(CharSequence pattern) {
        super(pattern);
    }

    @Override
    public <T extends CharSequence & Comparable<? super T>> MatchingResultSet<T> execute(
        MatchingResultSet<T> l, MatchingResultSet<T> r) {

        return l.combine(r, (word, entriesLeft, entriesRight) -> {
            List<Matching> matchingsLeft = entriesLeft.getMatchings();

            if (!matchingsLeft.isEmpty()) {
                // TODO: refactor
                Matching matchingLeft = matchingsLeft.get(matchingsLeft.size() - 1);
                List<Matching> matchingsRight = entriesRight.getMatchings();
                // search for nearest entry (lists sorted)
                Matching searchMatching = new Matching(matchingLeft.getTo(), matchingLeft.getTo());
                int idx = Collections.binarySearch(matchingsRight, searchMatching, (m1, m2) ->
                    Integer.compare(m1.getFrom(), m2.getFrom())
                );

                idx = idx < 0 ? -idx - 1 : idx;
                Matching matching = idx < matchingsRight.size() ? matchingsRight.get(idx) : null;

                if (matching != null) {
                    return new MatchingEntries(entriesLeft)
                        .add(matching);
                }
            }
            return null;
        });
    }
}

class ExpressionStrictPatternCommand extends AbstractPatternCommand {
    ExpressionStrictPatternCommand(CharSequence pattern) {
        super(pattern);
    }

    @Override
    public <T extends CharSequence & Comparable<? super T>> MatchingResultSet<T> execute(
        MatchingResultSet<T> l, MatchingResultSet<T> r) {

        return l.combine(r, (word, entriesLeft, entriesRight) -> {
            List<Matching> matchingsLeft = entriesLeft.getMatchings();

            if (!matchingsLeft.isEmpty()) {
                Matching matchingLeft = matchingsLeft.get(matchingsLeft.size() - 1);
                List<Matching> matchingsRight = entriesRight.getMatchings();
                // search for nearest entry (lists sorted)
                Matching searchMatching = new Matching(matchingLeft.getTo(), matchingLeft.getTo());
                int idx = Collections.binarySearch(matchingsRight, searchMatching, (m1, m2) ->
                    Integer.compare(m1.getFrom(), m2.getFrom())
                );

                idx = idx < 0 ? -idx - 1 : idx;
                Matching matchingRight = idx < matchingsRight.size() ? matchingsRight.get(idx) : null;

                if (matchingRight != null) {
                    // check pattern
                    int from = matchingLeft.getTo();
                    int to = matchingRight.getFrom();
                    boolean isValid = true;

                    for (int i = from; i < to && isValid; i++) {
                        isValid = Character.isLowerCase(word.charAt(i));
                    }

                    if (isValid) {
                        return new MatchingEntries(entriesLeft)
                            .add(matchingRight);
                    }
                }
            }
            return null;
        });
    }
}