package lva.patternmatcher.classfinder;

import com.google.common.reflect.ClassPath;
import lva.patternmatcher.MatchingResultSet;
import lva.patternmatcher.MatchingResultSet.Matching;
import lva.patternmatcher.PatternMatcher;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.Ansi.Attribute;
import org.fusesource.jansi.AnsiConsole;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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

        AnsiConsole.systemInstall();

        try (Scanner scanner = new Scanner(System.in)) {

            while (true) {
                print("> ", DEFAULT, INTENSITY_BOLD);
                String pattern = scanner.nextLine();

                print(format("searching for '%s' ... ", pattern), DEFAULT, INTENSITY_BOLD);
                Instant start = Instant.now();
                MatchingResultSet<ClassName> res = matcher.match(pattern);
                Duration searchDuration = Duration.between(start, Instant.now());
                print(format("done%n"), DEFAULT, INTENSITY_BOLD);

                var resultSet = res.getResultSet();

                resultSet.forEach((className, entries) -> {
                    var simpleName = className.getSimpleName();
                    var fullMatching = new Matching(0, simpleName.length());
                    var matchingIntervals = split(fullMatching, entries.getMatchings());

                    for (int i = 0; i < matchingIntervals.size(); i++) {
                        var interval = matchingIntervals.get(i);
                        print(simpleName.substring(interval.getFrom(), interval.getTo()),
                                i % 2 == 0 ? WHITE : RED, INTENSITY_BOLD);
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

    private static List<Matching> split(Matching fullMatching, List<Matching> matchings) {
        var matchingIntervals = new ArrayDeque<Matching>(matchings.size() * 2 + 1);
        matchingIntervals.addLast(fullMatching);

        for (var m : matchings) {
            var last = matchingIntervals.getLast();
            if (m.getTo() <= last.getTo()) {
                matchingIntervals.removeLast();
                matchingIntervals.addLast(new Matching(last.getFrom(), m.getFrom()));
                matchingIntervals.addLast(new Matching(m.getFrom(), m.getTo()));
                matchingIntervals.addLast(new Matching(m.getTo(), last.getTo()));
            }
        }

        return new ArrayList<>(matchingIntervals);
    }

    private static void print(String msg, Ansi.Color fgColor, Attribute attribute) {
        AnsiConsole.out.print(ansi().fg(fgColor).a(attribute).a(msg).reset());
        AnsiConsole.out.flush();
    }
}
