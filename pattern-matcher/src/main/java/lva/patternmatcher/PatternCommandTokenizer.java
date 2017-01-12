package lva.patternmatcher;

/**
 * @author vlitvinenko
 */
interface PatternCommandTokenizer {

    interface Command {
        CharSequence getPattern();
        <T extends CharSequence & Comparable<? super T>> MatchingResultSet<T> execute(MatchingResultSet<T> l, MatchingResultSet<T> r);
    }

    void restart(CharSequence pattern);
    Command nextCommand();
}
