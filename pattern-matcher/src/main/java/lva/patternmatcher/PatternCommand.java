package lva.patternmatcher;

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
    public <T extends CharSequence & Comparable<? super T>> MatchingResultSet<T> execute(MatchingResultSet<T> l, MatchingResultSet<T> r) {
        return null;
    }
}

class BeginAnyPatternCommand extends PatternCommandAbstract {
    BeginAnyPatternCommand(CharSequence pattern) {
        super(pattern);
    }

    @Override
    public <T extends CharSequence & Comparable<? super T>> MatchingResultSet<T> execute(MatchingResultSet<T> l, MatchingResultSet<T> r) {
        return null;
    }
}

class ExpressionAnyPatternCommand extends PatternCommandAbstract {
    ExpressionAnyPatternCommand(CharSequence pattern) {
        super(pattern);
    }

    @Override
    public <T extends CharSequence & Comparable<? super T>> MatchingResultSet<T> execute(MatchingResultSet<T> l, MatchingResultSet<T> r) {
        return null;
    }
}

class ExpressionStrictPatternCommand extends PatternCommandAbstract {
    ExpressionStrictPatternCommand(CharSequence pattern) {
        super(pattern);
    }

    @Override
    public <T extends CharSequence & Comparable<? super T>> MatchingResultSet<T> execute(MatchingResultSet<T> l, MatchingResultSet<T> r) {
        return null;
    }
}