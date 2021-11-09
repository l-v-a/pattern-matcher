package lva.patternmatcher.classfinder

import com.google.common.reflect.ClassPath
import lva.patternmatcher.MatchingResultSet.Matching
import lva.patternmatcher.PatternMatcher
import org.fusesource.jansi.Ansi
import org.fusesource.jansi.Ansi.Attribute.INTENSITY_BOLD
import org.fusesource.jansi.Ansi.Attribute.INTENSITY_BOLD_OFF
import org.fusesource.jansi.Ansi.Color.DEFAULT
import org.fusesource.jansi.Ansi.Color.RED
import org.fusesource.jansi.Ansi.Color.WHITE
import org.fusesource.jansi.AnsiConsole
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

/**
 * @author vlitvinenko
 */
@ExperimentalTime
fun main() {
    val classPath = ClassPath.from(ClassLoader.getSystemClassLoader())
    val classNames: Collection<ClassName> = classPath.topLevelClasses.map { ClassName(it) }.toList()

    print("loading ... ")
    val matcher = PatternMatcher(classNames)
    println("done")

    AnsiConsole.systemInstall()
    try {
        while (true) {
            print("> ", DEFAULT, INTENSITY_BOLD)
            val pattern = readLine()

            print("searching for '$pattern' ... ", DEFAULT, INTENSITY_BOLD)
            val (res, searchDuration) = measureTimedValue { matcher.match(pattern) }
            print("done\n", DEFAULT, INTENSITY_BOLD)

            val resultSet = res.resultSet

            resultSet.forEach { (className, entries) ->
                val simpleName = className.simpleName
                val fullMatching = Matching(0, simpleName.length)
                val matchingIntervals = split(fullMatching, entries.matchings)

                matchingIntervals.forEachIndexed { i, interval ->
                    print(simpleName.substring(interval.from, interval.to),
                        if (i % 2 == 0) WHITE else RED, INTENSITY_BOLD)
                }

                print(" (${className.packageName})\n", WHITE, INTENSITY_BOLD_OFF)
            }

            print("\nsearching time: ${searchDuration.inWholeMilliseconds} ms\nfound: ${resultSet.size}\n",
                DEFAULT, INTENSITY_BOLD)
        }
    } finally {
        AnsiConsole.systemUninstall()
    }
}

private fun split(fullMatching: Matching, matchings: List<Matching>): List<Matching> {
    val matchingIntervals = ArrayList<Matching>(matchings.size * 2 + 1).apply { add(fullMatching) }

    for (m in matchings) {
        val last = matchingIntervals.last()
        if (m.to <= last.to) {
            matchingIntervals -= last
            matchingIntervals += Matching(last.from, m.from)
            matchingIntervals += Matching(m.from, m.to)
            matchingIntervals += Matching(m.to, last.to)
        }
    }

    return matchingIntervals
}

private fun print(msg: String, fgColor: Ansi.Color, attribute: Ansi.Attribute) {
    AnsiConsole.out.print(Ansi.ansi().fg(fgColor).a(attribute).a(msg).reset())
    AnsiConsole.out.flush()
}

private class ClassName(private val classInfo: ClassPath.ClassInfo) :
    CharSequence by classInfo.simpleName,
    Comparable<ClassName> {

    val simpleName: String by classInfo::simpleName
    val packageName: String by classInfo::packageName
    private val fullName = simpleName + packageName

    override operator fun compareTo(other: ClassName) =
        fullName.compareTo(other.fullName)
}
