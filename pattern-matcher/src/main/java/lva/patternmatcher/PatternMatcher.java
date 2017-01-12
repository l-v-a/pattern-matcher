package lva.patternmatcher;

/**
 * @author vlitvinenko
 */
public class PatternMatcher <T extends CharSequence & Comparable<? super T>> {
    private final PatternCommandTokenizer CommandTokenizer;
    private final Searchable<T> searchable;
    private  MatchingResultSet<T> resultSet = new MatchingResultSet<T>();

    public PatternMatcher(Iterable<T> words) {
        this(new PatternCommandTokenizerImpl(), new SuffixTrie<>(words));
    }

    PatternMatcher(PatternCommandTokenizer commandTokenizer, Searchable<T> searchable) {
        this.CommandTokenizer = commandTokenizer;
        this.searchable = searchable;
    }

    public MatchingResultSet<T> match(CharSequence pattern) {
        MatchingResultSet<T> resultSet = new MatchingResultSet<>();

        CommandTokenizer.restart(pattern);
        PatternCommandTokenizer.Command command = CommandTokenizer.nextCommand();

        while (command != null) {
            resultSet = command.execute(resultSet, searchable.search(command.getPattern()));
            command = CommandTokenizer.nextCommand();
        }

        return resultSet;
    }

}
