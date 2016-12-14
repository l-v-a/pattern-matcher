package lva.patternmatcher;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * @author vlitvinenko
 */
public class PatternParserTest {
    private PatternParser parser;
    private PatternParser.Events evetnsSink;

    @Before
    public void setUp() {
        evetnsSink = mock(PatternParser.Events.class);
        parser = new PatternParser(evetnsSink);
    }

    @Test
    public void should_parse_begin() {
        parser.parse("abc");
        verify(evetnsSink).onBegin(eq("abc"));
    }

    @Test
    public void should_parse_begin_any() {
        parser.parse("*abc");
        verify(evetnsSink).onBeginAny(eq("abc"));
    }

    @Test
    public void should_parse_begin_any_with_many_asterisks() {
        parser.parse("**abc");
        verify(evetnsSink).onBeginAny(eq("abc"));
    }

    @Test
    public void should_parse_expression_any() {
        parser.parse("abc*def");
        verify(evetnsSink).onExpressionAny(eq("def"));
    }

    @Test
    public void should_parse_expression_any_with_many_asterisks() {
        parser.parse("abc**def");
        verify(evetnsSink).onExpressionAny(eq("def"));
    }

    @Test
    public void should_parse_expression_strict() {
        parser.parse("AbcDef");
        verify(evetnsSink).onExpressionStrict(eq("Def"));
    }

}