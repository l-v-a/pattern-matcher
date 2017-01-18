package lva.patternmatcher;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author vlitvinenko
 */
public class PatternCommandTokenizerImplTest {
    private final PatternCommandTokenizer parser = new PatternCommandTokenizerImpl();

    @Before
    public void setUp() {

    }

    @Test
    public void should_tokenize_begin() {
        parser.restart("abc");
        PatternCommandTokenizer.Command command = parser.nextCommand();
        assertTrue(command instanceof BeginPatternCommand);
    }

    @Test
    public void should_return_null_as_last_command() {
        parser.restart("abc");
        PatternCommandTokenizer.Command command = parser.nextCommand();
        assertTrue(command instanceof BeginPatternCommand);

        command = parser.nextCommand();
        assertNull(command);
    }



    @Test
    public void should_tokenize_begin_any() {
        parser.restart("*abc");
        PatternCommandTokenizer.Command command = parser.nextCommand();
        assertTrue(command instanceof BeginAnyPatternCommand);
    }

    @Test
    public void should_tokenize_begin_any_with_many_asterisks() {
        parser.restart("**abc");
        PatternCommandTokenizer.Command command = parser.nextCommand();
        assertTrue(command instanceof BeginAnyPatternCommand);
    }

    @Test
    public void should_tokenize_expression_any() {
        parser.restart("abc*def");
        PatternCommandTokenizer.Command command = parser.nextCommand();
        assertTrue(command instanceof BeginPatternCommand);

        command = parser.nextCommand();
        assertTrue(command instanceof ExpressionAnyPatternCommand);
    }

    @Test
    public void should_tokenize_expression_any_with_many_asterisks() {
        parser.restart("abc**def");

        PatternCommandTokenizer.Command command = parser.nextCommand();
        assertTrue(command instanceof BeginPatternCommand);

        command = parser.nextCommand();
        assertTrue(command instanceof ExpressionAnyPatternCommand);    }

    @Test
    public void should_tokenize_expression_strict() {
        parser.restart("AbcDef");

        PatternCommandTokenizer.Command command = parser.nextCommand();
        assertTrue(command instanceof BeginPatternCommand);

        command = parser.nextCommand();
        assertTrue(command instanceof ExpressionStrictPatternCommand);

    }

    @Test
    public void should_tokenize_expression_only_any() {
        parser.restart("*");

        PatternCommandTokenizer.Command command = parser.nextCommand();
        assertTrue(command instanceof BeginAnyPatternCommand);

        command = parser.nextCommand();
        assertNull(command);
    }

}