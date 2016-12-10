package lva.patternmatcher;

import org.junit.Before;
import org.junit.Test;

import javax.management.RuntimeMBeanException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;


/**
 * @author vlitvinenko
 */
public class FiniteStateMachineTest {
    enum State {
        S1, S2, S3;
    }

    enum Event {
        S1_TO_S2, S1_TO_S3, S2_TO_S3;
    }

    interface EventSink {
        void onStateChanged(State from, State to, Event event);
    }

    private FiniteStateMachine<State, Event> fsm;
    private EventSink eventSink;

    @Before
    public void setUp() {
        eventSink = mock(EventSink.class);
        fsm = new FiniteStateMachine.Builder<State, Event>()
            .addTransition(State.S1, State.S2, Event.S1_TO_S2, eventSink::onStateChanged)
            .addTransition(State.S1, State.S3, Event.S1_TO_S3, eventSink::onStateChanged)
            .addTransition(State.S2, State.S3, Event.S2_TO_S3, eventSink::onStateChanged)
            .setInitialState(State.S1)
            .setFinishedState(State.S2)
            .build();
    }

    @Test
    public void should_transit_to_to_state() {
        assertEquals(State.S1, fsm.getCurrentState());
        fsm.dispatch(Event.S1_TO_S2);
        assertEquals(State.S2, fsm.getCurrentState());
    }

    @Test
    public void should_transit_to_initial_state_after_resetting() {
        fsm.dispatch(Event.S1_TO_S2);
        assertEquals(State.S2, fsm.getCurrentState());

        fsm.reset();
        assertEquals(State.S1, fsm.getCurrentState());
    }

    @Test(expected = IllegalArgumentException.class)
    public void should_throw_for_unexpected_event() {
        assertEquals(State.S1, fsm.getCurrentState());
        fsm.dispatch(Event.S2_TO_S3);
    }

    @Test
    public void should_call_transition_function() {
        assertEquals(State.S1, fsm.getCurrentState());
        fsm.dispatch(Event.S1_TO_S2);
        verify(eventSink).onStateChanged(eq(State.S1), eq(State.S2), eq(Event.S1_TO_S2));
    }

    @Test
    public void should_not_change_state_if_event_sink_throws() {
        assertEquals(State.S1, fsm.getCurrentState());
        doThrow(new IndexOutOfBoundsException()).when(eventSink).onStateChanged(any(), any(), any());

        try {
            fsm.dispatch(Event.S1_TO_S2);
        } catch (IndexOutOfBoundsException ignored) {}

        assertEquals(State.S1, fsm.getCurrentState());
    }


}