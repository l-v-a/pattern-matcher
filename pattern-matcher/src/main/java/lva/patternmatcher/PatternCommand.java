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

        return r.filter((word, entries) ->
            entries.getFirstMatching()
                .filter(matching -> matching.getFrom() == 0)
                .map(m -> new MatchingEntries().add(m))
        );
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

        return r.filter((word, entries) ->
            entries.getFirstMatching()
                .map(matching -> new MatchingEntries().add(matching))
        );
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

        return l.combine(r, (word, entriesLeft, entriesRight) ->
            entriesLeft.getLastMatching()
                .flatMap(entriesRight::findNearestMatching)
                .map(nearestMatching -> new MatchingEntries(entriesLeft).add(nearestMatching))
        );
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

        return l.combine(r, (word, entriesLeft, entriesRight) ->
            entriesLeft.getLastMatching()
                .flatMap(matchingLeft ->
                    entriesRight.findNearestMatching(matchingLeft)
                        .filter(matchingRight -> isValid(word, matchingLeft, matchingRight))
                        .map(matchingRight -> new MatchingEntries(entriesLeft).add(matchingRight))
            )
        );

    }

    private static <T extends CharSequence & Comparable<? super T>> boolean isValid(
        T word, @NonNull Matching matchingLeft, @NonNull Matching matchingRight) {
        // check pattern
        int from = matchingLeft.getTo();
        int to = matchingRight.getFrom();
        boolean isValid = true;

        for (int i = from; i < to && isValid; i++) {
            isValid = Character.isLowerCase(word.charAt(i));
        }

        return isValid;
    }
}