package lva.patternmatcher;

import java.util.stream.Stream;

/**
 * Provides AST-based pattern matcher.
 *
 * @author vlitvinenko
 */
public class PatternMatcher <T extends CharSequence & Comparable<? super T>> {
    private final PatternCommandTokenizer commandTokenizer;
    private final Searchable<T> searchable;

    /**
     * Constructs matcher from stream of words to be searched.
     * @param words - stream of words
     */
    public PatternMatcher(Stream<T> words) {
        this(new PatternCommandTokenizerImpl(), new SuffixTrie<>(words));
    }

    PatternMatcher(PatternCommandTokenizer commandTokenizer, Searchable<T> searchable) {
        this.commandTokenizer = commandTokenizer;
        this.searchable = searchable;
    }

    /**
     * Performs matching within loaded words against pattern.<br/>
     * Available patterns are:
     * <ul>
     *     <li>Capital letters, i.e. ABC - matches CamelCase letters.
     *     For example, ABC matches with <b>A</b>xxx<b>B</b>xxxx<b>C</b></li>
     *     <li>Lower case letters makes matching more strictly, e.g. AbC matches with <b>Ab</b>xxx<b>C</b>yyy,
     *     and abcD with <b>abcD</b>xyz</li>
     *     <li>Asterisk <i>*</i> matches with any sequence, e.g. *a*b matches with xxxx<b>a</b>xxxx<b>b</b>yyy and <b>ab</b></li>
     *     <li>Blank at the end of pattern matches with the end of word, e.g. '*ab ' matches with xxxx<b>ab</b>,
     *     but not with xxxxaby</li>
     * </ul>
     * @param pattern - pattern to match against
     * @return matching result set.
     */
    public MatchingResultSet<T> match(CharSequence pattern) {
        MatchingResultSet<T> resultSet = new MatchingResultSet<>();

        commandTokenizer.restart(pattern);
        PatternCommandTokenizer.Command command = commandTokenizer.nextCommand();

        while (command != null) {
            resultSet = command.execute(resultSet, searchable.search(command.getPattern()));
            command = commandTokenizer.nextCommand();
        }

        return resultSet;
    }

}
