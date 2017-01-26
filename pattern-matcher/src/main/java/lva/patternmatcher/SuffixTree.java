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
            return new Node<>(sequence.subSequence(0, len), new HashMap<>(), matchings.getLeft(len));
        }

        Node<T> newRight(int len) {
            return new Node<>(sequence.subSequence(len, sequence.length()), new HashMap<>(), matchings.getRight(len));
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
        CharSequence sequence = new StringBuilder(word) // to avoid Object.toString() calling
            .append(TERMINAL_SYMBOL).toString();

        for (int i = 0; i < sequence.length(); i++) {

            Node<T> node = rootNode;
            Node<T> prevNode = node;
            int suffixIdx = i;

            while (suffixIdx < sequence.length() &&
                    (node = node.children.get(sequence.charAt(suffixIdx))) != null) {

                int nodeSequenceIdx = 0;
                int suffixStartIdx = suffixIdx;

                // test for matching with node
                while (nodeSequenceIdx < node.sequence.length() &&
                    node.sequence.charAt(nodeSequenceIdx) == sequence.charAt(suffixIdx)) {

                    suffixIdx++;
                    nodeSequenceIdx++;
                }

                if (nodeSequenceIdx == node.sequence.length()) {
                    // full matching
                    node.matchings.add(word, suffixStartIdx, suffixIdx);
                } else {
                    // partial matching
                    // split nodes
                    Node<T> nodeLeft = node.newLeft(nodeSequenceIdx);
                    Node<T> nodeRight = node.newRight(nodeSequenceIdx);

                    nodeLeft.children.put(nodeRight.sequence.charAt(0), nodeRight);
                    nodeRight.children.putAll(node.children);

                    prevNode.children.put(nodeLeft.sequence.charAt(0), nodeLeft);

                    Node<T> newNode = new Node<>(sequence.subSequence(suffixIdx, sequence.length()));
                    nodeLeft.children.put(newNode.sequence.charAt(0), newNode);

                    nodeLeft.matchings.add(word, suffixStartIdx, suffixIdx);
                    newNode.matchings.add(word, suffixIdx, sequence.length());

                    suffixIdx = sequence.length();
                }

                prevNode = node;
            }

            if (suffixIdx < sequence.length()) {
                Objects.requireNonNull(prevNode);

                // append new node for tail of sequence
                Node<T> newNode = new Node<>(sequence.subSequence(suffixIdx, sequence.length()));
                newNode.matchings.add(word, suffixIdx, sequence.length());
                prevNode.children.put(newNode.sequence.charAt(0), newNode);
            }
        }

    }

    private static <T extends CharSequence & Comparable<? super T>> void dump(Node<T> node, String offset) {
        System.out.printf("%s'%s' : %s %n", offset, node.sequence, node.matchings);
        node.children.forEach((k, v) -> dump(v, offset + "\t"));
    }

    void dump() {
        dump(rootNode, "");
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
            int offset = pattern.length() - sequenceIdx;
            int len = sequenceIdx;
            return node.matchings.filter((word, entries) ->
                Optional.of(entries.transform(matching ->
                    new Matching(matching.getFrom() - offset, matching.getFrom() + len)
                ))
            );
        }

        return MatchingResultSet.emptyResultSet();
    }
}
