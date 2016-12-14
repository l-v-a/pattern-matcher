package lva.patternmatcher;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

import lva.patternmatcher.FiniteStateMachineTest.State;
import lva.patternmatcher.FiniteStateMachineTest.Event;
import lva.patternmatcher.FiniteStateMachineTest.EventSink;
import org.junit.Test;

/**
 * @author vlitvinenko
 */
public class FiniteStateMachineBuilderTest {
    private EventSink eventSink = mock(EventSink.class);

    @Test
    public void should_build_with_initial_state() {
        State initialState = State.S1;
        FiniteStateMachine<State, Event> fsm = new FiniteStateMachine.Builder<State, Event>()
            .addTransition(State.S1, State.S2, Event.S1_TO_S2, eventSink::onStateChanged)
            .setInitialState(initialState)
            .setFinishedState(State.S2)
            .build();

        assertEquals(initialState, fsm.getCurrentState());
    }

    @Test(expected = NullPointerException.class)
    public void should_trow_if_initial_state_not_specified() {
        FiniteStateMachine<State, Event> fsm = new FiniteStateMachine.Builder<State, Event>()
            .addTransition(State.S1, State.S2, Event.S1_TO_S2, eventSink::onStateChanged)
            .setFinishedState(State.S2)
            .build();
    }
}