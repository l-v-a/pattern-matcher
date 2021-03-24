package lva.patternmatcher.classfinder;

import com.google.common.reflect.ClassPath;
import lva.patternmatcher.MatchingResultSet;
import lva.patternmatcher.PatternMatcher;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.Ansi.Attribute;
import org.fusesource.jansi.AnsiConsole;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.Map;
import java.util.Scanner;

import static java.lang.String.format;
import static java.util.stream.Collectors.toList;
import static org.fusesource.jansi.Ansi.Attribute.INTENSITY_BOLD;
import static org.fusesource.jansi.Ansi.Attribute.INTENSITY_BOLD_OFF;
import static org.fusesource.jansi.Ansi.Color.DEFAULT;
import static org.fusesource.jansi.Ansi.Color.RED;
import static org.fusesource.jansi.Ansi.Color.WHITE;
import static org.fusesource.jansi.Ansi.ansi;


/**
 * @author vlitvinenko
 */
public class App {
    public static void main(String... args) throws IOException {
        ClassPath classPath = ClassPath.from(App.class.getClassLoader());
        Collection<ClassName> classNames = classPath.getTopLevelClasses().stream()
            .map(ClassName::new).collect(toList());

        System.out.print("loading ... ");
        PatternMatcher<ClassName> matcher = new PatternMatcher<>(classNames);
        System.out.println("done");

        Scanner scanner = new Scanner(System.in);

        AnsiConsole.systemInstall();

        try {

            while (true) {
                print("> ", DEFAULT, INTENSITY_BOLD);
                String pattern = scanner.nextLine();

                print(format("searching for '%s' ... ", pattern), DEFAULT, INTENSITY_BOLD);
                Instant start = Instant.now();
                MatchingResultSet<ClassName> res = matcher.match(pattern);
                Duration searchDuration = Duration.between(start, Instant.now());
                print(format("done%n"), DEFAULT, INTENSITY_BOLD);

                Map<ClassName, MatchingResultSet.MatchingEntries> resultSet = res.getResultSet();

                resultSet.forEach((className, entries) -> {
                    String simpleName = className.getSimpleName();
                    int from = 0;

                    for (MatchingResultSet.Matching m : entries.getMatchings()) {
                        print(simpleName.substring(from, m.getFrom()), WHITE, INTENSITY_BOLD);

                        from = Math.min(m.getTo(), simpleName.length());
                        print(simpleName.substring(m.getFrom(), from), RED, INTENSITY_BOLD);
                    }

                    if (from < simpleName.length()) {
                        print(simpleName.substring(from), WHITE, INTENSITY_BOLD);
                    }

                    print(format(" (%s)%n", className.getPackageName()), WHITE, INTENSITY_BOLD_OFF);
                });

                print(format("%nsearching time: %d ms%nfound: %d%n",
                    searchDuration.toMillis(), resultSet.size()), DEFAULT, INTENSITY_BOLD);

            }
        } finally {
            AnsiConsole.systemUninstall();
        }
    }

    private static void print(String msg, Ansi.Color fgColor, Attribute attribute) {
        AnsiConsole.out.print(ansi().fg(fgColor).a(attribute).a(msg).reset());
        AnsiConsole.out.flush();
    }
}
