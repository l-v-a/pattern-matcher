package lva.patternmatcher;

import lva.patternmatcher.Scanner.Lexeme;

import java.util.Objects;

/**
 * @author vlitvinenko
 */
class PatternParser {

    interface Events {
        void onBegin(CharSequence sequence);
        void onBeginAny(CharSequence sequence);
        void onExpressionAny(CharSequence sequence);
        void onExpressionStrict(CharSequence sequence);
    }

    private enum State {
        INITIAL,
        BEGIN,
        BEGIN_ANY,
        EXP_ANY,
        EXP_STRICT
    }

    private final Scanner scanner = new Scanner();
    private final FiniteStateMachine<State, Lexeme.Type> fsm;
    private Lexeme currentLexeme;

    PatternParser(Events events) {
        Objects.requireNonNull(events);

        this.fsm = new FiniteStateMachine.Builder<State, Lexeme.Type>()
            .setInitialState(State.INITIAL)
            .addTransition(State.INITIAL, State.BEGIN, Lexeme.Type.LITERAL, ((from, to, event) -> {
                events.onBegin(currentLexeme.getValue());
            }))
            .addTransition(State.INITIAL, State.BEGIN_ANY, Lexeme.Type.CONCATENATION)
            .addTransition(State.BEGIN, State.EXP_ANY, Lexeme.Type.CONCATENATION)
            .addTransition(State.BEGIN, State.EXP_STRICT, Lexeme.Type.STRICT_CONCATENATION)
            .addTransition(State.BEGIN_ANY, State.BEGIN, Lexeme.Type.LITERAL, ((from, to, event) -> {
                events.onBeginAny(currentLexeme.getValue());
            }))
            .addTransition(State.BEGIN_ANY, State.BEGIN_ANY, Lexeme.Type.CONCATENATION)
            .addTransition(State.EXP_ANY, State.EXP_ANY, Lexeme.Type.CONCATENATION)
            .addTransition(State.EXP_ANY, State.BEGIN, Lexeme.Type.LITERAL, ((from, to, event) -> {
                events.onExpressionAny(currentLexeme.getValue());
            }))
            .addTransition(State.EXP_STRICT, State.BEGIN, Lexeme.Type.LITERAL, ((from, to, event) -> {
                events.onExpressionStrict(currentLexeme.getValue());
            }))
            .build();
    }

    void parse(String pattern) {
        Objects.requireNonNull(pattern);

        scanner.restart(pattern);
        currentLexeme = scanner.next();

        while (currentLexeme != null) {
            fsm.dispatch(currentLexeme.getType());
            currentLexeme = scanner.next();
        }

        if (fsm.getCurrentState() == State.EXP_STRICT) {
            throw new IllegalStateException("Unexpected state at EOF: " + fsm.getCurrentState());
        }
    }
}
