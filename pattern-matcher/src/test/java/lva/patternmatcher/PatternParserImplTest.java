package lva.patternmatcher;

import org.junit.Before;
import org.junit.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * @author vlitvinenko
 */
public class PatternParserImplTest {
    private final PatternParser parser = new PatternParserImpl();
    private PatternParser.EventListener eventListener;

    @Before
    public void setUp() {
        eventListener = mock(PatternParser.EventListener.class);
        parser.setEventListener(eventListener);
    }

    @Test
    public void should_parse_begin() {
        parser.parse("abc");
        verify(eventListener).beginParsed(eq("abc"));
    }

    @Test
    public void should_parse_begin_any() {
        parser.parse("*abc");
        verify(eventListener).beginAnyParsed(eq("abc"));
    }

    @Test
    public void should_parse_begin_any_with_many_asterisks() {
        parser.parse("**abc");
        verify(eventListener).beginAnyParsed(eq("abc"));
    }

    @Test
    public void should_parse_expression_any() {
        parser.parse("abc*def");
        verify(eventListener).expressionAnyParsed(eq("def"));
    }

    @Test
    public void should_parse_expression_any_with_many_asterisks() {
        parser.parse("abc**def");
        verify(eventListener).expressionAnyParsed(eq("def"));
    }

    @Test
    public void should_parse_expression_strict() {
        parser.parse("AbcDef");
        verify(eventListener).beginParsed(eq("Abc"));
        verify(eventListener).expressionStrictParsed(eq("Def"));
    }

    @Test
    public void should_not_generate_events_for_null_listener() {
        parser.setEventListener(null);

        parser.parse("AbcDef*abcA*");
        verify(eventListener, never()).beginParsed(any());
        verify(eventListener, never()).expressionAnyParsed(any());
        verify(eventListener, never()).expressionStrictParsed(any());
    }

    @Test
    public void should_not_generate_events_for_null_listener_for_begin_any() {
        parser.setEventListener(null);
        parser.parse("*abc");
        verify(eventListener, never()).beginAnyParsed(any());
    }
}