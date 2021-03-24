package lva.patternmatcher;

import lombok.NonNull;
import lva.patternmatcher.Scanner.Lexeme;

/**
 * FSM-based pattern tokenizer implementation.
 *
 * @author vlitvinenko
 */
class PatternCommandTokenizerImpl implements PatternCommandTokenizer {
    private enum State {
        INITIAL,
        BEGIN,
        BEGIN_ANY,
        EXP_ANY,
        EXP_STRICT,
        FINISHED
    }

    private final Scanner scanner = new Scanner();
    private final FiniteStateMachine<State, Lexeme.Type> fsm;
    private Lexeme currentLexeme;
    private Command currentCommand;

    PatternCommandTokenizerImpl() {
        this.fsm = new FiniteStateMachine.Builder<State, Lexeme.Type>()
            .setInitialState(State.INITIAL)
            .setFinishedState(State.FINISHED)
            .addTransition(State.INITIAL, State.BEGIN, Lexeme.Type.LITERAL, (from, to, event) -> {
                currentCommand = new BeginPatternCommand(currentLexeme.getValue());
            })
            .addTransition(State.INITIAL, State.BEGIN_ANY, Lexeme.Type.CONCATENATION)
            .addTransition(State.BEGIN, State.EXP_ANY, Lexeme.Type.CONCATENATION)
            .addTransition(State.BEGIN, State.EXP_STRICT, Lexeme.Type.STRICT_CONCATENATION)
            .addTransition(State.BEGIN_ANY, State.BEGIN, Lexeme.Type.LITERAL, (from, to, event) -> {
                currentCommand = new BeginAnyPatternCommand(currentLexeme.getValue());
            })
            .addTransition(State.BEGIN_ANY, State.BEGIN_ANY, Lexeme.Type.CONCATENATION)
            .addTransition(State.EXP_ANY, State.EXP_ANY, Lexeme.Type.CONCATENATION)
            .addTransition(State.EXP_ANY, State.BEGIN, Lexeme.Type.LITERAL, (from, to, event) -> {
                currentCommand = new ExpressionAnyPatternCommand(currentLexeme.getValue());
            })
            .addTransition(State.EXP_STRICT, State.BEGIN, Lexeme.Type.LITERAL, (from, to, event) -> {
                currentCommand = new ExpressionStrictPatternCommand(currentLexeme.getValue());
            })
            .addTransition(State.INITIAL, State.FINISHED, Lexeme.Type.NULL)
            .addTransition(State.BEGIN, State.FINISHED, Lexeme.Type.NULL)
            .addTransition(State.BEGIN_ANY, State.FINISHED, Lexeme.Type.NULL, (from, to, event) -> {
                currentCommand = new BeginAnyPatternCommand("");
            })
            .addTransition(State.EXP_ANY, State.FINISHED, Lexeme.Type.NULL)
            .addTransition(State.EXP_STRICT, State.FINISHED, Lexeme.Type.NULL)
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

        while (!fsm.isFinished() && prevCommand == currentCommand) {
            fsm.dispatch(currentLexeme.getType());
            currentLexeme = scanner.next();
        }

        if (prevCommand == currentCommand) {
            currentCommand = null;
        }
    }
}
