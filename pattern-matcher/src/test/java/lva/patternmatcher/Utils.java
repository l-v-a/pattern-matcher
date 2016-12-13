package lva.patternmatcher;

import java.util.List;
import java.util.Map;

/**
 * @author vlitvinenko
 */
class Utils {
    private Utils() {}

    static int getMatchingIndex(MatchingResultSet<String> resultSet, String word, int from, int to) {
        Map<String, MatchingResultSet.MatchingEntries> res = resultSet.getResultSet();
        MatchingResultSet.MatchingEntries entries = res.get(word);

        if (entries != null) {
            List<MatchingResultSet.Matching> matchings = res.get(word).getMatchings();

            if (matchings != null) {
                for (int i = 0; i < matchings.size(); i++) {
                    MatchingResultSet.Matching matching = matchings.get(i);
                    if (matching.getFrom() == from && matching.getTo() == to) {
                        return i;
                    }
                }
            }
        }

        return -1;
    }
}

