package lva.patternmatcher;

import java.util.*;
import java.util.stream.Stream;

/**
 * @author vlitvinenko
 */
class SuffixTrie<T extends CharSequence & Comparable<? super T>> {

    private static class Node<T extends CharSequence & Comparable<? super T>> {
        final Map<Character, Node<T>> children = new HashMap<>();
        final MatchingResultSet<T> matchings = new MatchingResultSet<>();
    }

    private final Node<T> rootNode = new Node<>();

    // TODO> think about to use Stream<T>
    SuffixTrie(Iterable<T> words) {
        Objects.requireNonNull(words);
        words.forEach(word -> {
            if (word != null) {
                addWord(word);
            }
        });
    }

    // TODO: add trailing blank support
    private void addWord(T word) {
        Objects.requireNonNull(word);
        // add suffixes
        for (int i = 0; i < word.length(); i++) {

            Node<T> node = rootNode;
            Node<T> prevNode = null;
            int suffixIndex = i;

            // find longest path
            while (suffixIndex < word.length() && node != null) {
                prevNode = node;
                node = node.children.get(word.charAt(suffixIndex));

                if (node != null) {
                    node.matchings.add(word, i, suffixIndex + 1);
                    suffixIndex++;
                }

            }

            // add remaining nodes
            Objects.requireNonNull(prevNode);
            for (; suffixIndex < word.length(); suffixIndex++) {
                node = new Node<>();

                node.matchings.add(word, i, suffixIndex + 1);
                prevNode.children.put(word.charAt(suffixIndex), node);

                prevNode = node;
            }
        }
    }

    MatchingResultSet<T> query(T pattern) {
        Objects.requireNonNull(pattern);

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
