package lva.patternmatcher;

/**
 * @author vlitvinenko
 */
public class PatternMatcher <T extends CharSequence & Comparable<? super T>> {
    private final PatternCommandTokenizer commandTokenizer;
    private final Searchable<T> searchable;

    public PatternMatcher(Iterable<T> words) {
        this(new PatternCommandTokenizerImpl(), new SuffixTrie<>(words));
    }

    PatternMatcher(PatternCommandTokenizer commandTokenizer, Searchable<T> searchable) {
        this.commandTokenizer = commandTokenizer;
        this.searchable = searchable;
    }

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
