package lva.classfinder;

import com.diogonunes.jcdp.color.ColoredPrinter;
import com.diogonunes.jcdp.color.api.Ansi;
import com.diogonunes.jcdp.color.api.Ansi.Attribute;
import com.diogonunes.jcdp.color.api.Ansi.BColor;
import com.diogonunes.jcdp.color.api.Ansi.FColor;
import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ClassInfo;
import lombok.NonNull;
import lombok.ToString;
import lombok.Getter;
import lva.patternmatcher.MatchingResultSet;
import lva.patternmatcher.PatternMatcher;

import java.io.Console;
import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Stream;


/**
 * @author vlitvinenko
 */
public class App {
    public static void main(String ... args) throws IOException {
        ClassPath classPath = ClassPath.from(App.class.getClassLoader());
        Stream<ClassName> classNames = classPath.getTopLevelClasses().stream()
            .map(ClassName::new);

        System.out.print("loading ... ");
        PatternMatcher<ClassName> matcher = new PatternMatcher<>(classNames);
        System.out.println("done");

        Scanner scanner = new Scanner(System.in);
        ColoredPrinter printer = new ColoredPrinter.Builder(1, false)
            .build();

        try {

            while (true) {
                System.out.print("> ");
                String pattern = scanner.nextLine();

                Instant start = Instant.now();
                System.out.printf("searching for '%s' ... ", pattern);
                MatchingResultSet<ClassName> res = matcher.match(pattern);
                System.out.println("done in ms: " + Duration.between(start, Instant.now()).toMillis());

                Map<ClassName, MatchingResultSet.MatchingEntries> resultSet = res.getResultSet();

                resultSet.forEach((className, entries) -> {
                    printer.setAttribute(Attribute.LIGHT);

                    String simpleName = className.getSimpleName();
                    int from = 0;

                    for (MatchingResultSet.Matching m : entries.getMatchings()) {
                        printer.setForegroundColor(FColor.WHITE);
                        printer.print(simpleName.substring(from, m.getFrom()));
                        printer.setForegroundColor(FColor.RED);

                        from = Math.min(m.getTo(), simpleName.length());
                        printer.print(simpleName.substring(m.getFrom(), from));

                    }

                    if (from < simpleName.length()) {
                        printer.setForegroundColor(FColor.WHITE);
                        printer.print(simpleName.substring(from, simpleName.length()));
                    }

                    printer.clear();

                    printer.setAttribute(Attribute.DARK);
                    printer.setForegroundColor(FColor.WHITE);
                    printer.print(String.format(" (%s)%n", className.getPackageName()));

                    printer.clear();
                });
            }
        } finally {
            printer.clear();
        }
    }
}
