package lva.patternmatcher;

/**
 * Represents pattern tokenizer interface.
 * Used for pattern tokenization into commands, that will be executed by {@link lva.patternmatcher.PatternMatcher }.
 *
 * @author vlitvinenko
 */
interface PatternCommandTokenizer {

    /**
     * Provides interface for Command - unit of tokenization
     */
    interface Command {
        /**
         * Returns a part of tokenized pattern that this command is responsible to process.
         * @return sub-pattern
         */
        CharSequence getPattern();

        /**
         * Performs some operation on passed result sets.
         *
         * @param l - first argument to operation
         * @param r - second argument to operation
         * @return new result set
         */
        <T extends CharSequence & Comparable<? super T>> MatchingResultSet<T> execute(
            MatchingResultSet<T> l, MatchingResultSet<T> r);
    }

    /**
     * Resets internal state and prepares for {@code pattern} processing.
     * @param pattern - pattern to be processed
     */
    void restart(CharSequence pattern);

    /**
     * Returns next command if available.
     * @return - command or {@code null} if end of sequence
     */
    Command nextCommand();
}
