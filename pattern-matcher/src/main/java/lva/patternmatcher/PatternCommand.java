package lva.patternmatcher;

import lva.patternmatcher.MatchingResultSet.Matching;
import lva.patternmatcher.MatchingResultSet.MatchingEntries;

import java.util.List;
import java.util.Objects;

/**
 * @author vlitvinenko
 */

abstract class PatternCommandAbstract implements PatternCommandTokenizer.Command {
    private final CharSequence pattern;
    PatternCommandAbstract(CharSequence pattern) {
        this.pattern = Objects.requireNonNull(pattern);
    }

    @Override
    public CharSequence getPattern() {
        return pattern ;
    }

}

class BeginPatternCommand extends PatternCommandAbstract {
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

class BeginAnyPatternCommand extends PatternCommandAbstract {
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

class ExpressionAnyPatternCommand extends PatternCommandAbstract {
    ExpressionAnyPatternCommand(CharSequence pattern) {
        super(pattern);
    }

    @Override
    public <T extends CharSequence & Comparable<? super T>> MatchingResultSet<T> execute(
        MatchingResultSet<T> l, MatchingResultSet<T> r) {

        return null;
    }
}

class ExpressionStrictPatternCommand extends PatternCommandAbstract {
    ExpressionStrictPatternCommand(CharSequence pattern) {
        super(pattern);
    }

    @Override
    public <T extends CharSequence & Comparable<? super T>> MatchingResultSet<T> execute(
        MatchingResultSet<T> l, MatchingResultSet<T> r) {

        return null;
    }
}