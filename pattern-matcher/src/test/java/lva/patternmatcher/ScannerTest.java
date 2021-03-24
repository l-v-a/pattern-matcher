package lva.patternmatcher;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static lva.patternmatcher.Scanner.Lexeme;
import static lva.patternmatcher.Scanner.Lexeme.concatenation;
import static lva.patternmatcher.Scanner.Lexeme.literal;
import static lva.patternmatcher.Scanner.Lexeme.strictConcatenation;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;

/**
 * @author vlitvinenko
 */
public class ScannerTest {
    private final Scanner scanner = new Scanner();

    @Test
    public void should_return_NULL_lexeme_for_empty_sequence() {
        scanner.restart("");
        assertSame(Lexeme.NULL, scanner.next());
    }

    @Test
    public void should_parse_seq_that_starts_with_asterisks() {
        scanner.restart("**abc");
        assertThat("**abc", list(scanner), is(list(concatenation(), concatenation(), literal("abc"))));
    }

    @Test
    public void should_parse_seq_that_starts_with_only_asterisk() {
        scanner.restart("*");
        assertThat("*", list(scanner), is(list(concatenation())));
    }

    @Test
    public void should_parse_seq_that_starts_with_symbol() {
        scanner.restart("abc");
        assertThat("abc", list(scanner), is(list(literal("abc"))));
    }

    @Test
    public void should_parse_seq_that_starts_with_capital_symbol() {
        scanner.restart("Abc");
        assertThat("Abc", list(scanner), is(list(literal("Abc"))));
    }

    @Test
    public void should_parse_seq_that_ends_with_asterisks() {
        scanner.restart("abc**");
        assertThat("abc**", list(scanner), is(list(literal("abc"), concatenation(), concatenation())));
    }

    @Test
    public void should_parse_seq_that_contains_asterisks() {
        scanner.restart("abc**def");
        assertThat("abc**def", list(scanner), is(list(literal("abc"), concatenation(), concatenation(), literal("def"))));
    }

    @Test
    public void should_parse_seq_that_contains_asterisks_and_second_literal_starts_with_capital_symbol() {
        scanner.restart("abc**Def");
        assertThat("abc**Def", list(scanner), is(list(literal("abc"), concatenation(), concatenation(), literal("Def"))));
    }

    @Test
    public void should_parse_seq_that_contains_asterisks_between_capital_symbols() {
        scanner.restart("abC**Def");
        assertThat("abC**Def", list(scanner), is(list(literal("abC"), concatenation(), concatenation(), literal("Def"))));
    }

    @Test
    public void should_parse_beginning_capital_symbols_as_strict_concatenation() {
        scanner.restart("AbcDef");
        assertThat("AbcDef", list(scanner), is(list(literal("Abc"), strictConcatenation(), literal("Def"))));
    }

    @Test
    public void should_parse_beginning_capital_symbols_as_strict_concatenation_only_for_following_capital_symbols() {
        scanner.restart("abcDefFgh");
        assertThat("abcDefFgh", list(scanner), is(list(literal("abcDef"), strictConcatenation(), literal("Fgh"))));
    }

    @Test
    public void should_parse_beginning_capital_symbols_as_strict_concatenation_only_for_following_capital_symbols_without_asterisks() {
        scanner.restart("Abc*Ghj");
        assertThat("Abc*Ghj", list(scanner), is(list(literal("Abc"), concatenation(), literal("Ghj"))));
    }

    @Test(expected = IllegalStateException.class)
    public void should_throw_if_not_restarted() {
        Scanner s = new Scanner();
        s.next();
    }


    private static List<Lexeme> list(Lexeme... lexemes) {
        return Arrays.asList(lexemes);
    }

    private static List<Lexeme> list(Scanner scanner) {
        List<Lexeme> lexemes = new ArrayList<>();
        Lexeme lexeme = scanner.next();
        while (lexeme != Lexeme.NULL) {
            lexemes.add(lexeme);
            lexeme = scanner.next();
        }
        return lexemes;
    }

}