package lva.patternmatcher;

import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;

// TODO: think about to implement Iterable<Lexeme>
/**
 * @author vlitvinenko
 */
class Scanner {

    static class Lexeme {
        enum Type {
            CONCATENATION,  // TODO: think about to rename ANY, AND, COMBINE
            STRICT_CONCATENATION,
            LITERAL
        }
        private final Type type;
        private final CharSequence value;

        private Lexeme(Type type, CharSequence value) {
            this.type = type;
            this.value = value;
        }

        private Lexeme(Type type) {
            this(type, null);
        }

        Type getType() {
            return type;
        }

        // TODO: use String
        String getValue() {
            return value.toString();
        }

        @Override
        public String toString() {
            return value != null ? String.format("%s:%s", type.name(), value) :
                String.format("%s", type.name());
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) return true;
            if (o instanceof Lexeme) {
                Lexeme l = (Lexeme) o;
                return Objects.equals(type, l.type) && Objects.equals(value, l.value);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return Objects.hash(type, value);
        }

        static Lexeme concatenation() {
            return new Lexeme(Type.CONCATENATION);
        }

        static Lexeme strictConcatenation() {
            return new Lexeme(Type.STRICT_CONCATENATION);
        }

        static Lexeme literal(CharSequence value) {
            return new Lexeme(Type.LITERAL, value);
        }
    }

    private enum State {
        INITIAL,
        ASTERISK,
        SYMBOL,
        CAPITAL_SYMBOL,
        FINISHED
    }

    private enum CharClass {
        ASTERISK,
        CAPITAL_SYMBOL,
        SYMBOL,
        EOF
    }

    private final Queue<Lexeme> currentLexemes = new LinkedList<>();
    private final FiniteStateMachine<State, CharClass> fsm;

    private CharSequence sequence;
    private int currentCharIndex = 0;
    private int beginOfLexemeIndex = 0;

    Scanner() {
        this.fsm = new FiniteStateMachine.Builder<State, CharClass>()
            .setInitialState(State.INITIAL)
            .setFinishedState(State.FINISHED)
            .addTransition(State.INITIAL, State.CAPITAL_SYMBOL, CharClass.CAPITAL_SYMBOL, ((from, to, event) -> {
                beginOfLexemeIndex = currentCharIndex;
                currentCharIndex++;
            }))
            .addTransition(State.INITIAL, State.SYMBOL, CharClass.SYMBOL, ((from, to, event) -> {
                beginOfLexemeIndex = currentCharIndex;
                currentCharIndex++;
            }))
            .addTransition(State.INITIAL, State.ASTERISK, CharClass.ASTERISK, ((from, to, event) -> {
                beginOfLexemeIndex = currentCharIndex;
                currentCharIndex++;
            }))
            .addTransition(State.INITIAL, State.FINISHED, CharClass.EOF, ((from, to, event) -> {
                beginOfLexemeIndex = currentCharIndex;
                currentCharIndex++;
            }))
            .addTransition(State.ASTERISK, State.CAPITAL_SYMBOL, CharClass.CAPITAL_SYMBOL, ((from, to, event) -> {
                currentLexemes.add(Lexeme.concatenation());
                beginOfLexemeIndex = currentCharIndex;
                currentCharIndex++;
            }))
            .addTransition(State.ASTERISK, State.SYMBOL, CharClass.SYMBOL, ((from, to, event) -> {
                currentLexemes.add(Lexeme.concatenation());
                beginOfLexemeIndex = currentCharIndex;
                currentCharIndex++;
            }))
            .addTransition(State.ASTERISK, State.ASTERISK, CharClass.ASTERISK, ((from, to, event) -> {
                currentLexemes.add(Lexeme.concatenation());
                beginOfLexemeIndex = currentCharIndex;
                currentCharIndex++;
            }))
            .addTransition(State.ASTERISK, State.FINISHED, CharClass.EOF, ((from, to, event) -> {
                currentLexemes.add(Lexeme.concatenation());
                beginOfLexemeIndex = currentCharIndex;
            }))
            .addTransition(State.SYMBOL, State.CAPITAL_SYMBOL, CharClass.CAPITAL_SYMBOL, ((from, to, event) -> {
                currentCharIndex++;
            }))
            .addTransition(State.SYMBOL, State.SYMBOL, CharClass.SYMBOL, ((from, to, event) -> {
                currentCharIndex++;
            }))
            .addTransition(State.SYMBOL, State.ASTERISK, CharClass.ASTERISK, ((from, to, event) -> {
                currentLexemes.add(Lexeme.literal(sequence.subSequence(beginOfLexemeIndex, currentCharIndex)));
                beginOfLexemeIndex = currentCharIndex;
                currentCharIndex++;
            }))
            .addTransition(State.SYMBOL, State.FINISHED, CharClass.EOF, ((from, to, event) -> {
                currentLexemes.add(Lexeme.literal(sequence.subSequence(beginOfLexemeIndex, currentCharIndex)));
                beginOfLexemeIndex = currentCharIndex;
            }))
            .addTransition(State.CAPITAL_SYMBOL, State.CAPITAL_SYMBOL, CharClass.CAPITAL_SYMBOL, ((from, to, event) -> {
                currentLexemes.add(Lexeme.literal(sequence.subSequence(beginOfLexemeIndex, currentCharIndex)));
                currentLexemes.add(Lexeme.strictConcatenation());
                beginOfLexemeIndex = currentCharIndex;
                currentCharIndex++;
            }))
            .addTransition(State.CAPITAL_SYMBOL, State.CAPITAL_SYMBOL, CharClass.SYMBOL, ((from, to, event) -> {
                currentCharIndex++;
            }))
            .addTransition(State.CAPITAL_SYMBOL, State.ASTERISK, CharClass.ASTERISK, ((from, to, event) -> {
                currentLexemes.add(Lexeme.literal(sequence.subSequence(beginOfLexemeIndex, currentCharIndex)));
                beginOfLexemeIndex = currentCharIndex;
                currentCharIndex++;
            }))
            .addTransition(State.CAPITAL_SYMBOL, State.FINISHED, CharClass.EOF, ((from, to, event) -> {
                currentLexemes.add(Lexeme.literal(sequence.subSequence(beginOfLexemeIndex, currentCharIndex)));
                beginOfLexemeIndex = currentCharIndex;
            }))
            .build();
    }

    void restart(CharSequence newSequence) {
        Objects.requireNonNull(newSequence);

        sequence = newSequence;
        currentCharIndex = 0;
        beginOfLexemeIndex = 0;

        currentLexemes.clear();
        fsm.reset();
    }

    Lexeme next() {
        if (currentLexemes.isEmpty()) {
            parse();
        }
        return currentLexemes.poll();
    }

    private boolean parse() {
        boolean wasFound = false;
        while (!wasFound && !fsm.isFinished()) {
            int foundLexemesCount = currentLexemes.size();
            fsm.dispatch(getCurrentCharClass());
            wasFound = currentLexemes.size() > foundLexemesCount;
        }
        return wasFound;
    }

    private CharClass getCurrentCharClass() {
        if (sequence == null) {
            throw new IllegalStateException("Scanner has not been initialized with sequence");
        }

        if (currentCharIndex >= sequence.length()) {
            return CharClass.EOF;
        }

        char c = sequence.charAt(currentCharIndex);

        if (c == '*') {
            return CharClass.ASTERISK;
        }

        if (Character.isUpperCase(c)) {
            return CharClass.CAPITAL_SYMBOL;
        }

        return CharClass.SYMBOL;
    }

}
