package lva.patternmatcher;

import lombok.NonNull;
import lva.patternmatcher.Scanner.Lexeme;

import java.util.Objects;

/**
 * @author vlitvinenko
 */
class PatternCommandTokenizerImpl implements PatternCommandTokenizer {
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
    private Command currentCommand;

    PatternCommandTokenizerImpl() {
        this.fsm = new FiniteStateMachine.Builder<State, Lexeme.Type>()
            .setInitialState(State.INITIAL)
            .addTransition(State.INITIAL, State.BEGIN, Lexeme.Type.LITERAL, ((from, to, event) -> {
                currentCommand = new BeginPatternCommand(currentLexeme.getValue());
            }))
            .addTransition(State.INITIAL, State.BEGIN_ANY, Lexeme.Type.CONCATENATION)
            .addTransition(State.BEGIN, State.EXP_ANY, Lexeme.Type.CONCATENATION)
            .addTransition(State.BEGIN, State.EXP_STRICT, Lexeme.Type.STRICT_CONCATENATION)
            .addTransition(State.BEGIN_ANY, State.BEGIN, Lexeme.Type.LITERAL, ((from, to, event) -> {
                currentCommand = new BeginAnyPatternCommand(currentLexeme.getValue());
            }))
            .addTransition(State.BEGIN_ANY, State.BEGIN_ANY, Lexeme.Type.CONCATENATION)
            .addTransition(State.EXP_ANY, State.EXP_ANY, Lexeme.Type.CONCATENATION)
            .addTransition(State.EXP_ANY, State.BEGIN, Lexeme.Type.LITERAL, ((from, to, event) -> {
                currentCommand = new ExpressionAnyPatternCommand(currentLexeme.getValue());
            }))
            .addTransition(State.EXP_STRICT, State.BEGIN, Lexeme.Type.LITERAL, ((from, to, event) -> {
                currentCommand = new ExpressionStrictPatternCommand(currentLexeme.getValue());
            }))
            .build();
    }

    @Override
    public void restart(@NonNull CharSequence pattern) {
        scanner.restart(pattern);
        fsm.reset();

        currentCommand = null;
        currentLexeme = scanner.next();

        tokenize();
    }

    @Override
    public Command nextCommand() {
        Command command = this.currentCommand;
        tokenize();
        return command;
    }

    private void tokenize() {
        Command prevCommand = currentCommand;

        while (currentLexeme != null && prevCommand == currentCommand) {
            fsm.dispatch(currentLexeme.getType());
            currentLexeme = scanner.next();
        }

        if (prevCommand == currentCommand) {
            currentCommand = null;
        }

        if (fsm.getCurrentState() == State.EXP_STRICT) {
            throw new IllegalStateException("Unexpected state at EOF: " + fsm.getCurrentState());
        }
    }
}
