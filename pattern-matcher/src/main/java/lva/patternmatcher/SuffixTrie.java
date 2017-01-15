package lva.patternmatcher;

import lombok.NonNull;

import java.util.*;
import java.util.stream.Stream;

/**
 * @author vlitvinenko
 */
class SuffixTrie<T extends CharSequence & Comparable<? super T>> implements Searchable<T> {

    private static class Node<T extends CharSequence & Comparable<? super T>> {
        final Map<Character, Node<T>> children = new HashMap<>();
        final MatchingResultSet<T> matchings = new MatchingResultSet<>();
    }

    private final Node<T> rootNode = new Node<>();

    SuffixTrie(@NonNull Stream<T> words) {
        words.filter(Objects::nonNull)
            .forEach(this::addWord);
    }

    // TODO: add trailing blank support
    private void addWord(@NonNull T word) {
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
