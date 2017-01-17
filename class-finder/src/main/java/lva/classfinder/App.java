package lva.classfinder;

import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ClassInfo;
import lombok.NonNull;
import lombok.ToString;
import lva.patternmatcher.MatchingResultSet;
import lva.patternmatcher.PatternMatcher;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;


@ToString
class ClassName implements CharSequence, Comparable<ClassName> {
    private final String simpleName;
    private final String packageName;

    ClassName(@NonNull ClassInfo classInfo) {
        this.simpleName = classInfo.getSimpleName();
        this.packageName = classInfo.getPackageName();
    }


    @Override
    public int length() {
        return simpleName.length();
    }

    @Override
    public char charAt(int index) {
        return simpleName.charAt(index);
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return simpleName.subSequence(start, end);
    }

    @Override
    public int compareTo(ClassName className) {
        int res = simpleName.compareTo(className.simpleName);
        if (res == 0) {
            res = packageName.compareTo(className.packageName);
        }
        return res;
    }
}

/**
 * @author vlitvinenko
 */
public class App {
    public static void main(String ... args) throws IOException {
        ClassPath classPath = ClassPath.from(App.class.getClassLoader());
        Stream<ClassName> classNames = classPath.getTopLevelClasses().stream()
            .map(ClassName::new);

        System.out.print("loading...");
        PatternMatcher<ClassName> matcher = new PatternMatcher<>(classNames);
        System.out.println("done");

        MatchingResultSet<ClassName> res = matcher.match("Date ");
        Map<ClassName, MatchingResultSet.MatchingEntries> resultSet = res.getResultSet();

        resultSet.forEach((className, entries) -> {
            System.out.println(className);
            List<MatchingResultSet.Matching> matchings = entries.getMatchings();
            matchings.forEach(m -> System.out.println(m.getFrom() + ", " + m.getTo()));
            System.out.println("-------------");
        });
    }
}
