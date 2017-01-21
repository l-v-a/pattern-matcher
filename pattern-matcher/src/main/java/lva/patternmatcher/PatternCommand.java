package lva.patternmatcher;

import lombok.NonNull;
import lva.patternmatcher.MatchingResultSet.Matching;
import lva.patternmatcher.MatchingResultSet.MatchingEntries;

import java.util.*;

/**
 * Base Command implementation for all kinds of supported patterns.
 *
 * @author vlitvinenko
 */

abstract class AbstractPatternCommand implements PatternCommandTokenizer.Command {
    private final CharSequence pattern;

    AbstractPatternCommand(@NonNull CharSequence pattern) {
        this.pattern = pattern;
    }

    @Override
    public CharSequence getPattern() {
        return pattern;
    }
}

/**
 * Provides command implementation for pattern that matches begin of sequence
 * (e.g. 'ab' part of 'ab*' pattern).
 */
class BeginPatternCommand extends AbstractPatternCommand {
    BeginPatternCommand(CharSequence pattern) {
        super(pattern);
    }

    @Override
    public <T extends CharSequence & Comparable<? super T>> MatchingResultSet<T> execute(
        MatchingResultSet<T> l, MatchingResultSet<T> r) {

        return r.filter((word, entries) -> {
            Matching matching = entries.getFirstMatching();
            if (matching != null && matching.getFrom() == 0) {
                return new MatchingEntries()
                    .add(matching);
            }
            return null;
        });
    }
}

/**
 * Provides command implementation for pattern that matches any substring
 * (e.g. 'ab' part of '*ab*' pattern)
 */
class BeginAnyPatternCommand extends AbstractPatternCommand {
    BeginAnyPatternCommand(CharSequence pattern) {
        super(pattern);
    }

    @Override
    public <T extends CharSequence & Comparable<? super T>> MatchingResultSet<T> execute(
        MatchingResultSet<T> l, MatchingResultSet<T> r) {

        return r.filter((word, entries) -> {
            Matching matching = entries.getFirstMatching();
            if (matching != null) {
                return new MatchingEntries()
                    .add(matching);
            }
            return null;
        });
    }
}

/**
 * Provides command implementation for pattern that matches any occurrences of {@code pattern }
 * and restricts current result set {@code l} with it by nearest matching rule.
 * (e.g. 'b' part of 'a*b' pattern)
 */
class ExpressionAnyPatternCommand extends AbstractPatternCommand {
    ExpressionAnyPatternCommand(CharSequence pattern) {
        super(pattern);
    }

    @Override
    public <T extends CharSequence & Comparable<? super T>> MatchingResultSet<T> execute(
        MatchingResultSet<T> l, MatchingResultSet<T> r) {

        return l.combine(r, (word, entriesLeft, entriesRight) -> {
            Matching matchingLeft = entriesLeft.getLastMatching();
            Matching matchingRight = matchingLeft != null ? entriesRight.findNearestMatching(matchingLeft) : null;

            if (matchingRight != null) {
                return new MatchingEntries(entriesLeft)
                    .add(matchingRight);
            }

            return null;
        });
    }
}

/**
 * Provides command implementation for pattern that matches any occurrences of {@code pattern }
 * and restricts current result set {@code l} with it by nearest capital symbol matching rule.
 * (e.g. 'B' part of 'AB' pattern)
 */
class ExpressionStrictPatternCommand extends AbstractPatternCommand {
    ExpressionStrictPatternCommand(CharSequence pattern) {
        super(pattern);
    }

    @Override
    public <T extends CharSequence & Comparable<? super T>> MatchingResultSet<T> execute(
        MatchingResultSet<T> l, MatchingResultSet<T> r) {

        return l.combine(r, (word, entriesLeft, entriesRight) -> {
            Matching matchingLeft = entriesLeft.getLastMatching();
            Matching matchingRight = matchingLeft != null ? entriesRight.findNearestMatching(matchingLeft) : null;

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

            return null;
        });
    }
}