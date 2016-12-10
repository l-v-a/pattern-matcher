package lva.patternmatcher;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author vlitvinenko
 */

public class FiniteStateMachine<S, E> {
    private final Map<S, State<S, E>> states;
    private final State<S, E> initialState;
    private final State<S, E> finishedState;
    private State<S, E> currentState;

    @FunctionalInterface
    public interface TransitionFunction<S, E> {
        void apply(S from, S to, E event);
    }

    private static class State<S, E> {
        final S state;
        final Map<E, Event<S, E>> events = new HashMap<>();

        State(S state) {
            this.state = state;
        }
    }

    private static class Event<S, E> {
        final State<S, E> targetState;
        final TransitionFunction<S, E> beforeStateChanged;

        Event(State<S, E> targetState, TransitionFunction<S, E> beforeStateChanged) {
            this.targetState = targetState;
            this.beforeStateChanged = beforeStateChanged;
        }
    }

    private FiniteStateMachine(Map<S, State<S, E>> states, State<S, E> initialState, State<S, E> finishedState) {
        Objects.requireNonNull(states);
        Objects.requireNonNull(initialState);
        Objects.requireNonNull(finishedState);

        this.states = states;
        this.initialState = initialState;
        this.finishedState = finishedState;
        this.currentState = this.initialState;
    }

    public void dispatch(E e) {
        Objects.requireNonNull(e);

        Event<S, E> event = currentState.events.get(e);
        if (event == null) {
            throw new IllegalArgumentException(String.format("Unexpected event %s for state %s", e, currentState.state));
        }

        event.beforeStateChanged.apply(currentState.state, event.targetState.state, e);
        currentState = event.targetState;
    }

    public boolean isFinished() {
        return currentState.state.equals(finishedState.state);
    }

    public S getCurrentState() {
        return currentState.state;
    }

    public void reset() {
        currentState = initialState;
    }

    public static class Builder<S, E> {
        private final Map<S, State<S, E>> states = new HashMap<>();
        private State<S, E> initialState;
        private State<S, E> finishState;

        public Builder<S, E> addTransition(S from, S to, E e, TransitionFunction<S, E> beforeStateChanged) {
            Objects.requireNonNull(from);
            Objects.requireNonNull(to);
            Objects.requireNonNull(e);
            Objects.requireNonNull(beforeStateChanged);

            State<S, E> fromState = states.computeIfAbsent(from, (k) -> new State<>(from));
            State<S, E> toState = states.computeIfAbsent(to, (k) -> new State<>(to));

            Event<S, E> event = new Event<>(toState, beforeStateChanged);
            fromState.events.put(e, event);
            return this;
        }

        public Builder<S, E> setInitialState(S state) {
            Objects.requireNonNull(state);
            initialState = states.computeIfAbsent(state, (k) -> new State<>(state));
            return this;
        }

        public Builder<S, E> setFinishedState(S state) {
            Objects.requireNonNull(state);
            finishState = states.computeIfAbsent(state, (k) -> new State<>(state));
            return this;
        }

        public FiniteStateMachine<S, E> build() {
            return new FiniteStateMachine<>(states, initialState, finishState);
        }
    }
}
