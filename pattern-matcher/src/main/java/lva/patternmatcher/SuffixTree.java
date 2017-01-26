package lva.patternmatcher;

import lombok.NonNull;
import lva.patternmatcher.MatchingResultSet.Matching;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Represents annotated suffix tree (AST).
 * Used to fast searching sub-sequence occurrence in provided words.
 *
 * @author vlitvinenko
 */
class SuffixTree<T extends CharSequence & Comparable<? super T>> implements Searchable<T> {
    private static final String TERMINAL_SYMBOL = " ";

    private static class Node<T extends CharSequence & Comparable<? super T>> {
        final Map<Character, Node<T>> children;
        final MatchingResultSet<T> matchings;
        final CharSequence sequence;

        Node(CharSequence sequence) {
            this(sequence, new HashMap<>(), new MatchingResultSet<>());
        }

        Node(CharSequence sequence, Map<Character, Node<T>> children, MatchingResultSet<T> matchings) {
            this.sequence = sequence;
            this.children = children;
            this.matchings = matchings;
        }

        Node<T> newLeft(int len) {
            return new Node<>(sequence.subSequence(0, len), new HashMap<>(), matchings.splitLeft(len));
        }

        Node<T> newRight(int len) {
            return new Node<>(sequence.subSequence(len, sequence.length()), new HashMap<>(), matchings.splitRight(len));
        }

    }

    private final Node<T> rootNode = new Node<>("");

    /**
     * Constructs AST from stream of {@code words}.
     *
     * @param words - stream of words to be added
     */
    SuffixTree(@NonNull Stream<T> words) {
        words.filter(Objects::nonNull)
            .forEach(this::addWord);
    }

    private void addWord(@NonNull T word) {
        // add suffixes
        CharSequence suffix = new StringBuilder(word) // to avoid Object.toString() calling
            .append(TERMINAL_SYMBOL).toString();

        for (int i = 0; i < suffix.length(); i++) {

            Node<T> node = rootNode;
            Node<T> prevNode = node;
            int suffixIdx = i;

            while (suffixIdx < suffix.length() &&
                    (node = node.children.get(suffix.charAt(suffixIdx))) != null) {

                int sequenceIdx = 0;
                int suffixStartIdx = suffixIdx;

                // test for matching with node
                while (sequenceIdx < node.sequence.length() &&
                    node.sequence.charAt(sequenceIdx) == suffix.charAt(suffixIdx)) {

                    suffixIdx++;
                    sequenceIdx++;
                }

                if (sequenceIdx == node.sequence.length()) {
                    // full matching with node sequence
                    node.matchings.add(word, suffixStartIdx, suffixIdx);
                } else {
                    // partial matching
                    // split nodes: leftNode contains full matching, rightNode have not matchings with suffix
                    // and newNode matches with suffix tail
                    Node<T> nodeLeft = node.newLeft(sequenceIdx);
                    nodeLeft.matchings.add(word, suffixStartIdx, suffixIdx);

                    Node<T> nodeRight = node.newRight(sequenceIdx);

                    Node<T> newNode = new Node<>(suffix.subSequence(suffixIdx, suffix.length()));
                    newNode.matchings.add(word, suffixIdx, suffix.length());

                    nodeLeft.children.put(nodeRight.sequence.charAt(0), nodeRight);
                    nodeRight.children.putAll(node.children);
                    prevNode.children.put(nodeLeft.sequence.charAt(0), nodeLeft);
                    nodeLeft.children.put(newNode.sequence.charAt(0), newNode);

                    suffixIdx = suffix.length();
                }

                prevNode = node;
            }

            if (suffixIdx < suffix.length()) {
                Objects.requireNonNull(prevNode);

                // append new node for tail of suffix
                Node<T> newNode = new Node<>(suffix.subSequence(suffixIdx, suffix.length()));
                newNode.matchings.add(word, suffixIdx, suffix.length());
                prevNode.children.put(newNode.sequence.charAt(0), newNode);
            }
        }

    }

    /**
     * Searches for all occurrences of passed substring {@code pattern}.
     *
     * @param pattern - substring to be searched
     * @return mathcig result set that contains all occurrences of substring within provided words
     */
    @Override
    public MatchingResultSet<T> search(@NonNull CharSequence pattern) {
        pattern = pattern.length() == 0 ? TERMINAL_SYMBOL : pattern;

        Node<T> node = rootNode;
        int patternIdx = 0;
        int sequenceIdx = 0;

        while (patternIdx < pattern.length() &&
                (node = node.children.get(pattern.charAt(patternIdx))) != null) {

            sequenceIdx = 0;
            while (sequenceIdx < node.sequence.length() && patternIdx < pattern.length() &&
                    node.sequence.charAt(sequenceIdx) == pattern.charAt(patternIdx)) {

                patternIdx++;
                sequenceIdx++;
            }

            if (sequenceIdx < node.sequence.length()) {
                break;
            }
        }

        if (node != null && patternIdx == pattern.length()) {
            // matches
            return node.matchings.shift(sequenceIdx - pattern.length(), pattern.length());
        }

        return MatchingResultSet.emptyResultSet();
    }
}
