package lva.patternmatcher;

import lombok.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Provides finite state machine implementation.
 *
 * @author vlitvinenko
 */

class FiniteStateMachine<S, E> {
    private final Map<S, State<S, E>> states;
    private final State<S, E> initialState;
    private final State<S, E> finishedState;
    private State<S, E> currentState;

    @FunctionalInterface
    interface TransitionFunction<S, E> {
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

    private FiniteStateMachine(@NonNull Map<S, State<S, E>> states, @NonNull State<S, E> initialState, State<S, E> finishedState) {
        this.states = states;
        this.initialState = initialState;
        this.finishedState = finishedState;
        this.currentState = this.initialState;
    }

    void dispatch(@NonNull E e) {
        Event<S, E> event = currentState.events.get(e);
        if (event == null) {
            throw new IllegalArgumentException(String.format("Unexpected event %s for state %s", e, currentState.state));
        }

        event.beforeStateChanged.apply(currentState.state, event.targetState.state, e);
        currentState = event.targetState;
    }

    boolean isFinished() {
        return currentState.state.equals(finishedState.state);
    }

    S getCurrentState() {
        return currentState.state;
    }

    void reset() {
        currentState = initialState;
    }

    static class Builder<S, E> {
        private final Map<S, State<S, E>> states = new HashMap<>();
        private State<S, E> initialState;
        private State<S, E> finishState;

        Builder<S, E> addTransition(@NonNull S from, @NonNull S to, @NonNull E e, @NonNull TransitionFunction<S, E> beforeStateChanged) {
            State<S, E> fromState = states.computeIfAbsent(from, (k) -> new State<>(from));
            State<S, E> toState = states.computeIfAbsent(to, (k) -> new State<>(to));

            Event<S, E> event = new Event<>(toState, beforeStateChanged);
            fromState.events.put(e, event);
            return this;
        }

        Builder<S, E> addTransition(S from, S to, E e) {
            addTransition(from, to, e, (f, t, ev) -> {});
            return this;
        }

        Builder<S, E> setInitialState(@NonNull S state) {
            initialState = states.computeIfAbsent(state, (k) -> new State<>(state));
            return this;
        }

        Builder<S, E> setFinishedState(@NonNull S state) {
            finishState = states.computeIfAbsent(state, (k) -> new State<>(state));
            return this;
        }

        FiniteStateMachine<S, E> build() {
            return new FiniteStateMachine<>(states, initialState, finishState);
        }
    }
}
