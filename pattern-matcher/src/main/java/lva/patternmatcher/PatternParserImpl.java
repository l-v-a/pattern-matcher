package lva.patternmatcher;

import lva.patternmatcher.Scanner.Lexeme;

import java.util.Objects;

/**
 * @author vlitvinenko
 */
class PatternParserImpl implements PatternParser {

    private static final EventListener NULL_LISTENER = new EventListener() {
        @Override
        public void beginParsed(CharSequence sequence) {}

        @Override
        public void beginAnyParsed(CharSequence sequence) {}

        @Override
        public void expressionAnyParsed(CharSequence sequence) {}

        @Override
        public void expressionStrictParsed(CharSequence sequence) {}
    };

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
    private EventListener listener = NULL_LISTENER;

    PatternParserImpl() {
        this.fsm = new FiniteStateMachine.Builder<State, Lexeme.Type>()
            .setInitialState(State.INITIAL)
            .addTransition(State.INITIAL, State.BEGIN, Lexeme.Type.LITERAL, ((from, to, event) -> {
                listener.beginParsed(currentLexeme.getValue());
            }))
            .addTransition(State.INITIAL, State.BEGIN_ANY, Lexeme.Type.CONCATENATION)
            .addTransition(State.BEGIN, State.EXP_ANY, Lexeme.Type.CONCATENATION)
            .addTransition(State.BEGIN, State.EXP_STRICT, Lexeme.Type.STRICT_CONCATENATION)
            .addTransition(State.BEGIN_ANY, State.BEGIN, Lexeme.Type.LITERAL, ((from, to, event) -> {
                listener.beginAnyParsed(currentLexeme.getValue());
            }))
            .addTransition(State.BEGIN_ANY, State.BEGIN_ANY, Lexeme.Type.CONCATENATION)
            .addTransition(State.EXP_ANY, State.EXP_ANY, Lexeme.Type.CONCATENATION)
            .addTransition(State.EXP_ANY, State.BEGIN, Lexeme.Type.LITERAL, ((from, to, event) -> {
                listener.expressionAnyParsed(currentLexeme.getValue());
            }))
            .addTransition(State.EXP_STRICT, State.BEGIN, Lexeme.Type.LITERAL, ((from, to, event) -> {
                listener.expressionStrictParsed(currentLexeme.getValue());
            }))
            .build();
    }

    @Override
    public void setEventListener(EventListener listener) {
        this.listener = listener == null ? NULL_LISTENER: listener;
    }

    @Override
    public void parse(CharSequence pattern) {
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
