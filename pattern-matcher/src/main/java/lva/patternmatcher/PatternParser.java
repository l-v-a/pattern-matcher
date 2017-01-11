package lva.patternmatcher;

/**
 * @author vlitvinenko
 */
interface PatternParser {

    interface EventListener {
        void beginParsed(CharSequence sequence);
        void beginAnyParsed(CharSequence sequence);
        void expressionAnyParsed(CharSequence sequence);
        void expressionStrictParsed(CharSequence sequence);
    }

    void setEventListener(EventListener listener);
    void parse(CharSequence pattern);
}
