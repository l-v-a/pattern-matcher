package lva.patternmatcher;

/**
 * @author vlitvinenko
 */

interface Searchable<T extends CharSequence & Comparable<? super T>> {
    MatchingResultSet<T> search(CharSequence pattern);
}

