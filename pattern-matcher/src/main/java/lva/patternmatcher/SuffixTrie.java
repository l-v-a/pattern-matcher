package lva.patternmatcher;

import lombok.NonNull;

import java.util.*;
import java.util.stream.Stream;

/**
 * @author vlitvinenko
 */
class SuffixTrie<T extends CharSequence & Comparable<? super T>> implements Searchable<T> {
    private static final String TERMINAL_SYMBOL = " ";

    private static class Node<T extends CharSequence & Comparable<? super T>> {
        final Map<Character, Node<T>> children = new HashMap<>();
        final MatchingResultSet<T> matchings = new MatchingResultSet<>();
    }

    private final Node<T> rootNode = new Node<>();

    SuffixTrie(@NonNull Stream<T> words) {
        words.filter(Objects::nonNull)
            .forEach(this::addWord);
    }

    private void addWord(@NonNull T word) {
        // add suffixes
        CharSequence seq = new StringBuilder(word) // to avoid Object.toString() calling
            .append(TERMINAL_SYMBOL).toString();
        for (int i = 0; i < seq.length(); i++) {

            Node<T> node = rootNode;
            Node<T> prevNode = null;
            int suffixIndex = i;

            // find longest path
            while (suffixIndex < seq.length() && node != null) {
                prevNode = node;
                node = node.children.get(seq.charAt(suffixIndex));

                if (node != null) {
                    node.matchings.add(word, i, suffixIndex + 1);
                    suffixIndex++;
                }

            }

            // add remaining nodes
            Objects.requireNonNull(prevNode);
            for (; suffixIndex < seq.length(); suffixIndex++) {
                node = new Node<>();

                node.matchings.add(word, i, suffixIndex + 1);
                prevNode.children.put(seq.charAt(suffixIndex), node);

                prevNode = node;
            }
        }
    }


    @Override
    public MatchingResultSet<T> search(@NonNull CharSequence pattern) {
        Node<T> node = rootNode;
        for (int i = 0; i < pattern.length() && node != null; i++) {
            node = node.children.get(pattern.charAt(i));
        }

        if (node == null) {
            return MatchingResultSet.emptyResultSet();
        }

        return MatchingResultSet.unmodifiable(node.matchings);
    }
}
